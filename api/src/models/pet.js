const mongoose = require('mongoose');

const petSchema = new mongoose.Schema({
  _id: { type: String },
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  name: { type: String, required: true,trim: true },
  gender: { type: String, required: true },
  petType: { type: String, required: true },
  breed: { type: String, default: null,trim: true },
  dateOfBirth: { type: Number, required: true },
  adoptionDate: { type: Number, required: true },
  microchipId: { type: String, default: null,trim: true },
  isNeutered: { type: Boolean, default: false },
  imageUrl: { type: String, default: null },
  localImageUrl: { type: String, default: null },
  remoteImageUrl: { type: String, default: null },
  colour: { type: String, default:null,trim: true},
  notes: { type: String, default:null},
  customColour: { type: String, default: null }
}, { timestamps: true });

module.exports = mongoose.model('Pet', petSchema);