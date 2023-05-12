const { PrismaClient } = require('@prisma/client');
const httpStatus = require('http-status');
const { email } = require('../config/config');

const prisma = new PrismaClient();
const ApiError = require('../utils/ApiError');
const { convertDateToString } = require('../utils/dateFormat');

const searchBus = async (req) => {
  const { page, limit } = req.params;
  const { boId, price, type } = req.body;

  const query = {};

  if (boId) {
    query.bo_id = boId;
  }

  if (typeof type === 'number') {
    query.type = type;
  }

  if (typeof price === 'number') {
    query.price = {
      gte: price,
    };
  }
  const data = await prisma.buses.findMany({
    skip: page * limit,
    take: limit,
    where: query,
    include: {
      bus_operators: {
        select: { name: true },
      },
      bus_stations_bus_stationsTobuses_start_point: {
        select: {
          name: true,
          location: true,
        },
      },
      bus_stations_bus_stationsTobuses_end_point: {
        select: {
          name: true,
          location: true,
        },
      },
    },
  });

  // eslint-disable-next-line no-restricted-syntax
  for (const bus of data) {
    bus.left_seats =
      bus.num_of_seats -
      (await prisma.bus_tickets.count({
        where: {
          bus_id: bus.id,
          status: {
            in: [0, 1],
          },
        },
      }));

    bus.rating =
      Math.round(
        (
          await prisma.reviews.aggregate({
            _avg: {
              rate: true,
            },
            where: {
              bo_id: bus.bo_id,
            },
          })
        )._avg.rate * 10
      ) / 10;
  }

  return { data };
};

const searchBooking = async (req) => {
  const { page, limit } = req.params;
  const { name, status } = req.body;
  const query = {};

  if (name) {
    query.name = {
      contains: name,
    };
  }

  if (typeof status === 'number') {
    query.status = status;
  }

  console.log('query ', query);

  const data = await prisma.bus_tickets.findMany({
    skip: page * limit,
    take: limit,
    where: query,
    include: {
      buses: {
        include: {
          bus_operators: true,
          bus_stations_bus_stationsTobuses_end_point: true,
          bus_stations_bus_stationsTobuses_start_point: true,
        },
      },
      users: {
        select: {
          email: true,
        },
      },
    },
  });

  const formatData = [];
  data.forEach(async (item) => {
    formatData.push({
      id: item.id,
      bus_id: item.bus_id,
      name: item.name,
      start_point: item.buses.bus_stations_bus_stationsTobuses_start_point.name,
      end_point: item.buses.bus_stations_bus_stationsTobuses_end_point.name,
      start_time: item.buses.start_time,
      end_time: item.buses.end_time,
      seat: item.seat,
      status: item.status,
      phone: item.phone,
    });
  });

  console.log('count: ', formatData.length);

  return { data: formatData };
};

const createBus = async (req) => {
  return prisma.buses.create({
    data: {
      bo_id: req.body.bo_id,
      start_point: req.body.start_point,
      end_point: req.body.end_point,
      type: req.body.type,
      start_time: req.body.start_time,
      end_time: req.body.end_time,
      image_url: req.body.image_url,
      policy: req.body.policy,
      num_of_seats: req.body.num_of_seats,
      price: req.body.price,
    },
  });
};

const deleteBusById = async (busId) => {
  const bus = await prisma.buses.findUnique({
    where: {
      id: busId,
    },
  });

  if (!bus) throw new ApiError(httpStatus.BAD_REQUEST, 'BUS NOT FOUND');
  const result = await prisma.buses.delete({
    where: {
      id: busId,
    },
  });
  if (!result) return false;
  return true;
};

