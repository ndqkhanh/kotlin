/* eslint-disable prettier/prettier */
const Joi = require('joi');

const getBlogByID = {
  params: Joi.object().keys({
    blogId: Joi.string().uuid().required(),
  }),
};

const createBlog = {
  body: Joi.object().keys({
    thumbnail: Joi.string(),
    title: Joi.string().required(),
    content: Joi.string().required(),
  }),
};

const deleteBlog = {
  params: Joi.object().keys({
    blogId: Joi.string().uuid().required(),
  }),
};

module.exports = {
  getBlogByID,
  createBlog,
  deleteBlog,
};