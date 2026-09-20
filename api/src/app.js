const express = require('express');

const app = express();

app.use(express.json());

const authRoutes = require("./routes/authRoutes");
const petRoutes = require("./routes/petRoutes");
const calendarRoutes = require("./routes/calendarRoutes")
const weightRoutes = require("./routes/weightRoutes")
app.get('/api/test', (req, res) => {
  res.status(200).json({
    status: 'success',
    message: 'Pawsitive Habits API is running',
    timestamp: new Date().toISOString()
  });
});

app.use("/auth", authRoutes);
app.use("/pets", petRoutes)
app.use("/", calendarRoutes)
app.use("/weights", weightRoutes)
module.exports = app;