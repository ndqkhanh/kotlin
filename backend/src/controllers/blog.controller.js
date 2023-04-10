/* eslint-disable prettier/prettier */
const catchAsync = require('../utils/catchAsync');
const { blogService } = require('../services');

const getBlogById = catchAsync(async (req, res) => {
  const result = await blogService.getBlogById(req.params.blogId);
  res.send(result);
});

const createBlog = catchAsync(async (req, res) => {
  const result = await blogService.createBlog(req.body);
  res.send(result);
});

const deleteBlog = catchAsync(async (req, res) => {
  const result = await blogService.deleteBlog(req.params.blogId);
  res.send({ success: result !== null });
});

const getBlogs = catchAsync(async (req, res) => {
  const result = await blogService.getBlogs(req.params.page, req.params.limit);
  res.send(result);
});

module.exports = {
  getBlogById,
  createBlog,
  deleteBlog,
  getBlogs,
};
