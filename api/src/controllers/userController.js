const { User } = require('../models/User');


exports.syncUser = async (req, res) => {
  try {
    const { uid, email, name, picture, firebase} = req.firebaseUser;
    const { firstName, lastName} = req.body;

    // Search for existing user profile
    let user = await User.findOne({ firebaseUid: uid });

    if (!user) {
      const provider = firebase?.sign_in_provider || 'password';
      const userDisplayName = name || `${firstName || ''} ${lastName || ''}`.trim() || 'Pawsitive Habits User';

      // First-time user creation (
      user = await User.create({
        firebaseUid: uid,
        email: email || '',
        firstName: firstName || '',
        lastName: lastName || '',
        displayName: userDisplayName || 'Pawsitive Habits User',
        photoURL: picture || '',
        authProvider: provider || ''
      });
      console.log(`Created new ${provider} user profile for UID: ${uid}`);
    } else {
      console.log(`Synchronized existing user profile for UID: ${uid}`);
    }

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