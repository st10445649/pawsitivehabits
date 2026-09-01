const admin = require('../config/firebaseAdmin');
const jwt = require(jsonwebtoken)
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
      req.user = await User.findById(decoded.id);
      return next();
    } catch (jwtError) {
      // If Custom JWT fails, attempt verification via Firebase Admin SDK (Google SSO)
      const decodedFirebase = await admin.auth().verifyIdToken(token);
      req.user = await User.findOne({ firebaseUid: decodedFirebase.uid });
      return next();
    }
  } catch (error) {
    return res.status(401).json({ message: 'Invalid or expired token' });
  }
};

module.exports = authenticateToken;