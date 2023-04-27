/* eslint-disable prettier/prettier */
const catchAsync = require('../utils/catchAsync');
const { pointService } = require('../services');

const getPointById = catchAsync(async (req, res) => {
  const result = await pointService.getPointById(req.params.pointId);
  res.send(result);
});

const getPointsByBsId = catchAsync(async (req, res) => {
  const result = await pointService.getPointsByBsId(req.params.bsId);
  res.send(result);
});

const getPoints = catchAsync(async (req, res) => {
  const result = await pointService.getPoints();
  res.send(result);
});

module.exports = {
  getPointById,
  getPointsByBsId,
  getPoints,
};
