package com.zahraag.pawsitivehabits.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zahraag.pawsitivehabits.data.Pet
import com.zahraag.pawsitivehabits.data.SampleData.samplePets
import com.zahraag.pawsitivehabits.screens.LoginScreen
import com.zahraag.pawsitivehabits.screens.MainScreen
import com.zahraag.pawsitivehabits.screens.PetScreen
import com.zahraag.pawsitivehabits.screens.RegisterScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    val startDestination = "Login"

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
                onNavigateToAddPet = {
                    navController.navigate("add_pet")
                }
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
    }
}