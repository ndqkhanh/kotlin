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

const payTicket = async (userId, ticketIds) => {
  for (let i = 0; i < ticketIds.length; i++) {
    const ticketCheck = await prisma.bus_tickets.findFirst({
      where: {
        id: ticketIds[i],
        user_id: userId,
      },
    });
    if (!ticketCheck) {
      throw new ApiError(httpStatus.NOT_FOUND, `Ticket ID ${ticketIds[i]} not found or not belong to user ID ${userId}`);
    }
  }
  for (let i = 0; i < ticketIds.length; i++) {
    const ticket = await prisma.bus_tickets.update({
      where: {
        id: ticketIds[i],
      },
      data: {
        status: 1,
        update_time: new Date(),
      },
    });
    if (!ticket) {
      throw new ApiError(httpStatus.NOT_FOUND, `Ticket ID ${ticketIds[i]} not updated successfully`);
    }
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

  const numOfSeatsBookedOrPayed = await prisma.bus_tickets.count({
    where: {
      bus_id: busId,
      status: { not: 2 },
    },
  });

  const numOfSeatsCanceled = await prisma.bus_tickets.count({
    where: {
      bus_id: busId,
      status: 2,
    },
  });

  if (numOfSeats + numOfSeatsBookedOrPayed - numOfSeatsCanceled > checkBusIDExist.num_of_seats) {
    return { error: ERROR_MESSAGE.NUM_OF_SEATS_EXCEED };
  }

  const allSeatPosArr = [];
  for (let i = 0; i < checkBusIDExist.num_of_seats; i++) {
    const checkSeatPosExist = await prisma.bus_tickets.findFirst({
      where: {
        bus_id: busId,
        seat: i.toString(),
        status: { not: 2 },
      },
    });
    if (!checkSeatPosExist) {
      allSeatPosArr.push(i.toString());
    }
  }

  const availableSeatPosArr = [];
  for (let i = 0; i < numOfSeats; ++i) {
    availableSeatPosArr.push({
      bus_id: busId,
      user_id: userId,
      name,
      phone,
      seat: allSeatPosArr[i],
      pick_up_point: pickUpPont,
      drop_down_point: dropDownPoint,
      note,
    });
  }

  // const msToTime = (ms) => {
  //   const seconds = (ms / 1000).toFixed(1);
  //   const minutes = (ms / (1000 * 60)).toFixed(1);
  //   const hours = (ms / (1000 * 60 * 60)).toFixed(1);
  //   const days = (ms / (1000 * 60 * 60 * 24)).toFixed(1);
  //   if (seconds < 60) return `${seconds} Seconds`;
  //   if (minutes < 60) return `${minutes} Minutes`;
  //   if (hours < 24) return `${hours} Hours`;
  //   return `${days} Days`;
  // };

  // const formatter = new Intl.NumberFormat('en-US', {
  //   style: 'decimal',
  //   minimumFractionDigits: 0,
  //   maximumFractionDigits: 0,
  // });

  const result = { seat_positions: [], ticket_ids: [] };
  // result.name = name;
  // result.phone = phone;
  // result.email = email;
  // result.bo_name = checkBusIDExist.bus_operators.name;
  // result.start_point = checkBusIDExist.bus_stations_bus_stationsTobuses_start_point.name;
  // result.end_point = checkBusIDExist.bus_stations_bus_stationsTobuses_end_point.name;
  // result.start_time = convertDateToTime(new Date(checkBusIDExist.start_time));
  // result.end_time = convertDateToTime(new Date(checkBusIDExist.end_time));
  // result.duration = Math.abs(checkBusIDExist.end_time.getTime() - checkBusIDExist.start_time.getTime());
  // result.num_of_seats = numOfSeats;
  // result.type = checkBusIDExist.type === 0 ? 'Limousine' : checkBusIDExist.type === 1 ? 'Normal Seat' : 'Sleeper Bus';
  // result.ticket_cost = formatter.format(checkBusIDExist.price);
  // result.total_cost = formatter.format(checkBusIDExist.price * numOfSeats);
  // result.pick_up_point = pickUpPont;
  // result.drop_down_point = dropDownPoint;
  // result.status = 0;
  // result.duration = await msToTime(result.duration);

  for (let i = 0; i < availableSeatPosArr.length; ++i) {
    const createTicket = await prisma.bus_tickets.create({
      data: availableSeatPosArr[i],
    });

    result.seat_positions.push(createTicket.seat);
    result.ticket_ids.push(createTicket.id);
  }

  return { status: result.ticket_ids.length > 0, data: result };
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
