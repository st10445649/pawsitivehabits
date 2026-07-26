package com.zahraag.pawsitivehabits.screens
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
    object Home : Screen("home")
    object AddPet : Screen("add_pet")
}