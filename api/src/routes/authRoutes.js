const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const authenticateToken = require('../middleware/authMiddleware');


router.post('/google', authController.googleSignIn);
router.post('/register', authController.registerUser);
router.post('/login', authController.loginUser);

router.get('/profile', authenticateToken, authController.getCurrentUserProfile);

module.exports = router;