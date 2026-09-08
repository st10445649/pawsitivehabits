const CalendarEvent = require('../models/CalendarEvent');
const Routine = require('../models/Routine');
const RoutineLog = require('../models/RoutineLog');
const mongoose = require('mongoose');

const getUserId = (req) => req.user?.id || req.user?._id;

exports.getCalendarEvents = async (req, res) => {
    try {
        const rawUserId = getUserId(req) || req.params.userId;
        if (!rawUserId) {
            return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
        }

        const userId = new mongoose.Types.ObjectId(rawUserId);
        const events = await CalendarEvent.find({ userId });
        
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
        const eventId = req.body._id || req.body.id;

        if (!eventId) {
            return res.status(400).json({ status: 'fail', message: 'Event ID is required' });
        }

        const event = await CalendarEvent.findOneAndUpdate(
            { _id: eventId },
            { $set: { ...req.body, _id: eventId, userId } },
            { new: true, upsert: true, runValidators: true, setDefaultsOnInsert: true }
        );

        res.status(201).json(event);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};

exports.deleteCalendarEvent = async (req, res) => {
    try {
        const { eventId } = req.params;
        await CalendarEvent.findOneAndDelete({ 
            $or: [{ _id: eventId }, { id: eventId }] 
        });
        res.status(200).json({ message: 'Event deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
};


exports.getRoutines = async (req, res) => {
    try {
        const rawUserId = getUserId(req) || req.params.userId;
        if (!rawUserId) {
            return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
        }

        const userId = new mongoose.Types.ObjectId(rawUserId);
        const routines = await Routine.find({ userId });
        res.status(200).json(routines);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
};

exports.saveRoutine = async (req, res) => {
    try {
        const rawUserId = getUserId(req);
        if (!rawUserId) {
            return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
        }

        const userId = new mongoose.Types.ObjectId(rawUserId);
        const routineId = req.body._id || req.body.id;

        if (!routineId) {
            return res.status(400).json({ status: 'fail', message: 'Routine ID is required' });
        }

        const routine = await Routine.findOneAndUpdate(
            { _id: routineId, userId },
            { $set: { ...req.body, _id: routineId, userId } },
            { new: true, upsert: true, runValidators: true, setDefaultsOnInsert: true }
        );
        res.status(201).json(routine);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};

exports.deleteRoutine = async (req, res) => {
    try {
        const { routineId } = req.params;
        await Routine.findOneAndDelete({ 
            $or: [{ _id: routineId }, { id: routineId }] 
        });
        res.status(200).json({ message: 'Routine deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
};

exports.getRoutineLogs = async (req, res) => {
    try {
        const rawUserId = getUserId(req) || req.params.userId;
        if (!rawUserId) {
            return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
        }

        const userId = new mongoose.Types.ObjectId(rawUserId);
        const userRoutines = await Routine.find({ userId }).select('_id id');
        const routineIds = userRoutines.map(r => r._id || r.id);
        
        const logs = await RoutineLog.find({ routineId: { $in: routineIds } });
        res.status(200).json(logs);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
};

exports.saveRoutineLog = async (req, res) => {
    try {
        const logId = req.body._id || req.body.id || new mongoose.Types.ObjectId();

        const logData = {
            ...req.body,
            _id: logId
        };

        const log = await RoutineLog.findOneAndUpdate(
            { _id: logId },
            logData,
            { new: true, upsert: true, runValidators: true }
        );
        res.status(201).json(log);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
};