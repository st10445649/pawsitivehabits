const express = require('express');
const router = express.Router();
const weightController = require('../controllers/weightController');
const authenticateToken = require('../middleware/authMiddleware');

router.use(authenticateToken);

router.post('/', weightController.addWeight);
router.get('/pet/:petId', weightController.getPetWeights);
router.get('/:id', weightController.getWeightById);
router.delete('/:id', weightController.deleteWeight);

module.exports = router;