/* eslint-disable prettier/prettier */
const Joi = require('joi');

const createTicket = {
  params: Joi.object().keys({
    busId: Joi.string().uuid().required(),
  }),
  body: Joi.object().keys({
    name: Joi.string().required(),
    pick_up_point: Joi.string().uuid().required(),
    drop_down_point: Joi.string().uuid().required(),
    phone: Joi.string().required(),
    num_of_seats: Joi.number().required(),
  }),
};

const printTicket = {
  body: Joi.object().keys({
    bus_id: Joi.string().uuid().required(),
    user_id: Joi.string().uuid().required(),
  }),
};
const discardTicket = {
  body: Joi.object().keys({
    tid: Joi.string().uuid().required(),
  }),
};

const payTicket = {
  body: Joi.object().keys({
    ticket_ids: Joi.array().required(),
  }),
};

module.exports = {
  discardTicket,
  createTicket,
  printTicket,
  payTicket,
};
