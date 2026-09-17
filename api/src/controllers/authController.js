const mongoose = require('mongoose');
const UserSettings = require('../models/userSettings');
const admin = require('../config/firebaseAdmin');
const { User } = require('../models/User');
const jwt = require('jsonwebtoken');
const bcrypt = require("bcrypt");

const signToken = (userId) => {
  return jwt.sign({ id: userId }, process.env.JWT_SECRET, { expiresIn: '30d' });
};


//google sign in for sso
exports.googleSignIn = async (req, res) => {
  try {
    const idToken = req.body.idToken || req.headers.authorization?.split(' ')[1];

    if (!idToken) {
      return res.status(400).json({ status: 'fail', message: 'Google ID Token is required' });
    }

    // Verify token with Firebase Admin
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    const { uid: googleId, email, name, picture } = decodedToken;

    // Search for existing user profile
    let user = await User.findOne({ $or: [{ googleId }, { email }] });

    if (!user) {
      // First-time creation for Google Sign-In
      const nameParts = (name || 'Google User').split(' ');
      const firstName = nameParts[0];
      const lastName = nameParts.slice(1).join(' ') || '';

      user = await User.create({
        googleId,
        email,
        firstName,
        lastName,
        displayName: name || `${firstName} ${lastName}`.trim(),
        picture,
        authProvider: 'google'
      });
    } else if (!user.googleId) {
      // Link Google ID if user originally registered via email/password
      user.googleId = googleId;
      if (!user.picture) user.picture = picture;
      await user.save();
    }

    const token = signToken(user._id);

    res.status(200).json({
      status: 'success',
      token,
      data: { user }
    });
  } catch (error) {
    console.error('Google Auth Error:', error);
    res.status(401).json({ status: 'error', message: 'Google authentication failed', error: error.message });
  }
};


//register regular user
exports.registerUser = async (req, res) => {
  try {

    const { email, password, firstName, lastName} = req.body;

    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ message: 'User already exists' });
    }

      // First-time user creation 
      user = await User.create({
        email,
        password,
        firstName,
        lastName,
        displayName: `${firstName} ${lastName}`.trim(),
        authProvider: 'password'
      });

      // Automatically create default UserSettings for the new user
      try {
        await UserSettings.create({
          _id: new mongoose.Types.ObjectId().toString(),
          userId: user._id
        });
      } catch (settingsError) {
        console.error('Could not create default settings:', settingsError.message);
      }

      const token = signToken(user._id); 
    
    res.status(200).json({
      status: 'success',
      token,
      data: { user }
    });
  } catch (error) {
    res.status(500).json({
      status: 'error',
      message: error.message
    });
  }
};

//login through database
exports.loginUser = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ status: 'fail', message: 'Please provide email and password' });
    }

    const user = await User.findOne({ email }).select('+password');

    const passwordMatches = await user.comparePassword(password);
    if (!passwordMatches) {
      return res.status(401).json({ status: 'fail', message: 'Invalid email or password.' });
    }

    const token = signToken(user._id);
    user.password = undefined;

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

//get the logged-in user's settings (creates defaults if none exist yet)
exports.getUserSettings = async (req, res) => {
  try {
    if (!req.user) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }

    let settings = await UserSettings.findOne({ userId: req.user._id });

    if (!settings) {
      settings = await UserSettings.create({
        _id: new mongoose.Types.ObjectId().toString(),
        userId: req.user._id
      });
    }

    res.status(200).json({ status: 'success', data: settings });
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};

//update the logged-in user's settings
exports.updateUserSettings = async (req, res) => {
  try {
    if (!req.user) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }

    const { language, weightUnit, notificationsEnabled, biometricLockEnabled } = req.body;

    const settings = await UserSettings.findOneAndUpdate(
      { userId: req.user._id },
      {
        $set: { language, weightUnit, notificationsEnabled, biometricLockEnabled },
        $setOnInsert: {
          _id: new mongoose.Types.ObjectId().toString(),
          userId: req.user._id
        }
      },
      { new: true, upsert: true, runValidators: true }
    );

    res.status(200).json(settings);
  } catch (error) {
    res.status(400).json({ status: 'fail', message: error.message });
  }
};