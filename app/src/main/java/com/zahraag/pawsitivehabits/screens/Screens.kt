package com.zahraag.pawsitivehabits.screens
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("register")
    object Home : Screen("home")
    object AddPet : Screen("add_pet")
    object Agenda : Screen("agenda")
    object AddRoutine : Screen("add_routine")
    object AddCalendarEvent : Screen("add_calendar_event")
    object EditCalendarEvent : Screen("edit_calendar_event/{eventId}") {
        fun createRoute(eventId: String) = "edit_calendar_event/$eventId"
    }
    object AddExpenses: Screen("add_expense")
    object Expenses : Screen("expenses")
    object Weight : Screen("weight")
    object MedicalRecord : Screen("medical_record")
    object AddMedicalRecord : Screen("add_medical_record")
    object Memories : Screen("memory")
    object EmergencyContacts : Screen("emergency_contacts")
    object Settings : Screen("setting")

}