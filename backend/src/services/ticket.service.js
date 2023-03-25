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

const payTicket = async (ticketIds) => {
  for (let i = 0; i < ticketIds.length; i++) {
    const ticket = await prisma.bus_tickets.update({
      where: {
        id: ticketIds[i],
      },
      data: {
        status: 1,
      },
    });
    if (!ticket) {
      throw new ApiError(httpStatus.NOT_FOUND, ERROR_MESSAGE.PAY_TICKET_ERROR);
    }
  }

  return { message: 'Pay ticket successfully' };
};

const createTicketByNumOfSeats = async (userId, email, busId, name, phone, numOfSeats) => {
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
    throw new ApiError(httpStatus.NOT_FOUND, 'Bus ID Not found');
  }

  const checkUserIDExist = await prisma.users.findUnique({
    where: {
      id: userId,
    },
  });

  if (!checkUserIDExist) {
    throw new ApiError(httpStatus.NOT_FOUND, 'User ID Not found');
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
    });
  }

  const msToTime = (ms) => {
    const seconds = (ms / 1000).toFixed(1);
    const minutes = (ms / (1000 * 60)).toFixed(1);
    const hours = (ms / (1000 * 60 * 60)).toFixed(1);
    const days = (ms / (1000 * 60 * 60 * 24)).toFixed(1);
    if (seconds < 60) return `${seconds} Seconds`;
    if (minutes < 60) return `${minutes} Minutes`;
    if (hours < 24) return `${hours} Hours`;
    return `${days} Days`;
  };

  const formatter = new Intl.NumberFormat('en-US', {
    style: 'decimal',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  });

  const result = { seat_positions: [], ticket_ids: [] };

  for (let i = 0; i < availableSeatPosArr.length; ++i) {
    const createTicket = await prisma.bus_tickets.create({
      data: availableSeatPosArr[i],
    });
    result.name = name;
    result.email = email;
    result.seat_positions.push(createTicket.seat);
    result.ticket_ids.push(createTicket.id);
    result.bo_name = checkBusIDExist.bus_operators.name;
    result.start_point = checkBusIDExist.bus_stations_bus_stationsTobuses_start_point.name;
    result.end_point = checkBusIDExist.bus_stations_bus_stationsTobuses_end_point.name;
    result.start_time = new Date(checkBusIDExist.start_time).toLocaleDateString(undefined, {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    });
    result.end_time = new Date(checkBusIDExist.end_time).toLocaleDateString(undefined, {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    });
    result.duration = Math.abs(checkBusIDExist.end_time.getTime() - checkBusIDExist.start_time.getTime());
    result.policy = '';
    result.num_of_seats = numOfSeats;
    result.type = checkBusIDExist.type === 0 ? 'Limousine' : checkBusIDExist.type === 1 ? 'Normal Seat' : 'Sleeper Bus';
    result.ticket_cost = formatter.format(checkBusIDExist.price);
    result.total_cost = formatter.format(checkBusIDExist.price * numOfSeats);
    result.status = createTicket.status === 0 ? 'Booked' : createTicket.status === 1 ? 'Paid' : 'Canceled';
  }

  const ticketIdsFormatted = await result.ticket_ids.map((tid) => `<li>${tid}</li>`);
  result.ticket_ids = `<ul>${ticketIdsFormatted.join('')}</ul>`;
  result.duration = await msToTime(result.duration);
  result.seat_positions = result.seat_positions.join(', ');

  return result;
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
      id: req.body.tid,
    },
  });
  if (!checkTicket) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Ticket not found');
  }
  return prisma.bus_tickets.update({
    where: {
      id: req.body.tid,
    },
    data: {
      status: 2,
    },
  });
};
module.exports = {
  discardTicket,
  createTicketByNumOfSeats,
  getTicketByBusIdAndUserId,
  payTicket,
};
