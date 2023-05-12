/* eslint-disable no-await-in-loop */
/* eslint-disable no-restricted-syntax */
const { PrismaClient, sql } = require('@prisma/client');
const httpStatus = require('http-status');
const ApiError = require('../utils/ApiError');

const { convertDateToString } = require('../utils/dateFormat');

const prisma = new PrismaClient();

function secondsToHms(d) {
  d = Number(d);
  const h = Math.floor(d / 3600);
  const m = Math.floor((d % 3600) / 60);
  const s = Math.floor((d % 3600) % 60);

  const hDisplay = h > 0 ? h + (h == 1 ? ' hour, ' : 'h') : '';
  const mDisplay = m > 0 ? m + (m == 1 ? ' minute, ' : 'm') : '';
  const sDisplay = s > 0 ? s + (s == 1 ? ' second' : 's') : '';
  return hDisplay + mDisplay + sDisplay;
}

const searchBus = async (body) => {
  const { startPoint, endPoint, page, limit, boId, price, type, startTime } = body;

  const query = {
    start_point: startPoint,
    end_point: endPoint,
  };

  if (startTime) {
    query.start_time = {
      gte: startTime,
    };
  }

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
      bus_operators: true,
      bus_stations_bus_stationsTobuses_end_point: true,
      bus_stations_bus_stationsTobuses_start_point: true,
    },
  });

  for (const bus of data) {
    bus.start_point = bus.bus_stations_bus_stationsTobuses_start_point;
    bus.end_point = bus.bus_stations_bus_stationsTobuses_end_point;
    delete bus.bus_stations_bus_stationsTobuses_start_point;
    delete bus.bus_stations_bus_stationsTobuses_end_point;

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
    const duration = (new Date(bus.end_time) - new Date(bus.start_time)) / 1000;
    bus.duration = secondsToHms(duration);
    // format bus pricing format by thousand
    bus.pricing_format = bus.price.toLocaleString('en-US', {
      style: 'currency',
      currency: 'VND',
    });
    // get hour:minute with format double 0 of start time
    bus.start_time_hour =
      // eslint-disable-next-line prefer-template
      (new Date(bus.start_time).getHours() < 10
        ? `0${new Date(bus.start_time).getHours()}`
        : new Date(bus.start_time).getHours()) +
      ':' +
      (new Date(bus.start_time).getMinutes() < 10
        ? `0${new Date(bus.start_time).getMinutes()}`
        : new Date(bus.start_time).getMinutes());

    // get hour of end time
    bus.end_time_hour =
      // eslint-disable-next-line prefer-template
      (new Date(bus.end_time).getHours() < 10
        ? `0${new Date(bus.end_time).getHours()}`
        : new Date(bus.end_time).getHours()) +
      ':' +
      (new Date(bus.end_time).getMinutes() < 10
        ? `0${new Date(bus.end_time).getMinutes()}`
        : new Date(bus.end_time).getMinutes());

    bus.start_time = new Date(bus.start_time).toLocaleDateString(undefined, {
      hour: 'numeric',
      minute: 'numeric',
    });

    bus.end_time = new Date(bus.end_time).toLocaleDateString(undefined, {
      hour: 'numeric',
      minute: 'numeric',
    });
  }

  const count = await prisma.buses.count({
    where: query,
  });
  return { count, data };
};

const getBusInformation = async (busId) => {
  const data = await prisma.buses.findFirst({
    where: {
      id: busId,
    },
    include: {
      bus_operators: true,
      bus_stations_bus_stationsTobuses_end_point: true,
      bus_stations_bus_stationsTobuses_start_point: 'end_point',
    },
  });
  data.start_time = convertDateToString(new Date(data.start_time));
  data.end_time = convertDateToString(new Date(data.end_time));

  data.start_point = data.bus_stations_bus_stationsTobuses_start_point;
  data.end_point = data.bus_stations_bus_stationsTobuses_end_point;
  delete data.bus_stations_bus_stationsTobuses_start_point;
  delete data.bus_stations_bus_stationsTobuses_end_point;

  const numOfSeatsBookedOrPayed = await prisma.bus_tickets.aggregate({
    where: {
      bus_id: busId,
      status: { not: 2 },
    },
    _sum: {
      num_seats: true,
    },
  });

  // get the number of seats left
  data.left_seats = data.num_of_seats - numOfSeatsBookedOrPayed._sum.num_seats;
  // (await prisma.bus_tickets.count({
  //   where: {
  //     bus_id: data.id,
  //     status: {
  //       in: [0, 2],
  //     },
  //   },
  // }));

  return data;
};

const cloneBus = async (id, startTime, endTime) => {
  const busTemplate = await prisma.buses.findUnique({
    where: { id },
  });

  if (startTime.getTime() > endTime.getTime()) {
    throw new ApiError(httpStatus.BAD_REQUEST, 'Start time and end time is invalidate');
  }

  const newBus = await prisma.buses.create({
    data: {
      bo_id: busTemplate.bo_id,
      start_point: busTemplate.start_point,
      end_point: busTemplate.end_point,
      type: busTemplate.type,
      start_time: startTime,
      end_time: endTime,
      image_url: busTemplate.image_url,
      policy: busTemplate.policy,
      num_of_seats: busTemplate.num_of_seats,
      price: busTemplate.price,
    },
  });

  return newBus;
};

const getBusById = async (id) => {
  return prisma.buses.findUnique({ where: { id } });
};
const getBusDetail = async (req) => {
  const sqlQuery = sql`select ps."name" ten_diem_don, ps."location" dia_chi_diem_don,
                       		pe."name" ten_diem_tra, ps."location" dia_chi_diem_tra,
                       		b.start_time, b.end_time, bo."name" ten_nha_xe,
                       		bo.phone sdt_nha_xe, bo.image_url anh_nha_xe,
                       		b.image_url anh_xe, b."policy"
                       from buses b join bus_operators bo on b.bo_id = bo.id and b.id = ${req.query.bId}
                       	join bus_stations bss on bss.id = b.start_point
                       	join bus_stations bse on bse.id = b.end_point
                       	join point_bs pbs on pbs.bs_id = bss.id
                       	join point_bs pbe on pbe.bs_id = bse.id
                       	join points ps on ps.id = pbs.point_id
                       	join points pe on pe.id = pbe.point_id `

  result = await prisma.$queryRaw(sqlQuery);

  return result;
};
module.exports = {
  searchBus,
  getBusInformation,
  cloneBus,
  getBusById,
  getBusDetail,
};
