const admin = require('firebase-admin');
const { initializeApp, cert, getApps } = require('firebase-admin/app');
const { getAuth } = require('firebase-admin/auth');
const path = require('path');

const serviceAccountPath = process.env.FIREBASE_SERVICE_ACCOUNT_PATH || './serviceAccountKey.json';

let app;

try {
  if (!getApps().length) {
    const serviceAccount = require(path.resolve(serviceAccountPath));

    app = initializeApp({
      credential: cert(serviceAccount)
    });

    console.log('Firebase Admin SDK initialized successfully');
  } else {
    app = getApps()[0];
  }
} catch (error) {
  console.error('Failed to initialize Firebase Admin SDK:');
  console.error(error.message);
  process.exit(1);
}

// Bind auth directly so admin.auth() always resolves
admin.auth = () => getAuth(app);

module.exports = admin;