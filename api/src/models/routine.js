const mongoose = require('mongoose');

const RoutineSchema = new mongoose.Schema({
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
    title: { type: String, required: true, trim:true},
    time: { type: String ,default: ''},
    frequency: { type: String, required: true },
    startDate: { type: Number, required: true },
    endDate: { type: Number, required: true },
    repeatDays: { type: String, default: '' }
}, { timestamps: true });

module.exports = mongoose.model('Routine', RoutineSchema);