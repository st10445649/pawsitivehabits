const express = require('express');
const router = express.Router();
const petController = require('../controllers/petController');
const authenticateToken = require('../middleware/authMiddleware');

router.use(authenticateToken);

router.post('/', petController.createPet);
router.get('/', petController.getUserPets);
router.get('/:id', petController.getPetById);
router.delete('/:id', petController.deletePet);

module.exports = router;