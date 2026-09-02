const Pet = require('../models/Pet');

// Create a new pet linked to logged-in user
exports.createPet = async (req, res) => {
  try {

    const petData = { ...req.body };

    if (petData.id && !petData._id) {
      petData._id = petData.id;
      delete petData.id;
    }
    
    const pet = new Pet({
      ...petData,
      user: req.user.id 
    });

    await pet.save();

    res.status(201).json({ status: 'success', data: { pet } });
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};

// Get all pets belonging to logged-in user
exports.getUserPets = async (req, res) => {
  try {
    const userId = req.user.id || req.user._id;
    const pets = await Pet.find({ userId });

    res.status(200).json({ 
      status: 'success', 
      results: pets.length, 
      data: { pets } 
    });
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};

// Get single pet by ID 
exports.getPetById = async (req, res) => {
  try {
    const pet = await Pet.findOne({ _id: req.params.id, userId: req.user._id });
    if (!pet) {
      return res.status(404).json({ status: 'fail', message: 'Pet not found' });
    }
    res.status(200).json({ status: 'success', data: { pet } });
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};

// Delete pet
exports.deletePet = async (req, res) => {
  try {
    const pet = await Pet.findOneAndDelete({ _id: req.params.id, userId: req.user._id });
    if (!pet) {
      return res.status(404).json({ status: 'fail', message: 'Pet not found or unauthorized' });
    }
    res.status(204).send();
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};