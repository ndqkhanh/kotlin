/* eslint-disable prettier/prettier */
const catchAsync = require('../utils/catchAsync');
const { boService } = require('../services');

const getReviews = catchAsync(async (req, res) => {
  const result = await boService.getReviews(req.query.boId, req.query.page, req.query.limit);
  result.forEach((item, index) =>{
      let timespan = new Date(item.ngay_review)
      item.ngay_review = timespan.toLocaleDateString("vi",{year: "numeric", month: "2-digit", day: "2-digit",})
    })
  const review_list = result
  res.send({review_list});
});

const createReview = catchAsync(async (req, res) => {
  const result = await boService.createReview(req.user.id, req.params.boId, req.body.rate, req.body.comment);
  res.send(result);
});
const viewBO = catchAsync(async (req, res) => {
  const result = await boService.listBusOperator(req);
  res.send(result);
});
const createBO = catchAsync(async (req, res) => {
  const result = await boService.createBO(req);
  res.send(result);
});

const updateBO = catchAsync(async (req, res) => {
  const result = await boService.updateBO(req);
  res.send(result);
});
const deleteBO = catchAsync(async (req, res) => {
  await boService.deleteBO(req);
  res.send({ success: true });
});
const getBOById = catchAsync(async (req, res) => {
  const result = await boService.getBusOperatorById(req.params.boId);
  res.send(result);
});
const getAverageRating = catchAsync(async (req, res) => {
  const result = await boService.getAverageRating(req);
  if (result[0].avg == null)
    result[0].avg = 0
  result[0].avg = Math.round(result[0].avg * 100) / 100

  res.send(result[0]);
});
module.exports = {
  getBOById,
  deleteBO,
  updateBO,
  createBO,
  viewBO,
  getReviews,
  createReview,
  getAverageRating
};
