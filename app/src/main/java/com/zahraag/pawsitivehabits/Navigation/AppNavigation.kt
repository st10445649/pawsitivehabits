package com.zahraag.pawsitivehabits.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zahraag.pawsitivehabits.data.Pet
import com.zahraag.pawsitivehabits.data.SampleData.sampleCalendarEvents
import com.zahraag.pawsitivehabits.data.SampleData.samplePetNamesMap
import com.zahraag.pawsitivehabits.data.SampleData.samplePets
import com.zahraag.pawsitivehabits.data.SampleData.sampleRoutines
import com.zahraag.pawsitivehabits.data.UserSettings
import com.zahraag.pawsitivehabits.screens.AddEditCalendarEventScreen
import com.zahraag.pawsitivehabits.screens.AddEditMedicalRecordScreen
import com.zahraag.pawsitivehabits.screens.AddExpenseScreen
import com.zahraag.pawsitivehabits.screens.AddRoutineScreen
import com.zahraag.pawsitivehabits.screens.AgendaScreen
import com.zahraag.pawsitivehabits.screens.EmergencyContactsScreen
import com.zahraag.pawsitivehabits.screens.ExpenseScreen
import com.zahraag.pawsitivehabits.screens.LoginScreen
import com.zahraag.pawsitivehabits.screens.MainScreen
import com.zahraag.pawsitivehabits.screens.MedicalRecordsScreen
import com.zahraag.pawsitivehabits.screens.MemoriesScreen
import com.zahraag.pawsitivehabits.screens.PetScreen
import com.zahraag.pawsitivehabits.screens.RegisterScreen
import com.zahraag.pawsitivehabits.screens.Screen
import com.zahraag.pawsitivehabits.screens.SettingsScreen
import com.zahraag.pawsitivehabits.screens.WeightScreen
import kotlin.String

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val rootnavController = rememberNavController()

    val startDestination = "login"

    NavHost(
        navController = rootnavController,
        startDestination = startDestination
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {

                    rootnavController.navigate("main") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoogleSignInClick = {

                },
                onNavigateToSignUp = {
                    rootnavController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            RegisterScreen(
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


        composable(Screen.AddPet.route) {
            PetScreen(
                pets= samplePets,
                selectedPetId= null,
                onSelectPet={},
                onAddPetSubmitted={}
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
                weightList = emptyList(),
                currentUserId = "user123",
                onNavigateBack = { rootnavController.popBackStack() },
            ) { }
        }

        composable(Screen.Expenses.route) {
            ExpenseScreen(
                onNavigateBack = { rootnavController.popBackStack() },
                expensesList = emptyList(),
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
                medicalList = emptyList(),
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