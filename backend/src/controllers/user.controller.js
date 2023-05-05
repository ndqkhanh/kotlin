const httpStatus = require('http-status');
const pick = require('../utils/pick');
const ApiError = require('../utils/ApiError');
const catchAsync = require('../utils/catchAsync');
const { userService } = require('../services');

const getUser = catchAsync(async (req, res) => {
  const user = await userService.getUserById(req.params.userId);
  if (!user) {
    throw new ApiError(httpStatus.NOT_FOUND, 'User not found');
  }
  delete user.password;
  res.send(user);
});

const getProfile = catchAsync(async (req, res) => {
  const user = await userService.getUserById(req.user.id);
  if (!user) {
    throw new ApiError(httpStatus.NOT_FOUND, 'User not found');
  }
  delete user.password;
  res.send(user);
});

const createUser = catchAsync(async (req, res) => {
  const user = await userService.createUser(req.body);
  res.status(httpStatus.CREATED).send(user);
});

const getUsers = catchAsync(async (req, res) => {
  const filter = pick(req.query, ['name', 'role']);
  const options = pick(req.query, ['sortBy', 'limit', 'page']);
  const result = await userService.queryUsers(filter, options);
  res.send(result);
});

const updateUser = catchAsync(async (req, res) => {
  const user = await userService.updateUserById(req.user.id, req.body);
  delete user.password;
  res.send(user);
});

const getMyQuestions = catchAsync(async (req, res) => {
  const countQuestions = await userService.countMyQuestions(req);
  const myQuestions = await userService.getMyQuestionsPagination(req);

  res.send({ count: countQuestions, questions: myQuestions });
});

const getHistoryByUId = catchAsync(async (req, res) => {
  //console.log('req.user.id', req);
  let history_list = await userService.getHistoryByUId(req);

  history_list.forEach((item, index) =>{
    let timespan = new Date(item.start_time)
    item.start_date = timespan.toLocaleDateString("vi",{year: "numeric", month: "2-digit", day: "2-digit",})
    item.start_time = timespan.toLocaleTimeString("vi", { hour: "2-digit", minute: "2-digit" })

    timespan = new Date(item.end_time)
    item.end_date = timespan.toLocaleDateString("vi",{year: "numeric", month: "2-digit", day: "2-digit",})
    item.end_time = timespan.toLocaleTimeString("vi", { hour: "2-digit", minute: "2-digit" })
  })

   res.send({ history_list });
});
const getUserByUsername = catchAsync(async (req, res) => {
  const user = await userService.getUserByUsername(req.query.username);
  if (!user) {
    throw new ApiError(httpStatus.NOT_FOUND, 'User not found');
  }
  delete user.password;
  res.send(user);
});
module.exports = {
  getHistoryByUId,
  createUser,
  getUsers,
  getUser,
  updateUser,
  getMyQuestions,
  getProfile,
  getUserByUsername
};
