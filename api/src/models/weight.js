const mongoose = require('mongoose');

const WeightSchema = new mongoose.Schema({
    _id: { type: String, required: true},
    userId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
            index: true
          },
    petId: { type: String,
            ref: 'Pet',
            required: true,
            index: true },
    weightValue: { type: Number, required:true},
    unit: { type: String ,default: 'kg'},
    date: { type: Number, required: true }
}, { timestamps: true });

module.exports = mongoose.model('Weight', WeightSchema);