/* eslint-disable prettier/prettier */
/* eslint-disable no-plusplus */
/* eslint-disable no-await-in-loop */
const httpStatus = require('http-status');
const { PrismaClient, sql } = require('@prisma/client');
const ApiError = require('../utils/ApiError');

const prisma = new PrismaClient();

const getReviews = async (boId, page, limit) => {
  const checkBoIdExist = await prisma.bus_operators.findUnique({
    where: {
      id: boId,
    },
  });
  if (!checkBoIdExist) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Bus operator not found');
  }

  var getReviews = sql`select r.rate, r."comment", u.display_name name, u.avatar_url, u.email account_name, r.update_time ngay_review
                         from reviews r join bus_operators bo on r.bo_id = bo.id join users u on u.id = r.user_id
                         where bo.id = ${boId}
                         order by r.update_time desc
                         offset ${limit * page} rows fetch next ${limit} rows only`;

  var data = await prisma.$queryRaw(getReviews);

  return data;
};

const createReview = async (userId, boId, rate, comment) => {
  const checkBoIdExist = await prisma.bus_operators.findUnique({
    where: {
      id: boId,
    },
  });
  if (!checkBoIdExist) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Bus operator not found');
  }

  const checkUserIdExist = await prisma.users.findUnique({
    where: {
      id: userId,
    },
  });
  if (!checkUserIdExist) {
    throw new ApiError(httpStatus.NOT_FOUND, 'User not found');
  }

  if (rate < 0 || rate > 5) {
    throw new ApiError(httpStatus.BAD_REQUEST, 'Rate must be between 0 and 5');
  }

  const data = await prisma.reviews.create({
    data: {
      bo_id: boId,
      user_id: userId,
      rate,
      comment,
    },
  });

  return data;
};

const getBusOperatorById = async (id) => {
  return prisma.bus_operators.findUnique({ where: { id } });
};

const listBusOperator = async (req) => {
  let condition = {};
  if (req.user.role === 'bus_operator') {
    user = await prisma.users.findFirst({
      where: {
        id: req.user.id,
      },
      select: {
        boid: true,
      },
    });
    condition = { id: user.boid };
  }
  const listBO = await prisma.bus_operators.findMany({
    orderBy: {
      name: 'asc',
    },
    skip: req.params.page * req.params.limit,
    take: req.params.limit,
    where: condition,
  });
  return { data: listBO };
};
const createBO = async (req) => {
  const message = await prisma.bus_operators.create({
    data: {
      image_url: req.body.image_url,
      phone: req.body.phone,
      name: req.body.name,
    },
  });
  return message;
};
const updateBO = async (req) => {
  const checkBO = await prisma.bus_operators.findUnique({
    where: {
      id: req.body.id,
    },
  });
  if (!checkBO) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Bus operator not found');
  }
  const message = await prisma.bus_operators.update({
    where: {
      id: req.body.id,
    },
    data: {
      image_url: req.body.image_url,
      phone: req.body.phone,
      name: req.body.name,
    },
  });
  return message;
};
const deleteBO = async (req) => {
  const checkBO = await prisma.bus_operators.findUnique({
    where: {
      id: req.params.boId,
    },
  });
  if (!checkBO) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Bus operator not found');
  }
  const message = await prisma.bus_operators.delete({
    where: {
      id: req.params.boId,
    },
  });
  return message;
};
const getAverageRating = async (req) => {
  const checkBoIdExist = await prisma.bus_operators.findUnique({
    where: {
      id: req.query.boId,
    },
  });
  if (!checkBoIdExist) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Bus operator not found');
  }

  var getAvg = sql`select avg(r.rate) avg
                        from reviews r
                        join bus_operators bo
                        on r.bo_id = bo.id
                        where bo.id = ${req.query.boId}`;

  res = await prisma.$queryRaw(getAvg);

  return res;
};
module.exports = {
  deleteBO,
  updateBO,
  createBO,
  listBusOperator,
  getReviews,
  createReview,
  getBusOperatorById,
  getAverageRating,
};
