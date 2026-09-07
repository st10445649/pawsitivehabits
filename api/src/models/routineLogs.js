const mongoose = require('mongoose');

const RoutineLogSchema = new mongoose.Schema({
    id: { type: String, required: true, unique: true },
    routineId: {
                type: mongoose.Schema.Types.ObjectId,
                ref: 'Routine',
                required: true,
                index: true
              },
    petId: { type: mongoose.Schema.Types.ObjectId,
                ref: 'Pet',
                required: true,
                index: true },
    completedAt: { type: Number, required: true }
}, { timestamps: true });

module.exports = mongoose.model('RoutineLog', RoutineLogSchema);