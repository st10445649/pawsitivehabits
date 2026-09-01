const { User } = require('../models/User');
const jwt = require('jsonwebtoken');

const generateToken = (userId) => {
  return jwt.sign({ id: userId }, process.env.JWT_SECRET, { expiresIn: '30d' });
};


exports.registerUser = async (req, res) => {
  try {

    const { email, password, firstName, lastName} = req.body;

    // Search for existing user profile
    let user = await User.findOne({ firebaseUid: uid });

    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ message: 'User already exists' });
    }
      // First-time user creation (
      user = await User.create({
        email,
        password,
        firstName,
        lastName,
        displayName: `${firstName} ${lastName}`.trim(),
        authProvider: 'password'
      });

      const token = signToken(user._id); 
    
    res.status(200).json({
      status: 'success',
      data: { user }
    });
  } catch (error) {
    res.status(500).json({
      status: 'error',
      message: error.message
    });
  }
};

exports.loginUser = async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email }).select('+password');
    if (!user || !(await user.comparePassword(password))) {
      return res.status(401).json({ status: 'fail', message: 'Invalid email or password.' });
    }

    const token = signToken(user._id);

    res.status(200).json({
      status: 'success',
      token,
      data: { user }
    });
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};


exports.getCurrentUserProfile = async (req, res) => {
  try {
    if (!req.user) {
      return res.status(404).json({
        status: 'fail',
        message: 'User profile not found in database. Please call /api/users/sync first.'
      });
    }

    res.status(200).json({
      status: 'success',
      data: { user: req.user }
    });
  } catch (error) {
    res.status(500).json({
      status: 'error',
      message: error.message
    });
  }
};