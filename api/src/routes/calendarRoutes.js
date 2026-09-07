const express = require('express');
const router = express.Router();
const calendarController = require('../controllers/calendarController');

// Calendar Events Routes
router.get('/calendar/:userId', calendarController.getCalendarEvents);
router.post('/calendar', calendarController.saveCalendarEvent);
router.delete('/calendar/:eventId', calendarController.deleteCalendarEvent);

// Routine Routes
router.get('/routines/:userId', calendarController.getRoutines);
router.post('/routines', calendarController.saveRoutine);
router.delete('/routines/:routineId', calendarController.deleteRoutine);

// Routine Logs Routes
router.get('/routines/logs/:userId', calendarController.getRoutineLogs);
router.post('/routines/logs', calendarController.saveRoutineLog);

module.exports = router;