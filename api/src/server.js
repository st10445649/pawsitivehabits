const dotenv = require('dotenv');

dotenv.config();

const app = require('./app');

const connectDB = require('./config/database')

const PORT = process.env.PORT || 3000;

const APP_NAME = process.env.APP_NAME || "PawsitiveHabits"


const startServer = async() => {
    try{
        await connectDB();

app.listen(PORT, ()=>
{
    console.log(
              `${APP_NAME} is running securely on http://localhost:${PORT}`
    );
});
}catch(error){

    //if mongodb can't be reached
    console.error("ParkSmart should not start.")

    console.error(error.message);

    //end node.js process to stop the app from crashin. 1 - failure status 
    process.exit(1);
    }

};

startServer();
