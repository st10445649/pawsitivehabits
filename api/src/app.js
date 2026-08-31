const express = require('express');

const app = express();

app.use(express.json());

app.get('/api/test', (req, res) => {
  res.status(200).json({
    status: 'success',
    message: 'Pawsitive Habits API is running',
    timestamp: new Date().toISOString()
  });
});


app.use((req, res) => {
  res.status(404).json({
    status: 'fail',
    message: `Cannot find ${req.originalUrl} on this server`
  });
});

module.exports = app;