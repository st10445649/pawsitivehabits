const admin = require('firebase-admin');
const { cert } = require('firebase-admin/app');
const path = require('path');

const serviceAccountPath = process.env.FIREBASE_SERVICE_ACCOUNT_PATH || './serviceAccountKey.json';

try {
  const serviceAccount = require(path.resolve(serviceAccountPath));

  admin.initializeApp({
    credential: cert(serviceAccount)
  });

  console.log('Firebase Admin SDK initialized successfully');
} catch (error) {
  console.error('Failed to initialize Firebase Admin SDK:');
  console.error(error.message);
  process.exit(1);
}

module.exports = admin;