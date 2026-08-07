package com.zahraag.pawsitivehabits.screens
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
    object Home : Screen("home")
    object AddPet : Screen("add_pet")
    object Agenda : Screen("agenda")
    object AddRoutine : Screen("add_routine")
    object AddExpenses: Screen("add_expense")
    object Expenses : Screen("expenses")
    object Weight : Screen("weight")
    object MedicalRecord : Screen("medical_record")
    object AddMedicalRecord : Screen("add_medical_record")
}