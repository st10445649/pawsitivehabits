const mongoose = require('mongoose');
const bcrypt = require("bcrypt")

const userSchema = new mongoose.Schema(
  {
    firebaseUid: {
      type: String,
      unique: true,
      sparse: true
    },
    email: {
      type: String,
      required: [true, 'Email address is required'],
      unique:true,
      lowercase: true,
      trim: true
    },
    password: {
      type: String,
      // used only when signing up through form not SSO
      required: function () {
        return this.authProvider === 'password';
      },
      select: false // Excludes password hash from default query results
    },
    firstName: {
      type: String,
      trim: true,
      default: ''
    },
    lastName: {
      type: String,
      trim: true,
      default: ''
    },
    displayName: {
      type: String,
      trim: true,
      default: 'Pawsitive Habits User'
    },
    photoURL: {
      type: String,
      default: ''
    },
    authProvider: {
      type: String,
      enum: ['password', 'google.com'],
      default: 'password'
    }
  },
  {
    timestamps: true
  }
);

userSchema.pre('save', async function (next) {
  if (!this.isModified('password') || !this.password) return next();

  const salt = await bcrypt.genSalt(10);
  this.password = await bcrypt.hash(this.password, salt);
  next();
});

userSchema.methods.comparePassword = async function (enteredPassword) {
  return await bcrypt.compare(enteredPassword, this.password);
};

const User = mongoose.model('User', userSchema);

module.exports = { User };