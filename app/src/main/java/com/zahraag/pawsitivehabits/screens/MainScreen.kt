package com.zahraag.pawsitivehabits.screens

import android.R.attr.contentDescription
import android.R.attr.onClick
import android.R.attr.padding
import android.R.attr.text
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomAppBarState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zahraag.pawsitivehabits.BottomNavItem
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.data.SampleData.samplePets
import com.zahraag.pawsitivehabits.ui.theme.MintBackground
import com.zahraag.pawsitivehabits.ui.theme.MintCardSurface
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.MintPrimary
import com.zahraag.pawsitivehabits.ui.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(){

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Pets.route,
        BottomNavItem.Agenda.route,
        BottomNavItem.Features.route,
        BottomNavItem.Profile.route
    )

    Scaffold(
        bottomBar = {
            if(currentRoute in bottomBarRoutes){
                NavigationBar(
                    containerColor = Color(0xff6ebc99),
                    tonalElevation = 8.dp
                ) {
                    val navItems = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Pets,
                        BottomNavItem.Agenda,
                        BottomNavItem.Features,
                        BottomNavItem.Profile
                    )

                    navItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick ={
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route){
                                        popUpTo(navController.graph.startDestinationId){
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                                modifier = Modifier.size(23.dp)
                            )} ,
                            label = { Text(item.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MintCardSurface
                            ) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = MintCardSurface.copy(alpha = 0.7f),
                                selectedTextColor = Color.White,
                                unselectedTextColor = MintCardSurface.copy(alpha = 0.7f),
                                indicatorColor = MintCardSurface

                            )
                        )
                    }
                }
            }
        }
    ){ paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
//                HomeScreen(
//                    onNavigateToPetDetails = { petId -> navController.navigate("pet_detail/$petId") },
//                    onNavigateToFeatures = { navController.navigate(BottomNavItem.Features.route) }
//                )
            }
            composable(BottomNavItem.Pets.route) {
                PetScreen(
                    pets = samplePets,
                    selectedPetId = null,
                    onSelectPet = { selectedId -> /* Update selected pet */ },
                    onAddPetSubmitted = { newPet -> /* Save pet */ }
                )
            }
            composable(BottomNavItem.Agenda.route) {
                AgendaScreen(
                    onNavigateBack = {},
                    onNavigateToAddRoutine = { },
                )
            }
            composable(BottomNavItem.Features.route) {
                FeaturesScreen(onFeatureClick = { route -> navController.navigate(route) })
            }
            composable("weight") {
                //WeightTrackerScreen(onOpenAddDialog = { navController.navigate("add_weight") })
            }

            composable("weight") {
               WeightScreen(
                   onNavigateBack = {}
               ) { }
            }

        }
    }

}