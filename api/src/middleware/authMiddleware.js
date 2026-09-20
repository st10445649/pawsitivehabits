const admin = require('../config/firebaseAdmin');
const jwt = require('jsonwebtoken');
const { User } = require('../models/User');

const authenticateToken = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({
        status: 'fail',
        message: 'Authentication required. Please provide a Bearer token in the Authorization header.'
      });
    }

    const token = authHeader.split(' ')[1];

    try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      const user = await User.findById(decoded.id);

      if (!user) {
        return res.status(401).json({ status: 'fail', message: 'User no longer exists.' });
      }
      
      req.user = user;
      return next();
    } catch (jwtError) {
      // If Custom JWT fails, attempt verification via Firebase Admin SDK (Google SSO)
      const decodedFirebase = await admin.auth().verifyIdToken(token);
      let user = await User.findOne({ $or: [{ googleId: decodedFirebase.uid }, { firebaseUid: decodedFirebase.uid }] });

      if (!user) {
        return res.status(401).json({ status: 'fail', message: 'User profile not found in database.' });
      }

      req.user = user;
      return next();
    }
  } catch (error) {
    return res.status(401).json({ message: 'Invalid or expired token' });
  }
};

module.exports = authenticateToken;