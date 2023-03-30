/* eslint-disable prettier/prettier */
const express = require('express');
const auth = require('../../middlewares/auth');
const validate = require('../../middlewares/validate');
const blogValidation = require('../../validations/blog.validation');
const blogController = require('../../controllers/blog.controller');

const router = express.Router();

router.route('/:blogId').get(validate(blogValidation.getBlogByID), blogController.getBlogById);
router.route('/create').post(auth('createBlog'), validate(blogValidation.createBlog), blogController.createBlog);
router.route('/delete/:blogId').post(auth('deleteBlog'), validate(blogValidation.deleteBlog), blogController.deleteBlog);

module.exports = router;
