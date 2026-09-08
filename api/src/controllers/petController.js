const mongoose = require('mongoose');
const Pet = require('../models/Pet');

const getUserId = (req) => req.user?.id || req.user?._id;

exports.createPet = async (req, res) => {
  try {
    const rawUserId = getUserId(req);
    if (!rawUserId) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }

    // Cast string userId into valid Mongoose ObjectId for relational queries
    const userId = new mongoose.Types.ObjectId(rawUserId);
    const petId = req.body._id || req.body.id;

    if (!petId) {
      return res.status(400).json({ status: 'fail', message: 'Pet ID is required' });
    }

    // Spread request body directly & override key parameters
    const petData = {
      ...req.body,
      _id: petId,
      userId
    };

    if (petData.remoteImageUrl || petData.imageUrl) {
      petData.remoteImageUrl = petData.remoteImageUrl || petData.imageUrl;
    }

    const pet = await Pet.findOneAndUpdate(
      { _id: petId, userId },
      { $set: { ...req.body, _id: petId, userId } },
      { new: true, upsert: true, runValidators: true, setDefaultsOnInsert: true }
    );

    res.status(201).json({ status: 'success', data: { pet } });
  } catch (error) {
    res.status(400).json({ status: 'fail', message: error.message });
  }
};

// Update existing pet by ID (PUT /pets/:id)
exports.updatePet = async (req, res) => {
  try {
    const rawUserId = req.user?.id || req.user?._id;
    if (!rawUserId) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }

    const userId = new mongoose.Types.ObjectId(rawUserId);
    const petId = req.params.id;

    const petData = {
      ...req.body,
      _id: petId,
      userId
    };

    const pet = await Pet.findOneAndUpdate(
      { _id: petId, userId },
      { $set: petData },
      { new: true, upsert: true, runValidators: true, setDefaultsOnInsert: true }
    );

    res.status(200).json({ status: 'success', data: { pet } });
  } catch (error) {
    res.status(400).json({ status: 'fail', message: error.message });
  }
};

// Get all pets belonging to logged-in user
exports.getUserPets = async (req, res) => {
  try {
    const userId = req.user?.id || req.user?._id;

    if (!userId) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }
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
    const userId = getUserId(req);
    const pet = await Pet.findOne({ _id: req.params.id, userId });
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
    const rawUserId = getUserId(req);
    if (!rawUserId) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }

    const userId = new mongoose.Types.ObjectId(rawUserId);

    const pet = await Pet.findOneAndDelete({ _id: req.params.id, userId });

    if (!pet) {
      return res.status(404).json({ status: 'fail', message: 'Pet not found or unauthorized' });
    }

    res.status(204).send();
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};