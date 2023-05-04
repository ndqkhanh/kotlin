const { PrismaClient, sql } = require('@prisma/client');
const httpStatus = require('http-status');
const bcrypt = require('bcrypt');
const { User } = require('../models');

const prisma = new PrismaClient();

const ApiError = require('../utils/ApiError');

/**
 * Create a user
 * @param {Object} userBody
 * @returns {Promise<User>}
 */
const createUser = async (userBody) => {
  if (userBody.password !== userBody.repassword) {
    throw new ApiError(httpStatus.BAD_REQUEST, 'Repassword is not identical to password');
  }

  const saltRounds = 10;

  // eslint-disable-next-line no-param-reassign
  userBody.password = await bcrypt.hash(userBody.password, saltRounds);
  // eslint-disable-next-line no-param-reassign
  userBody.password = Buffer.from(userBody.password);

  const checkEmail = await prisma.users.findUnique({
    where: {
      email: userBody.email,
    },
  });

  if (checkEmail) {
    throw new ApiError(httpStatus.BAD_REQUEST, 'Username already taken');
  }
  // else if (checkEmail && checkEmail.verification === false) {
  //   await prisma.users.delete({
  //     where: {
  //       email: userBody.email,
  //     },
  //   });
  // }

  // eslint-disable-next-line no-param-reassign
  delete userBody.repassword;
  const user = prisma.users.create({
    data: userBody,
  });

  return user;
};

/**
 * Query for users
 * @param {Object} filter - Mongo filter
 * @param {Object} options - Query options
 * @param {string} [options.sortBy] - Sort option in the format: sortField:(desc|asc)
 * @param {number} [options.limit] - Maximum number of results per page (default = 10)
 * @param {number} [options.page] - Current page (default = 1)
 * @returns {Promise<QueryResult>}
 */
const queryUsers = async (filter, options) => {
  const users = await User.paginate(filter, options);
  return users;
};

const getUserById = async (id) => {
  return prisma.users.findUnique({
    where: {
      id,
    },
  });
};

/**
 * Get user by email
 * @param {string} email
 * @returns {Promise<User>}
 */
const getUserByEmail = async (email) => {
  return prisma.users.findUnique({
    where: {
      email,
    },
  });
};

/**
 * Update user by id
 * @param {ObjectId} userId
 * @param {Object} updateBody
 * @returns {Promise<User>}
 */
const updateUserById = async (userId, newPassword) => {
  const saltRounds = 10;
  // eslint-disable-next-line no-param-reassign
  newPassword = await bcrypt.hash(newPassword, saltRounds);
  // eslint-disable-next-line no-param-reassign
  newPassword = Buffer.from(newPassword);

  const checkUserExists = await getUserById(userId);
  if (!checkUserExists) {
    throw new ApiError(httpStatus.NOT_FOUND, 'User not found');
  }

  const user = await prisma.users.update({
    where: {
      id: userId,
    },
    data: {
      password: newPassword,
    },
  });

  return user;
};

const countMyQuestions = async (req) => {
  const questions = await prisma.questions.findMany({
    where: { uid: req.user.id },
  });

  return questions.length;
};

const getMyQuestionsPagination = async (req) => {
  const questions = await prisma.questions.findMany({
    skip: req.params.page * req.params.limit,
    take: req.params.limit,
    where: {
      uid: req.user.id,
    },
  });

  return questions;
};
const getHistoryByUId = async (req) => {
  var updateQuery = sql`update bus_tickets bt
                        set status = 2
                        where bt.id in (select bt2.id from bus_tickets bt2 join buses b on bt2.bus_id = b.id
                        				where b.start_time < current_timestamp and bt2.status = 0 and bt2.user_id = ${req.user.id})`
  var condition = sql``
  if(req.query.type == 'discard'){
    await prisma.$queryRaw(updateQuery)
    condition = sql`bt.status = 2 and `
    }
  else if(req.query.type == 'current'){
    await prisma.$queryRaw(updateQuery)
    condition = sql`b.start_time >= current_timestamp and bt.status != 2 and `
    }
  else if(req.query.type == 'done')
    condition = sql`b.start_time < current_timestamp and bt.status = 1 and `

  var historyList = null

  const querySQL = sql`select bt.id, bt.bus_id, bt.phone, bt.seat, bt.status, b.start_time, b.end_time,
                           		b.price, bo.name ten_nha_xe, p.name ten_diem_don,
                           		p.location dia_chi_diem_don, p2.name ten_diem_tra, p2.location dia_chi_diem_tra,
                           		bs.name tinh_don, bs2.name tinh_tra, bt.note
                                             from bus_tickets bt
                                             	join buses b
                                             	on b.id = bt.bus_id
                                             	join points p
                                             	on bt.drop_down_point = p.id
                                             	join points p2
                                             	on bt.pick_up_point = p2.id
                                             	join bus_operators bo
                                             	on bo.id = b.bo_id
                                             	join bus_stations bs
                                             	on bs.id = b.start_point
                                             	join bus_stations bs2
                                             	on bs2.id = b.end_point
                                             where ${condition} bt.user_id = ${req.user.id}
                                             order by b.start_time desc
                  offset ${req.query.limit * req.query.page} rows fetch next ${req.query.limit} rows only`

    historyList = await prisma.$queryRaw(querySQL)

  return historyList;
};

const getUserByUsername = async (email) => {

  return await prisma.users.findUnique({
    where: {
      email,
    },
  });
};
module.exports = {
  getHistoryByUId,
  createUser,
  queryUsers,
  getUserById,
  getUserByEmail,
  updateUserById,
  countMyQuestions,
  getMyQuestionsPagination,
  getUserByUsername,
};
