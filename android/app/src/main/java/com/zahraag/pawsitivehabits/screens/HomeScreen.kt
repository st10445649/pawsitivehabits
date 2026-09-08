package com.zahraag.pawsitivehabits.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.ui.theme.*

data class RoutineItem(
    val id: String,
    val title: String,
    val time: String,
    var isCompleted: Boolean = false
)

@Composable
fun HomeScreen(
    pets: List<Pet>,
    selectedPetId: String?,
    onSelectPet: (String) -> Unit,
    onNavigateToPetDetails: (String) -> Unit,
    onNavigateToFeature: (route: String) -> Unit,
    onLogout: () -> Unit
) {
    val activePet = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()

    var routines by remember {
        mutableStateOf(
            listOf(
                RoutineItem("1", "Morning Walk & Exercise", "07:30 AM", true),
                RoutineItem("2", "Breakfast Feeding & Fresh Water", "08:00 AM", true),
                RoutineItem("3", "Evening Walk", "05:30 PM", false),
                RoutineItem("4", "Grooming & Brushing", "07:00 PM", false)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello! John",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted
                    )
                    Text(
                        text = "Pawsitive Habits",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MintDarkGreen
                    )
                }

                if (activePet != null) {
                    Surface(
                        color = SurfaceWhite,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clickable { onNavigateToPetDetails(activePet.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MintCardSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.petnav),
                                    contentDescription = null,
                                    tint = MintDarkGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = activePet.name,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceWhite, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = MintDarkGreen
                    )
                }
            }

            if (pets.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(pets) { pet ->
                        val isSelected = pet.id == selectedPetId
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectPet(pet.id) },
                            label = { Text(pet.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MintDarkGreen,
                                selectedLabelColor = SurfaceWhite,
                                containerColor = SurfaceWhite,
                                labelColor = TextDark
                            ),
                            shape = RoundedCornerShape(50)
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SurfaceWhite,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = MintDarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Next Upcoming Event",
                            style = MaterialTheme.typography.labelSmall,
                            color = MintDarkGreen.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Annual Checkup for ${activePet?.name ?: "Pet"}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MintDarkGreen
                        )
                        Text(
                            text = "14 Aug 2026 • 10:00 AM",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }

             val completedCount = routines.count { it.isCompleted }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Routines",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextDark
                )
                Text(
                    text = "$completedCount of ${routines.size} done",
                    style = MaterialTheme.typography.labelMedium,
                    color = MintPrimary
                )
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    routines.forEach { routine ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    routines = routines.map {
                                        if (it.id == routine.id) it.copy(isCompleted = !it.isCompleted) else it
                                    }
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (routine.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                contentDescription = null,
                                tint = if (routine.isCompleted) MintDarkGreen else TextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = routine.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        textDecoration = if (routine.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    color = if (routine.isCompleted) TextMuted else TextDark
                                )
                                Text(
                                    text = routine.time,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

               Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeShortcutButton(
                    title = "+ Expense",
                    color = MintCardSurface,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToFeature("expenses") }
                )
                HomeShortcutButton(
                    title = "+ Weight",
                    color = MintCardSurface,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToFeature("weight_tracker") }
                )
                HomeShortcutButton(
                    title = "+ Medical",
                    color = MintCardSurface,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToFeature("medical_records") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HomeShortcutButton(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MintDarkGreen
            )
        }
    }
}