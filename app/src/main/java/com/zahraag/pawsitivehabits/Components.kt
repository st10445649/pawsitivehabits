package com.zahraag.pawsitivehabits

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: Int
){

    object Home: BottomNavItem("home", "Home", R.drawable.homenav)
    object Pets: BottomNavItem("pets", "Pets", R.drawable.petnav)
    object Agenda: BottomNavItem("agenda", "Agenda", R.drawable.calendarnav)
    object Features: BottomNavItem("features", "Features", R.drawable.homenav)
    object Profile: BottomNavItem("profile", "Profile", R.drawable.profilenav)

}