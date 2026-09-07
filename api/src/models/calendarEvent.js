const mongoose = require('mongoose');

const CalendarEventSchema = new mongoose.Schema({
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
    eventDate: { type: Number, required: true }, 
    eventTime: { type: String, default: '' },
    notes: { type: String, default: '' }
}, { timestamps: true });

module.exports = mongoose.model('CalendarEvent', CalendarEventSchema);