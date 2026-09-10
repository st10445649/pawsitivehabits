const mongoose = require('mongoose');
const Weight = require('../models/Weight');

const getUserId = (req) => req.user?.id || req.user?._id;

exports.addWeight = async (req, res) => {
  try {
    const rawUserId = getUserId(req);
    if (!rawUserId) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }

    const userId = new mongoose.Types.ObjectId(rawUserId);
   
    const weightId = req.body._id || req.body.id || new mongoose.Types.ObjectId();

    const weightData = {
      ...req.body,
      _id: weightId,
      userId
    };

    const weight = await Weight.findOneAndUpdate(
      { _id: weightId, userId },
      { $set: weightData },
      { new: true, upsert: true, runValidators: true, setDefaultsOnInsert: true }
    );

    res.status(201).json({
      status: 'success',
      data: { weight }
    });
  } catch (error) {
    res.status(400).json({ status: 'fail', message: error.message });
  }
};


exports.getPetWeights = async (req, res) => {
  try {
    const rawUserId = getUserId(req);
    if (!rawUserId) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }

    const userId = new mongoose.Types.ObjectId(rawUserId);
    const { petId } = req.params;

    const weights = await Weight.find({ petId, userId }).sort({ date: -1 });

    res.status(200).json({
      status: 'success',
      results: weights.length,
      data: { weights }
    });
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};

exports.getWeightById = async (req, res) => {
  try {
    const rawUserId = getUserId(req);
    if (!rawUserId) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }

    const userId = new mongoose.Types.ObjectId(rawUserId);
    const weight = await Weight.findOne({ _id: req.params.id, userId });

    if (!weight) {
      return res.status(404).json({ status: 'fail', message: 'Weight record not found' });
    }

    res.status(200).json({
      status: 'success',
      data: { weight }
    });
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};

exports.deleteWeight = async (req, res) => {
  try {
    const rawUserId = getUserId(req);
    if (!rawUserId) {
      return res.status(401).json({ status: 'fail', message: 'Unauthorized' });
    }

    const userId = new mongoose.Types.ObjectId(rawUserId);
    const weight = await Weight.findOneAndDelete({ _id: req.params.id, userId });

    if (!weight) {
      return res.status(404).json({ status: 'fail', message: 'Weight record not found or unauthorized' });
    }

    res.status(204).send();
  } catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
  }
};