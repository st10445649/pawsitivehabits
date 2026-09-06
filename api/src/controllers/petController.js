const Pet = require('../models/Pet');

const getUserId = (req) => req.user?.id || req.user?._id;

exports.createPet = async (req, res) => {
  try {
    const petData = { ...req.body };
    const currentUserId = getUserId(req);

    const petId = petData._id || petData.id;
    if (!petId) {
      return res.status(400).json({ status: 'fail', message: 'Pet ID is required' });
    }

    if (!petData.name || !petData.name.trim()) {
      return res.status(400).json({ status: 'fail', message: 'Pet name is required' });
    }

    const remoteImageUrl = petData.remoteImageUrl || petData.imageUrl || null;

    const updatePayload = {
      _id: petId,
      userId: currentUserId,
      name: petData.name.trim(),
      gender: petData.gender,
      petType: petData.petType,
      breed: petData.breed ? petData.breed.trim() : null,
      dateOfBirth: petData.dateOfBirth,
      adoptionDate: petData.adoptionDate,
      microchipId: petData.microchipId ? petData.microchipId.trim() : null,
      isNeutered: petData.isNeutered || false,
      localImageUrl: petData.localImageUrl || null,
      remoteImageUrl,
      colour: petData.colour,
      notes: petData.notes ? petData.notes.trim() : null,
      customColour: petData.customColour
    };

    const pet = await Pet.findOneAndUpdate(
      { _id: petId, userId: currentUserId },
      updatePayload,
      { new: true, upsert: true, runValidators: true }
    );

    res.status(201).json({ status: 'success', data: { pet } });
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};

// Update existing pet by ID (PUT /pets/:id)
exports.updatePet = async (req, res) => {
  try {
    const currentUserId = getUserId(req);
    const petId = req.params.id;

    const petData = { ...req.body };

    // Trim string inputs if present
    if (petData.name) petData.name = petData.name.trim();
    if (petData.breed) petData.breed = petData.breed.trim();
    if (petData.microchipId) petData.microchipId = petData.microchipId.trim();
    if (petData.notes) petData.notes = petData.notes.trim();

    const updatedPet = await Pet.findOneAndUpdate(
      { _id: petId, userId: currentUserId },
      petData,
      { new: true, runValidators: true }
    );

    if (!updatedPet) {
      return res.status(404).json({ status: 'fail', message: 'Pet not found or unauthorized' });
    }

    res.status(200).json({ status: 'success', data: { pet: updatedPet } });
  } catch (error) {
    res.status(400).json({ status: 'fail', message: error.message });
  }
};

// Get all pets belonging to logged-in user
exports.getUserPets = async (req, res) => {
  try {
    const userId = getUserId(req);
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
    const userId = getUserId(req);
    const pet = await Pet.findOneAndDelete({ _id: req.params.id, userId });
    if (!pet) {
      return res.status(404).json({ status: 'fail', message: 'Pet not found or unauthorized' });
    }
    res.status(204).send();
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};