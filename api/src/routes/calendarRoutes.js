const express = require('express');
const router = express.Router();
const calendarController = require('../controllers/calendarController');
const authenticateToken = require('../middleware/authMiddleware');

router.use(authenticateToken);

// Calendar Events Routes
router.get('/calendar', calendarController.getCalendarEvents); 
router.post('/calendar', calendarController.saveCalendarEvent);
router.delete('/calendar/:eventId', calendarController.deleteCalendarEvent);

// Routine Routes
router.get('/routines', calendarController.getRoutines);
router.post('/routines', calendarController.saveRoutine);
router.delete('/routines/:routineId', calendarController.deleteRoutine);

// Routine Logs Routes
router.get('/routines/logs', calendarController.getRoutineLogs); 
router.post('/routines/logs', calendarController.saveRoutineLog);

module.exports = router;