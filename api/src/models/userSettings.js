const mongoose = require('mongoose');

const UserSettingsSchema = new mongoose.Schema({
    _id: { type: String, required: true },
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true,
        unique: true,
        index: true
    },
    language: { type: String, default: 'en' },
    weightUnit: { type: String, default: 'kg' },
    notificationsEnabled: { type: Boolean, default: true },
    biometricLockEnabled: { type: Boolean, default: false }
}, { timestamps: true });

module.exports = mongoose.model('UserSettings', UserSettingsSchema);