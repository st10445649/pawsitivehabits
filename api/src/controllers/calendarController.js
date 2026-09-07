const CalendarEvent = require('../models/CalendarEvent');
const Routine = require('../models/Routine');
const RoutineLog = require('../models/RoutineLog');

const getUserId = (req) => req.user?.id || req.user?._id;

exports.getCalendarEvents = async (req, res) => {
    try {
        const events = await CalendarEvent.find({ userId: req.params.userId });
        res.status(200).json(events);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
};

exports.saveCalendarEvent = async (req, res) => {
    try {
        const rawUserId = getUserId(req);
            if (!rawUserId) {
              return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
            }
        
            const userId = new mongoose.Types.ObjectId(rawUserId);
        const eventData = req.body;
        const event = await CalendarEvent.findOneAndUpdate(
            { id: eventData.id },
            eventData,
            { new: true, upsert: true }
        );
        res.status(201).json(event);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};

exports.deleteCalendarEvent = async (req, res) => {
    try {
        await CalendarEvent.findOneAndDelete({ id: req.params.eventId });
        res.status(200).json({ message: 'Event deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
};


exports.getRoutines = async (req, res) => {
    try {
        const routines = await Routine.find({ userId: req.params.userId });
        res.status(200).json(routines);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
};

exports.saveRoutine = async (req, res) => {
    try {
        const routineData = req.body;
        const routine = await Routine.findOneAndUpdate(
            { id: routineData.id },
            routineData,
            { new: true, upsert: true }
        );
        res.status(201).json(routine);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};

exports.deleteRoutine = async (req, res) => {
    try {
        await Routine.findOneAndDelete({ id: req.params.routineId });
        res.status(200).json({ message: 'Routine deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
};

exports.getRoutineLogs = async (req, res) => {
    try {
        const userRoutines = await Routine.find({ userId: req.params.userId }).select('id');
        const routineIds = userRoutines.map(r => r.id);
        const logs = await RoutineLog.find({ routineId: { $in: routineIds } });
        res.status(200).json(logs);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
};

exports.saveRoutineLog = async (req, res) => {
    try {
        const logData = req.body;
        const log = await RoutineLog.findOneAndUpdate(
            { id: logData.id },
            logData,
            { new: true, upsert: true }
        );
        res.status(201).json(log);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};