package com.zahraag.pawsitivehabits.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zahraag.pawsitivehabits.BottomNavItem
import com.zahraag.pawsitivehabits.data.remote.TokenManager
import com.zahraag.pawsitivehabits.data.repository.AuthRepository
import com.zahraag.pawsitivehabits.ui.theme.MintCardSurface
import com.zahraag.pawsitivehabits.viewmodel.CalendarViewModel
import com.zahraag.pawsitivehabits.viewmodel.HomeViewModel
import com.zahraag.pawsitivehabits.viewmodel.PetViewModel
import com.zahraag.pawsitivehabits.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(rootnavController: NavHostController){

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val context = LocalContext.current.applicationContext
    val tokenManager = remember { TokenManager(context) }

    val currentUserId = tokenManager.getUserId() ?: ""

    val bottomBarRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Pets.route,
        BottomNavItem.Agenda.route,
        BottomNavItem.Features.route,
        BottomNavItem.Settings.route
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
                        BottomNavItem.Settings
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
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel()

                val pets by homeViewModel.pets.collectAsStateWithLifecycle()
                val selectedPetId by homeViewModel.selectedPetId.collectAsStateWithLifecycle()
                val userName by homeViewModel.userName.collectAsStateWithLifecycle()
                val routines by homeViewModel.routines.collectAsStateWithLifecycle()
                val upcomingEvent by homeViewModel.upcomingEvent.collectAsStateWithLifecycle()
                val coroutineScope = rememberCoroutineScope()

                HomeScreen(
                    pets = pets,
                    selectedPetId = selectedPetId,
                    onSelectPet = { id -> homeViewModel.selectPet(id) },
                    onNavigateToPetDetails = { petId ->
                        rootnavController.navigate("pet_details/$petId")},
                    onNavigateToFeature = { featureRoute ->
                        rootnavController.navigate(featureRoute)
                    },
                    onLogout = {
                        coroutineScope.launch {
                            AuthRepository.logoutUser(context)
                            rootnavController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    userName = userName,
                    routines = routines,
                    event = upcomingEvent,
                    onToggleRoutine = { routineId, isCompleted ->
                        homeViewModel.toggleRoutine(routineId)
                    }
                )
            }

            composable(BottomNavItem.Pets.route) {
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
            composable(Screen.Agenda.route) {
                val vm: CalendarViewModel = viewModel()
                val state by vm.uiState.collectAsStateWithLifecycle()

                AgendaScreen(
                    routinesList = state.routines,
                    calendarEventsList = state.calendarEvents,
                    petNamesMap = state.petNamesMap,
                    selectedDate = state.selectedDate,
                    isLoading = state.isLoading,
                    onDateSelected = vm::onDateSelected,
                    onDeleteCalendarEvent = vm::deleteCalendarEvent,
                    onNavigateBack = { rootnavController.popBackStack() },
                    onDeleteRoutine = { routine -> vm.deleteRoutine(routine) },
                    onNavigateToAddRoutine = {
                        rootnavController
                            .currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<String>("routineToEditId")

                        rootnavController.navigate(Screen.AddRoutine.route)
                    },
                    onNavigateToEditRoutine = { routine ->
                        rootnavController
                            .currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("routineToEditId", routine.id)

                        rootnavController.navigate(Screen.AddRoutine.route)
                    },
                    onNavigateToAddCalendarEvent = {
                        rootnavController
                            .currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<String>("eventToEditId")

                        rootnavController.navigate(Screen.AddCalendarEvent.route)
                    },

                    onNavigateToEditCalendarEvent = { event ->
                        rootnavController
                            .currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("eventToEditId", event.id)

                        rootnavController.navigate(Screen.AddCalendarEvent.route)
                    }
                )
            }
            composable(BottomNavItem.Features.route) {
                FeaturesScreen(onFeatureClick = { route -> rootnavController.navigate(route) })
            }

            composable(BottomNavItem.Settings.route) {
                val vm: UserViewModel = viewModel()
                val coroutineScope = rememberCoroutineScope()

                val uiState by vm.uiState.collectAsStateWithLifecycle()

                SettingsScreen(
                    uiState = uiState,
                    pets = uiState.pets,
                    onNavigateBack = { rootnavController.popBackStack() },
                    onSaveSettings = { updatedSettings ->
                        vm.saveSettings(updatedSettings)
                    },
                    onSyncDataClick = {
                        vm.syncAllData()
                        vm.loadUserData()
                    },
                    onExportDataClick = { petId, uri ->
                        vm.exportPetData(petId, uri)
                    },
                    onLogout = {
                        coroutineScope.launch {
                            AuthRepository.logoutUser(context)
                            rootnavController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

        }
    }

}