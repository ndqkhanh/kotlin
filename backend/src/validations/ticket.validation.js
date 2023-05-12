/* eslint-disable prettier/prettier */
const Joi = require('joi');

const createTicket = {
  params: Joi.object().keys({
    busId: Joi.string().uuid().required(),
  }),
  body: Joi.object().keys({
    name: Joi.string().required(),
    pick_up_point: Joi.string().uuid(),
    drop_down_point: Joi.string().uuid(),
    phone: Joi.string().required(),
    num_of_seats: Joi.number().required(),
    note: Joi.string(),
  }),
};

const printTicket = {
  body: Joi.object().keys({
    bus_id: Joi.string().uuid().required(),
    user_id: Joi.string().uuid().required(),
  }),
};
const discardTicket = {
  query: Joi.object().keys({
    tid: Joi.string().uuid().required(),
  }),
};

const payTicket = {
  query: Joi.object().keys({
    tId: Joi.string().uuid().required(),
  }),
};

module.exports = {
  discardTicket,
  createTicket,
  printTicket,
  payTicket,
};
