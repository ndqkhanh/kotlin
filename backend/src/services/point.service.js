/* eslint-disable prettier/prettier */
/* eslint-disable no-plusplus */
/* eslint-disable no-await-in-loop */
const { PrismaClient } = require('@prisma/client');

const httpStatus = require('http-status');
const ApiError = require('../utils/ApiError');

const prisma = new PrismaClient();

const getPoints = async () => {
  const data = await prisma.points.findMany({});
  return { data };
};

const getPointById = async (id) => {
  const point = await prisma.points.findUnique({ where: { id } });
  if (!point) {
    throw new ApiError(httpStatus.BAD_REQUEST, 'This point does not exist');
  }
  return point;
};

const getPointsByBsId = async (bsId) => {
  const points = await prisma.point_bs.findMany({
    where: { bs_id: bsId },
    include: {
      points: true,
      bus_stations: true,
    },
  });
  if (!points) {
    throw new ApiError(httpStatus.BAD_REQUEST, 'No points for this bus station');
  }
  return { data: points };
};
module.exports = {
  getPoints,
  getPointById,
  getPointsByBsId
};
