/* eslint-disable prettier/prettier */
/* eslint-disable no-plusplus */
/* eslint-disable no-await-in-loop */
const httpStatus = require('http-status');
const { PrismaClient } = require('@prisma/client');
const ApiError = require('../utils/ApiError');

const prisma = new PrismaClient();

const getBlogById = async (id) => {
  const blog = await prisma.blogs.findFirst({
    where: {
      id,
      status: 0,
    },
  });
  if (!blog) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Blog not found');
  }
  return blog;
};

const createBlog = async (body) => {
  const blog = await prisma.blogs.create({ data: body });
  if (!blog) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Blog not created successfully');
  }
  return blog;
};

const deleteBlog = async (id) => {
  const blogCheck = await prisma.blogs.findFirst({
    where: {
      id,
      status: 0,
    },
  });
  if (!blogCheck) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Blog not found or already deleted');
  }
  const blog = await prisma.blogs.update({
    where: { id },
    data: { status: 1 },
  });
  if (!blog) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Blog not deleted successfully');
  }
  return blog;
};

module.exports = {
  getBlogById,
  createBlog,
  deleteBlog,
};
