const admin = require('../config/firebaseAdmin');
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

    if (!token) {
      return res.status(401).json({
        status: 'fail',
        message: 'Malformed authorization header. Token is missing.'
      });
    }

    //  Verify token with Firebase Admin
    const decodedToken = await admin.auth().verifyIdToken(token);

    // Attach verified identity to req
    req.firebaseUser = decodedToken;

    // Fetch optional existing pawsitive habits profile from MongoDB
    const userProfile = await User.findOne({ firebaseUid: decodedToken.uid });
    if (userProfile) {
      req.user = userProfile;
    }

    next();
  } catch (error) {
    console.error('Authentication Error:', error.message);
    return res.status(401).json({
      status: 'fail',
      message: 'Invalid, expired, or unverified authentication token.'
    });
  }
};

module.exports = authenticateToken;