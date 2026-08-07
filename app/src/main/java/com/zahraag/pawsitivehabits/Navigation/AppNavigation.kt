package com.zahraag.pawsitivehabits.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zahraag.pawsitivehabits.data.Pet
import com.zahraag.pawsitivehabits.data.SampleData.samplePets
import com.zahraag.pawsitivehabits.screens.AddEditMedicalRecordScreen
import com.zahraag.pawsitivehabits.screens.AddExpenseScreen
import com.zahraag.pawsitivehabits.screens.AddRoutineScreen
import com.zahraag.pawsitivehabits.screens.AgendaScreen
import com.zahraag.pawsitivehabits.screens.ExpenseScreen
import com.zahraag.pawsitivehabits.screens.LoginScreen
import com.zahraag.pawsitivehabits.screens.MainScreen
import com.zahraag.pawsitivehabits.screens.MedicalRecordsScreen
import com.zahraag.pawsitivehabits.screens.PetScreen
import com.zahraag.pawsitivehabits.screens.RegisterScreen
import com.zahraag.pawsitivehabits.screens.Screen
import com.zahraag.pawsitivehabits.screens.WeightScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    val startDestination = "medical_record"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {

                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoogleSignInClick = {

                },
                onNavigateToSignUp = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onSignUpSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }


        composable("main") {
            MainScreen(

            )
        }


        composable("add_pet") {
            PetScreen(
                pets= samplePets,
                selectedPetId= null,
                onSelectPet={},
                onAddPetSubmitted={}
           )
        }

        composable("agenda") {

            AgendaScreen(
                routinesList = emptyList(),
                calendarEventsList = emptyList(),
                petNamesMap = emptyMap(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddRoutine = { navController.navigate("add_routine") }
            )
        }

        composable("add_routine") {

            AddRoutineScreen(
                petsMap = emptyMap(),
                currentUserId = "user123",
                onNavigateBack = { navController.popBackStack() },
                onSaveRoutine = {}
            )
        }

        composable("weight") {
            WeightScreen(
                petsMap = emptyMap(),
                weightList = emptyList(),
                currentUserId = "user123",
                onNavigateBack = { navController.popBackStack() },
            ) { }
        }

        composable("expenses") {
            ExpenseScreen(
                onNavigateBack = { navController.popBackStack() },
                expensesList = emptyList(),
                petsMap = emptyMap(),
                onNavigateToAddExpense = { navController.navigate("add_expense") },
                onNavigateToEditExpense = { navController.navigate("add_routine") }
            ) { }
        }

        composable("add_expense"){
            AddExpenseScreen(existingExpense =null,
                petsMap= emptyMap(),
                currentUserId ="user123",
                onNavigateBack= { navController.popBackStack() },
                onSaveExpense= {},
                onDeleteExpense ={})
        }


        composable("medical_record"){
            MedicalRecordsScreen(
                medicalList = emptyList(),
                petsMap = emptyMap(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddRecord = { navController.navigate("add_medical_record") },
                onNavigateToEditRecord = { navController.navigate("add_medical_record") },
            ) { }
        }

        composable("add_medical_record"){
            AddEditMedicalRecordScreen(
                petsMap = emptyMap(),
                onNavigateBack = { navController.popBackStack() },
                existingRecord = null,
                currentUserId = "user123",
                onSaveRecord = { }
            ) { }
        }
    }
}