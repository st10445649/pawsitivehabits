const express = require('express');

const app = express();

app.use(express.json());

const authRoutes = require("./routes/authRoutes");

app.get('/api/test', (req, res) => {
  res.status(200).json({
    status: 'success',
    message: 'Pawsitive Habits API is running',
    timestamp: new Date().toISOString()
  });
});

app.use("/auth", authRoutes);

module.exports = app;