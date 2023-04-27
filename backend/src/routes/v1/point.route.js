/* eslint-disable prettier/prettier */
const express = require('express');
// eslint-disable-next-line import/no-unresolved
const validate = require('../../middlewares/validate');
const pointValidation = require('../../validations/point.validation');
const pointController = require('../../controllers/point.controller');

const router = express.Router();

router.route('/list').get(pointController.getPoints);
router.route('/list-point/:bsId').get(validate(pointValidation.getPointsByBsID), pointController.getPointsByBsId);

module.exports = router;
