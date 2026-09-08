const mongoose = require('mongoose');

const CalendarEventSchema = new mongoose.Schema({
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
    category: { type: String, required: true, trim:true},
    date: { type: Number, required: true }, 
    time: { type: String, default: '' },
    notes: { type: String, default: '' },
    reminderMinutes: {type: Number, default:'30'}
}, { timestamps: true });

module.exports = mongoose.model('CalendarEvent', CalendarEventSchema);