const updateBus = async (req) => {
  const checkBusExist = await prisma.buses.findUnique({
    where: {
      id: req.params.busId,
    },
  });

  if (!checkBusExist) {
    throw new ApiError(httpStatus.NOT_FOUND, 'This ticket does not exist');
  }

  const busUpdated = await prisma.buses.update({
    where: {
      id: req.params.busId,
    },
    data: {
      bo_id: req.body.bo_id,
      start_point: req.body.start_point,
      end_point: req.body.end_point,
      type: req.body.type,
      start_time: req.body.start_time,
      end_time: req.body.end_time,
      image_url: req.body.image_url,
      policy: req.body.policy,
      num_of_seats: req.body.num_of_seats,
      price: req.body.price,
    },
  });

  return busUpdated;
};
const getBusById = async (busId) => {
  const data = await prisma.buses.findUnique({
    where: {
      id: busId,
    },
    include: {
      bus_operators: true,
      bus_stations_bus_stationsTobuses_start_point: true,
      bus_stations_bus_stationsTobuses_end_point: true,
    },
  });

  return data;
};
const busList = async (page, limit, req) => {
  let data = null;
  let condition = {};
  if (req.user.role == 'bus_operator') {
    user = await prisma.users.findFirst({
      where: {
        id: req.user.id,
      },
      select: {
        boid: true,
      },
    });
    condition = { bo_id: user.boid };
  }

  data = await prisma.buses.findMany({
    skip: page * limit,
    take: limit,
    where: condition,

    include: {
      bus_operators: {
        select: {
          name: true, // MORE INFO
          image_url: true,
        },
      },
      bus_stations_bus_stationsTobuses_start_point: {
        select: {
          name: true,
        },
      },
      bus_stations_bus_stationsTobuses_end_point: {
        select: {
          name: true,
        },
      },
    },
    orderBy: [
      {
        start_time: 'desc',
      },
    ],
  });

  for (const bus of data) {
    bus.left_seats =
      bus.num_of_seats -
      (await prisma.bus_tickets.count({
        where: {
          bus_id: bus.id,
          status: {
            in: [0, 1],
          },
        },
      }));

    bus.rating =
      Math.round(
        (
          await prisma.reviews.aggregate({
            _avg: {
              rate: true,
            },
            where: {
              bo_id: bus.bo_id,
            },
          })
        )._avg.rate * 10
      ) / 10;
  }
  return { data };
};

const bookingDelete = async (req) => {
  if (req.user.role !== 'admin') return false;

  const ticket = await prisma.bus_tickets.findUnique({
    where: {
      id: req.params.bid,
    },
  });
  if (!ticket) throw new ApiError(httpStatus.BAD_REQUEST, 'Can not find ticket');
  const result = await prisma.bus_tickets.delete({
    where: {
      id: req.params.bid,
    },
  });

  if (result) return true;
  return false;
};
const bookingList = async (page, limit, req) => {
  let data = null;
  console.log(req.user.role);
  if (req.user.role !== 'admin') return [];

  data = await prisma.bus_tickets.findMany({
    skip: page * limit,
    take: limit,
    include: {
      buses: {
        include: {
          bus_operators: true,
          bus_stations_bus_stationsTobuses_end_point: true,
          bus_stations_bus_stationsTobuses_start_point: true,
        },
      },
      users: {
        select: {
          email: true,
        },
      },
    },
  });

  const formatData = [];
  data.forEach(async (item) => {
    formatData.push({
      id: item.id,
      bus_id: item.bus_id,
      name: item.name,
      start_point: item.buses.bus_stations_bus_stationsTobuses_start_point.name,
      end_point: item.buses.bus_stations_bus_stationsTobuses_end_point.name,
      start_time: item.buses.start_time,
      end_time: item.buses.end_time,
      seat: item.seat,
      status: item.status,
      phone: item.phone,
    });
  });

  return { data: formatData };
};

const bookingUpdate = async (req) => {
  const checkTicketExist = await prisma.bus_tickets.findUnique({
    where: {
      id: req.params.bid,
    },
  });

  if (!checkTicketExist) {
    throw new ApiError(httpStatus.NOT_FOUND, 'This ticket does not exist');
  }

  const ticketUpdated = await prisma.bus_tickets.update({
    where: {
      id: req.params.bid,
    },
    data: {
      status: req.body.status,
      name: req.body.name,
      phone: req.body.phone,
      seat: req.body.seat,
    },
  });

  return ticketUpdated;
};

const bookingGet = async (bid) => {
  const ticket = await prisma.bus_tickets.findUnique({
    where: {
      id: bid,
    },
    include: {
      buses: {
        include: {
          bus_stations_bus_stationsTobuses_start_point: true,
          bus_stations_bus_stationsTobuses_end_point: true,
        },
      },
      users: {
        select: {
          email: true,
        },
      },
    },
  });

  if (!ticket) {
    throw new ApiError(httpStatus.NOT_FOUND, 'This ticket does not exist');
  }

  return ticket;
};
module.exports = {
  createBus,
  deleteBusById,
  updateBus,
  getBusById,
  bookingList,
  bookingUpdate,
  bookingGet,
  busList,
  bookingDelete,
  searchBus,
  searchBooking,
};
