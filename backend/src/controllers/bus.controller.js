const catchAsync = require('../utils/catchAsync');
const { busService } = require('../services');

const searchBus = catchAsync(async (req, res) => {
  // const countQuestions = await userService.countMyQuestions(req);
  // const myQuestions = await userService.getMyQuestionsPagination(req);
  const busData = await busService.searchBus(req.body);
  res.send(busData);
});

const getBusInformation = catchAsync(async (req, res) => {
  const busInformation = await busService.getBusInformation(req.params.busId);
  res.send(busInformation);
});

const cloneBus = catchAsync(async (req, res) => {
  const newBus = await busService.cloneBus(req.body.id, req.body.start_time, req.body.end_time);
  res.send(newBus);
});
const getBusDetail = catchAsync(async (req, res) => {
  const result = await busService.getBusDetail(req);
  let bDetail = result.result[0];

  let timespan = new Date(bDetail.start_time);
  bDetail.start_date = timespan.toLocaleDateString('vi', { year: 'numeric', month: '2-digit', day: '2-digit' });
  bDetail.start_time = timespan.toLocaleTimeString('vi', { hour: '2-digit', minute: '2-digit' });

  timespan = new Date(bDetail.end_time);
  bDetail.end_date = timespan.toLocaleDateString('vi', { year: 'numeric', month: '2-digit', day: '2-digit' });
  bDetail.end_time = timespan.toLocaleTimeString('vi', { hour: '2-digit', minute: '2-digit' });

  bDetail.ds_don = result.don
  bDetail.ds_tra = result.tra
  delete bDetail.start_bs_id
  delete bDetail.end_bs_id

  res.send(bDetail);
});
module.exports = {
  searchBus,
  getBusInformation,
  cloneBus,
  getBusDetail,
};
