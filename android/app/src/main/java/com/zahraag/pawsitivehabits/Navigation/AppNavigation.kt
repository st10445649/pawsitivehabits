package com.zahraag.pawsitivehabits.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zahraag.pawsitivehabits.data.SampleData.sampleCalendarEvents
import com.zahraag.pawsitivehabits.data.SampleData.sampleExpenses
import com.zahraag.pawsitivehabits.data.SampleData.samplePetNamesMap
import com.zahraag.pawsitivehabits.data.SampleData.samplePets
import com.zahraag.pawsitivehabits.data.SampleData.sampleRoutines
import com.zahraag.pawsitivehabits.data.SampleData.sampleWeightRecords
import com.zahraag.pawsitivehabits.data.SampleData.sampleMedicalRecords
import com.zahraag.pawsitivehabits.data.models.UserSettings
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.screens.AddEditCalendarEventScreen
import com.zahraag.pawsitivehabits.screens.AddEditMedicalRecordScreen
import com.zahraag.pawsitivehabits.screens.AddExpenseScreen
import com.zahraag.pawsitivehabits.screens.AddRoutineScreen
import com.zahraag.pawsitivehabits.screens.AgendaScreen
import com.zahraag.pawsitivehabits.screens.EmergencyContactsScreen
import com.zahraag.pawsitivehabits.screens.ExpenseScreen
import com.zahraag.pawsitivehabits.screens.HomeScreen
import com.zahraag.pawsitivehabits.screens.LoginScreen
import com.zahraag.pawsitivehabits.screens.MainScreen
import com.zahraag.pawsitivehabits.screens.MedicalRecordsScreen
import com.zahraag.pawsitivehabits.screens.MemoriesScreen
import com.zahraag.pawsitivehabits.screens.PetDetailScreen
import com.zahraag.pawsitivehabits.screens.PetScreen
import com.zahraag.pawsitivehabits.screens.RegisterScreen
import com.zahraag.pawsitivehabits.screens.Screen
import com.zahraag.pawsitivehabits.screens.SettingsScreen
import com.zahraag.pawsitivehabits.screens.WeightScreen
import com.zahraag.pawsitivehabits.viewmodel.AuthUiState
import com.zahraag.pawsitivehabits.viewmodel.AuthViewModel
import com.zahraag.pawsitivehabits.viewmodel.PetViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    onGoogleSignInTriggered: () -> Unit = {}
) {
    val rootnavController = rememberNavController()
    val authUiState by authViewModel.uiState.collectAsState()

    val context = LocalContext.current.applicationContext
    val tokenManager = remember { TokenManager(context) }

    val currentUserId = tokenManager.getUserId() ?: ""

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.Success) {
            rootnavController.navigate("main") {
                popUpTo(Screen.Login.route) { inclusive = true }
                popUpTo(Screen.SignUp.route) { inclusive = true }
            }
        }
    }

    val startDestination = if (currentUserId.isNotBlank()) "main" else Screen.Login.route

    NavHost(
        navController = rootnavController,
        startDestination = startDestination
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                uiState = authUiState,
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                },
                onGoogleSignInClick = {
                    onGoogleSignInTriggered()
                },
                onNavigateToSignUp = {
                    rootnavController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            RegisterScreen(
                uiState = authUiState,
                onRegisterClick = { email, password, firstName, lastName ->
                    authViewModel.register(email, password, firstName, lastName)
                },
                onSignUpSuccess = {
                    rootnavController.navigate("main") {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    rootnavController.navigate(Screen.Login.route)
                }
            )
        }


        composable("main") {
            MainScreen(
rootnavController = rootnavController
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                pets= samplePets,
                selectedPetId= samplePets.first().id,
                onSelectPet={ id -> samplePets.first().id},
                onNavigateToPetDetails = { petId ->
                    rootnavController.navigate("pet_details/$petId")
                },
                onNavigateToFeature = {
                }
            )
        }


        composable(Screen.Pets.route) {
            val userId = tokenManager.getUserId() ?: ""
            val petViewModel: PetViewModel = viewModel()

            PetScreen(
                currentUserId = userId,
                viewModel = petViewModel,
                onViewDetails = { petId ->
                    rootnavController.navigate("pet_details/$petId")
                },
                onBackClick = {
                    rootnavController.popBackStack()
                }
           )
        }

        composable(
            route = "pet_details/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId")
            val selectedPet = samplePets.find { it.id == petId } ?: samplePets.first()

            PetDetailScreen(
                pet = selectedPet,
                onBackClick = { rootnavController.popBackStack() },
                onEditPetClick = { /* Open edit dialog/screen */ },
                onFeatureClick = { route ->
                    // Pass petId alongside the feature route so the next screen filters by this pet
                    rootnavController.navigate("$route/$petId")
                }
            )
        }

        composable(Screen.Agenda.route) {
            AgendaScreen(
                routinesList = sampleRoutines,
                calendarEventsList = sampleCalendarEvents,
                petNamesMap = samplePetNamesMap,
                onNavigateBack = { rootnavController.popBackStack() },
                onNavigateToAddRoutine = { rootnavController.navigate(Screen.AddRoutine.route) },
                onNavigateToAddCalendarEvent = { rootnavController.navigate(Screen.AddCalendarEvent.route) },
                onNavigateToEditCalendarEvent = { rootnavController.navigate(Screen.AddCalendarEvent.route) },
                onDeleteCalendarEvent = { },
            )
        }

        composable(Screen.AddCalendarEvent.route) {
            AddEditCalendarEventScreen(
                petsMap = samplePetNamesMap,
                existingEvent= null,
                currentUserId= "user123",
                onSaveEvent= {},
                onNavigateBack = { rootnavController.popBackStack() },
                )
        }

        composable(Screen.AddRoutine.route) {
            AddRoutineScreen(
                petsMap = samplePetNamesMap,
                currentUserId = "user123",
                onNavigateBack = { rootnavController.popBackStack() },
                onSaveRoutine = {}
            )
        }

        composable(Screen.Weight.route) {
            WeightScreen(
                petsMap = samplePetNamesMap,
                weightList = sampleWeightRecords,
                currentUserId = "user123",
                onNavigateBack = { rootnavController.popBackStack() },
            ) { }
        }

        composable(Screen.Expenses.route) {
            ExpenseScreen(
                onNavigateBack = { rootnavController.popBackStack() },
                expensesList = sampleExpenses,
                petsMap = samplePetNamesMap,
                onNavigateToAddExpense = { rootnavController.navigate(Screen.AddExpenses.route) },
                onNavigateToEditExpense = { rootnavController.navigate(Screen.AddExpenses.route) }
            ) { }
        }

        composable(Screen.AddExpenses.route){
            AddExpenseScreen(existingExpense =null,
                petsMap= samplePetNamesMap,
                currentUserId ="user123",
                onNavigateBack= { rootnavController.popBackStack() },
                onSaveExpense= {},
                onDeleteExpense ={})
        }


        composable(Screen.MedicalRecord.route){
            MedicalRecordsScreen(
                medicalList = sampleMedicalRecords,
                petsMap = samplePetNamesMap,
                onNavigateBack = { rootnavController.popBackStack() },
                onNavigateToAddRecord = { rootnavController.navigate(Screen.AddMedicalRecord.route) },
                onNavigateToEditRecord = { rootnavController.navigate(Screen.AddMedicalRecord.route) },
            ) { }
        }

        composable(Screen.AddMedicalRecord.route){
            AddEditMedicalRecordScreen(
                petsMap = samplePetNamesMap,
                onNavigateBack = { rootnavController.popBackStack() },
                existingRecord = null,
                currentUserId = "user123",
                onSaveRecord = { }
            ) { }
        }

        composable(Screen.Memories.route){
            MemoriesScreen(
                memoriesList = emptyList(),
                petsMap = samplePetNamesMap,
                currentUserId= "user123",
                onNavigateBack = { rootnavController.popBackStack() },
                onSaveMemory = { rootnavController.navigate(Screen.Memories.route) }
            ) { }
        }

        composable(Screen.Settings.route){
            SettingsScreen(
                userSettings = UserSettings(id = "user123"),
                userName = "John Doe",
                userEmail = "johndoe@gmail.com",
                onNavigateBack = { rootnavController.popBackStack() },
                onSaveSettings = { },
                onSyncDataClick = { }
            ) { }
        }

        composable(Screen.EmergencyContacts.route) {
            EmergencyContactsScreen(
                contactsList = emptyList(),
                onNavigateBack = { rootnavController.popBackStack() },
                onAddContact = { },
                onDeleteContact = { }
            )
        }

    }
}