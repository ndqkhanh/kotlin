/* eslint-disable no-console */
/* eslint-disable no-nested-ternary */
/* eslint-disable prettier/prettier */
/* eslint-disable no-plusplus */
/* eslint-disable no-await-in-loop */
// const phantomPath = require('witch')('phantomjs-prebuilt', 'phantomjs');
// import chromium from 'chrome-aws-lambda';
// const chromium = require('chrome-aws-lambda');
// if (process.env.AWS_LAMBDA_FUNCTION_VERSION) {
//   // running on the Vercel platform.
//   chrome = require('chrome-aws-lambda');
//   puppeteer = require('puppeteer-core');
// } else {
//   // running locally.

// }
// const pdf = require('pdf-creator-node');
const httpStatus = require('http-status');
const { PrismaClient } = require('@prisma/client');
const { ERROR_MESSAGE } = require('../constants/ticket.constant');
const ApiError = require('../utils/ApiError');

const prisma = new PrismaClient();

const payTicket = async (userId, tId) => {

  const ticketCheck = await prisma.bus_tickets.findFirst({
    where: {
      id: tId,
      user_id: userId,
    },
  });
  if (!ticketCheck) {
    throw new ApiError(httpStatus.NOT_FOUND, `Ticket ID ${tId} not found or not belong to user ID ${userId}`);
  }
  const ticket = await prisma.bus_tickets.update({
    where: {
      id: tId,
    },
    data: {
      status: 1,
      update_time: new Date(),
    },
  });
  if (!ticket) {
    throw new ApiError(httpStatus.NOT_FOUND, `Ticket ID ${tId} not updated successfully`);
  }

  return { message: 'Pay ticket successfully' };
};

const createTicketByNumOfSeats = async (userId, email, busId, name, phone, numOfSeats, pickUpPont, dropDownPoint, note) => {
  const checkBusIDExist = await prisma.buses.findUnique({
    where: {
      id: busId,
    },
    include: {
      bus_stations_bus_stationsTobuses_end_point: true,
      bus_stations_bus_stationsTobuses_start_point: true,
      bus_operators: true,
    },
  });

  if (!checkBusIDExist) {
    throw new ApiError(httpStatus.NOT_FOUND, `Bus ID ${busId} not found`);
  }

  const checkUserIDExist = await prisma.users.findUnique({
    where: {
      id: userId,
    },
  });

  if (!checkUserIDExist) {
    throw new ApiError(httpStatus.NOT_FOUND, `User ID ${userId} not found`);
  }

  // const numOfSeatsBookedOrPayed = await prisma.bus_tickets.count({
  //   where: {
  //     bus_id: busId,
  //     status: { not: 2 },
  //   },
  // });

  // get sum of column num_of_seats of table bus_tickets where bus_id = busId and status = 0 or 1
  const numOfSeatsBookedOrPayed = await prisma.bus_tickets.aggregate({
    where: {
      bus_id: busId,
      status: { not: 2 },
    },
    _sum: {
      num_seats: true,
    },
  });

  // const numOfSeatsCanceled = await prisma.bus_tickets.count({
  //   where: {
  //     bus_id: busId,
  //     status: 2,
  //   },
  // });

  // get sum of column num_of_seats of table bus_tickets where bus_id = busId and status = 2
  const numOfSeatsCanceled = await prisma.bus_tickets.aggregate({
    where: {
      bus_id: busId,
      status: 2,
    },
    _sum: {
      num_seats: true,
    },
  });

  if (
    numOfSeats + numOfSeatsBookedOrPayed._sum.num_seats - numOfSeatsCanceled._sum.num_seats >
    checkBusIDExist.num_of_seats
  ) {
    return { error: ERROR_MESSAGE.NUM_OF_SEATS_EXCEED };
  }

  // const allSeatPosArr = [];
  // for (let i = 0; i < checkBusIDExist.num_of_seats; i++) {
  //   const checkSeatPosExist = await prisma.bus_tickets.findFirst({
  //     where: {
  //       bus_id: busId,
  //       seat: i.toString(),
  //       status: { not: 2 },
  //     },
  //   });
  //   if (!checkSeatPosExist) {
  //     allSeatPosArr.push(i.toString());
  //   }
  // }

  const getUnavailableSeats = await prisma.bus_tickets.findMany({
    where: {
      bus_id: busId,
      status: { not: 2 },
    },
    select: {
      seats: true,
    },
  });

  const unavailableSeatsPos = [];
  // const unavailableSeatsPos = getUnavailableSeats.map((item) => {

  //   const seatsPos = item.seat.split(',');
  //   for (let i = 0; i < seatsPos.length; ++i) {
  //     result.push(seatsPos[i]);
  //   }
  // });

  for (let i = 0; i < getUnavailableSeats.length; ++i) {
    const seatsPos = getUnavailableSeats[i].seats.split(',');
    for (let j = 0; j < seatsPos.length; ++j) {
      if (seatsPos[j] !== '') unavailableSeatsPos.push(seatsPos[j]);
    }
  }

  const allSeatPosArr = [];
  for (let i = 0; i < checkBusIDExist.num_of_seats; ++i) {
    if (!unavailableSeatsPos.includes(i.toString())) {
      allSeatPosArr.push(i.toString());
    }
  }
  const availableSeatPos = [];
  for (let i = 0; i < numOfSeats; ++i) {
    availableSeatPos.push(allSeatPosArr[i]);
  }

  const createTicket = await prisma.bus_tickets.create({
    data: {
      bus_id: busId,
      user_id: userId,
      name,
      phone,
      seats: availableSeatPos.join(),
      pick_up_point: pickUpPont,
      drop_down_point: dropDownPoint,
      note,
      num_seats: numOfSeats,
    },
  });

  return { status: createTicket !== null, data: createTicket };
};

const getTicketByBusIdAndUserId = async (busId, userId) => {
  return prisma.bus_tickets.findMany({
    where: {
      bus_id: busId,
      user_id: userId,
    },
  });
};
const discardTicket = async (req) => {
  const checkTicket = await prisma.bus_tickets.findUnique({
    where: {
      id: req.query.tid,
    },
  });
  if (!checkTicket) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Ticket not found');
  }
  return prisma.bus_tickets.update({
    where: {
      id: req.query.tid,
    },
    data: {
      status: 2,
      update_time: new Date(),
    },
  });
};
module.exports = {
  discardTicket,
  createTicketByNumOfSeats,
  getTicketByBusIdAndUserId,
  payTicket,
};
