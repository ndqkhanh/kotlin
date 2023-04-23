/* eslint-disable prettier/prettier */
const express = require('express');
// eslint-disable-next-line import/no-unresolved
const pointController = require('../../controllers/point.controller');

const router = express.Router();

router.route('/list').get(pointController.getPoints);

module.exports = router;
