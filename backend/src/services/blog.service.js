/* eslint-disable prettier/prettier */
/* eslint-disable no-plusplus */
/* eslint-disable no-await-in-loop */
const httpStatus = require('http-status');
const { PrismaClient } = require('@prisma/client');
const ApiError = require('../utils/ApiError');

const { convertDateToString } = require('../utils/dateFormat');

const prisma = new PrismaClient();

const getBlogById = async (id) => {
  const blog = await prisma.blogs.findFirst({
    where: {
      id,
    },
  });
  if (!blog) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Blog not found');
  }
  blog.create_time = convertDateToString(new Date(blog.create_time));
  blog.update_time = convertDateToString(new Date(blog.update_time));

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
    },
  });
  if (!blogCheck) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Blog not found or already deleted');
  }
  const blog = await prisma.blogs.delete({
    where: {
      id,
    },
  });
  if (!blog) {
    throw new ApiError(httpStatus.NOT_FOUND, 'Blog not deleted successfully');
  }
  return blog;
};

const getBlogs = async (page, limit) => {
  const data = await prisma.blogs.findMany({
    skip: (page - 1) * limit,
    take: limit,
    orderBy: {
      update_time: 'desc',
    },
  });

  for (let i = 0; i < data.length; i++) {
    data[i].create_time = convertDateToString(new Date(data[i].create_time));
    data[i].update_time = convertDateToString(new Date(data[i].update_time));
  }

  const count = await prisma.blogs.count();

  return { count, data };
};

module.exports = {
  getBlogById,
  createBlog,
  deleteBlog,
  getBlogs,
};
