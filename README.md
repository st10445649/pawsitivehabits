<img width="700" height="280" alt="image" src="https://github.com/user-attachments/assets/129f0c9a-bcd0-4ba2-bdf0-63110f02d550" />


**Pawsitive Habits**
-


**Demonstration Video**

Click here to watch the Pawsitive Habits Demo: 


**Description:**

***Pawsitive Habits*** is a play on the word ‘positive habits’, which is the main goal of the application. The  idea behind the app is to help owners set up positive habit tracking for their furry friends, whether it is setting up a brushing routine, tracking their weight or being diligent with their medical records. The app aims to make it easy to manage and care for your pets in a digital space.

The purpose of the app was to solve a challenge posed by pet owners trying to manage their pets’ daily responsibilities, wellness and healthcare in one place. By unifying routines, weight tracking, pet memories, etc., in one intuitive mobile platform, the application helps to transform passive tracking into proactive pet care management. 

The system design is tailored for real-time mobile interaction and modern cloud deployment. It makes use of decoupled layers like the presentation layer, backend services and cloud services. The UI will use modern Jetpack Compose design for an interactive UI, which will be built on an MVVM architectural pattern. Retrofit will handle all API connections and communication with the backend. The REST API handles all business logic and implements middleware for orchestrating security, error handling and logic separated into its own service and controller layers. This connects to the data layer, hosted online with MongoDB, which allows for flexible storage of entities and logs without rigid relational bottlenecks. Because of the authentication and notification features, Google Cloud Services and Firebase will be used for authentication and for real-time push notifications. For image storage for pet profile pictures, Supabase it used.

Overall, this helps to create a reliable solution for a modern pet wellness app aimed at improving the daily habits of both pet owners and their pets.


**Features**
-
**1. User Authentication**

- Secure Access: Users can register and log in to a personalised account.
- Data Privacy: Each user has exclusive access to their own pets and pet records.
- Validation: Input handling for secure passwords and valid email formats.

- **Google SSO:** Users will be able to use Google single sign-on for registering and logging into the app. 
This is done using Firebase Authentication.

**2. Pet Management** 

- Users can add a new pet to their profile, specifying details like breed, type, name, birth date, adoption date, microchip ID and others.
- Users can choose from a variety of pet types like dogs, cats, birds, rabbits, etc.
- Pets can be created with their own profile photo and linked to a custom colour that helps the user identify pets quickly throughout the app.
- Users can add as many pets as they have in their household and add, update and switch between pet profiles. Data logged for features like weight tracking
  will be linked to a selected pet.
  
**3. Calendar & Routine Management**

- Users will be able to configure custom daily routines for a selected pet with user-selected frequencies and date range.
- For example, if users want to add brushing teeth to their pet's routine, they can add that as a task, pick a time and choose how often to repeat. They will then be notified before the task using the real-time notification system implemented in later parts. 
- Additionally can log events to the calendar, like vet appointments or cattery visits.
- All events and routines are created using categories to improve routine management for common pet routines that owners might work with daily.
- Users can see the events and routines for the day on the home screen, where there is a section for upcoming events and a to-do list for the day. This makes it easy for the user to keep track of their pets habits without venturing far into the app.

**4. Health Tracking**
- Users can log periodic body weight entries and chart trends over a time period.
- Weight is added per pet, and the graph gives a good visual represnation of the pet's health trends

**5. Settings**








**6. Cloud Implementation:**

- PocketPenny uses MongoDB as our Backend-as-a-Service (BaaS) to provide real-time cloud synchronisation.
- NoSQL: Handles all pet and user-related data
- Supabase Storage: Securely hosts pet profile pictures

-Sync Logic: We use an OfflineSyncWorker (WorkManager) to handle data consistency. When a user is offline, data is queued locally. Once the network is restored, the worker triggers a background push to the API, ensuring the remote database reflects the user's latest state.

**7. Github Actions**

- To ensure code quality and seamless deployment, we use GitHub Actions. Every time code is pushed, the workflow automatically:
- Builds the project to check for compilation errors.
- Generates an APK/Bundle for testing
  
**8. Error Handling and Testing**

- Implemented Try/catch blocks with exception handling across screens.
- User-friendly messages are generated when a user leaves a field blank.
- Inputs from users are sanitised to ensure clean data entry and prevent database errors.

**Technologies Uses**
UI:
-IDE: Android Studio
-Language: Kotlin
-UI Framework: Jetpack Compose (Material 3)
-Database: Room SQLite (Offline), Supabase (Cloud Storage (Images))
-Asynchronous Processing: Kotlin Coroutines & Flow
-API Services: Retrofit, OkHttp Client
-CI/CD: GitHub Actions

REST API:
-IDE: Visual Studio Code
-Language: JavaScript
-Framework: Node.js
-Database: Supabase, MongoDB Atlas


**How to Run the Program:**
-

**Prerequisites:**
Android Studio: Download the latest version of Android Studio.

JDK: Java Development Kit (JDK) 17.

Android SDK: API Level 34 (Android 14) or higher.

Hardware/Emulator: An Android device or Emulator (e.g., BlueStacks or Android Studio Pixel Emulator).




Run the Application:
-
**Setup and Execution:**

1. Clone the Repository:
   
3. Open Project: Launch Android Studio and select Open, then navigate to the cloned folder.

4. Gradle Sync: Wait for the IDE to finish the Gradle sync and download necessary dependencies (Room, Compose, etc.).


**Run the Application:** Use your IDE's Run button

1. Select Target: Choose your connected physical device or a virtual emulator from the dropdown menu in the top toolbar.

2. Run: Click the green Run button


**Alternatively:**

Alternatively, you can download the latest pre-compiled APK from the Actions tab in this repository 
or from the releases section

