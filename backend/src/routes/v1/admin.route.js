const express = require('express');
const validate = require('../../middlewares/validate');
const adminValidation = require('../../validations/admin.validation');
const adminController = require('../../controllers/admin.controller');
const auth = require('../../middlewares/auth');

const router = express.Router();

router.route('/bus/create').post(auth('createBus'), validate(adminValidation.createBus), adminController.createBus);

router.route('/bus/delete/:busId').post(auth('deleteBus'), validate(adminValidation.deleteBus), adminController.deleteBus);

router.route('/bus/update/:busId').post(auth('updateBus'), validate(adminValidation.updateBus), adminController.updateBus);

router.route('/bus/list/:page/:limit').get(auth('busList'), validate(adminValidation.busList), adminController.busList);

router
  .route('/bus-operator/list/:page/:limit')
  .get(auth('busOperatorList'), validate(adminValidation.boList), adminController.boList);
router.route('/bus/search/:page/:limit').post(validate(adminValidation.searchBus), adminController.searchBus);
router.route('/bus/:busId').get(auth('getBus'), validate(adminValidation.getBus), adminController.getBus);
router
  .route('/booking/list/:page/:limit')
  .get(auth('bookingList'), validate(adminValidation.bookingList), adminController.bookingList);

router
  .route('/booking/search/:page/:limit')
  .post(auth('searchBooking'), validate(adminValidation.searchBooking), adminController.searchBooking);
router
  .route('/booking/:bid')
  .get(auth('bookingGet'), validate(adminValidation.bookingGet), adminController.bookingGet)
  .post(auth('bookingUpdate'), validate(adminValidation.bookingUpdate), adminController.bookingUpdate)
  .delete(auth('bookingDelete'), validate(adminValidation.bookingDelete), adminController.bookingDelete);

module.exports = router;
