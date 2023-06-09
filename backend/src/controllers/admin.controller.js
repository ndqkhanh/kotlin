const catchAsync = require('../utils/catchAsync');
const { adminService } = require('../services');

const searchBus = catchAsync(async (req, res) => {
  const busData = await adminService.searchBus(req);
  res.send(busData);
});

const searchBooking = catchAsync(async (req, res) => {
  const bookingData = await adminService.searchBooking(req);
  res.send(bookingData);
});

const createBus = catchAsync(async (req, res) => {
  const bus = await adminService.createBus(req);

  res.send(bus);
});

const deleteBus = catchAsync(async (req, res) => {
  const result = await adminService.deleteBusById(req.params.busId);
  res.send({ success: result });
});

const updateBus = catchAsync(async (req, res) => {
  const busUpdated = await adminService.updateBus(req);
  res.send(busUpdated);
});

const getBus = catchAsync(async (req, res) => {
  const bus = await adminService.getBusById(req.params.busId);
  res.send(bus);
});

const busList = catchAsync(async (req, res) => {
  const { page, limit } = req.params;
  const buses = await adminService.busList(page, limit, req);
  res.send(buses);
});

const boList = catchAsync(async (req, res) => {
  const { page, limit } = req.params;
  const bos = await adminService.boList(page, limit, req);
  res.send(bos);
});

const bookingList = catchAsync(async (req, res) => {
  const { page, limit } = req.params;
  const bookings = await adminService.bookingList(page, limit, req);
  res.send(bookings);
});

const bookingUpdate = catchAsync(async (req, res) => {
  const booking = await adminService.bookingUpdate(req);
  res.send(booking);
});

const bookingGet = catchAsync(async (req, res) => {
  const booking = await adminService.bookingGet(req.params.bid);
  res.send(booking);
});

const bookingDelete = catchAsync(async (req, res) => {
  const result = await adminService.bookingDelete(req);
  res.send({ success: result });
});
module.exports = {
  createBus,
  deleteBus,
  updateBus,
  getBus,
  bookingList,
  bookingUpdate,
  bookingGet,
  busList,
  bookingDelete,
  searchBus,
  searchBooking,
  boList,
};
