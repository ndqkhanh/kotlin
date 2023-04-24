/* eslint-disable prettier/prettier */
const Joi = require('joi');

const getPointsByBsID = {
  params: Joi.object().keys({
    bsId: Joi.string().uuid().required(),
  }),
};

module.exports = {
  getPointsByBsID,
};
