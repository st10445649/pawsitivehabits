const mongoose = require('mongoose');

const RoutineSchema = new mongoose.Schema({
    id: { type: String, required: true, unique: true },
    userId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
            index: true
          },
    petId: { type: mongoose.Schema.Types.ObjectId,
            ref: 'Pet',
            required: true,
            index: true },
    title: { type: String, required: true, trim:true},
    frequency: { type: String, required: true },
    startDate: { type: Number, required: true },
    repeatDays: { type: String, default: '' }
}, { timestamps: true });

module.exports = mongoose.model('Routine', RoutineSchema);