const mongoose = require('mongoose');

const connectDB = async () => {
 const mongoURI = process.env.MONGODB_URI;

   if(!mongoURI){
    throw new Error(
        "MONGODB_URI is not ready/configured"
    );
   }

   const conn = await mongoose.connect(mongoURI);

    console.log(`Pawsitive Habits connected to MongoDB successfully: ${conn.connection.host}`);

   console.log("Pawsitive Habits connected to MongoDB succesfully")
};

module.exports = connectDB;