package com.zahraag.pawsitivehabits.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.outlinedCardBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.play.integrity.internal.ac
import com.zahraag.pawsitivehabits.BottomNavItem.Agenda.title
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
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
    userName: String,
    pets: List<Pet>,
    selectedPetId: String?,
    routines: List<RoutineItem>,
    event: CalendarEvents?,
    onToggleRoutine: (String, Boolean) -> Unit,
    onSelectPet: (String) -> Unit,
    onNavigateToPetDetails: (String) -> Unit,
    onNavigateToFeature: (route: String) -> Unit,
    onLogout: () -> Unit
) {
    val activePet = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()
    val activePetColor = parseHexColor(activePet?.customColour, MintCardSurface)
    val title = event?.title.orEmpty()
    val category = event?.category.orEmpty()
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

            // --- HEADER ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello! ${userName.ifEmpty { "Pet Parent" }}",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted
                    )
                    Text(
                        text = "Pawsitive Habits",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MintDarkGreen
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (activePet != null) {
                        Surface(
                            color = SurfaceWhite,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 2.dp,
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
                                        .background(activePetColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.petnav),
                                        contentDescription = "Pet Details",
                                        tint = activePetColor,
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
            }

            if (pets.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(pets, key = { it.id }) { pet ->
                        val isSelected = pet.id == selectedPetId
                        val petThemeColor = parseHexColor(pet.customColour, MintDarkGreen)

                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectPet(pet.id) },
                            label = {
                                Text(
                                    text = pet.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Pets,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) SurfaceWhite else petThemeColor
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = petThemeColor,
                                selectedLabelColor = SurfaceWhite,
                                containerColor = SurfaceWhite,
                                labelColor = TextDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = petThemeColor.copy(alpha = 0.4f),
                                selectedBorderColor = petThemeColor
                            ),
                            shape = RoundedCornerShape(50)
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(3.dp, activePetColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToFeature("agenda") }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (category.isEmpty()) MintPrimary else MintCardSurface
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(getCalendarIcon(category)),
                                contentDescription = category,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Next Upcoming Event",
                                style = MaterialTheme.typography.labelSmall,
                                color = MintDarkGreen
                            )
                            Text(
                                text = if (title.isEmpty()) {
                                    "No upcoming events scheduled"
                                } else {
                                    title
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Calendar",
                            tint = activePetColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }


            val completedCount = routines.count { it.isCompleted }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Routines",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MintDarkGreen
                    )
                    Text(
                        text = "$completedCount of ${routines.size} completed",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (completedCount == routines.size && routines.isNotEmpty()) activePetColor else MintPrimary
                    )
                }
                TextButton(onClick = { onNavigateToFeature("routines") }) {
                    Text(
                        text = "Manage Routines",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MintDarkGreen
                    )
                }
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
                    if (routines.isEmpty()) {
                        Text(
                            text = "No routines scheduled for today.",
                            color = TextMuted,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        routines.forEach { routine ->
                            val iconResId = getRoutineIconRes(routine.title)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (routine.isCompleted) MintBackground.copy(alpha = 0.2f)
                                        else Color.Unspecified
                                    )
                                    .clickable { onNavigateToFeature("routine_detail/${routine.id}") }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (routine.isCompleted) TextMuted.copy(alpha = 0.15f) else MintCardSurface
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = iconResId),
                                        contentDescription = routine.title,
                                        tint = if (routine.isCompleted) TextMuted else Color.Unspecified,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = routine.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            textDecoration = if (routine.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                        ),
                                        color = if (routine.isCompleted) TextMuted else TextDark
                                    )
                                    if (routine.time.isNotEmpty()) {
                                        Text(
                                            text = routine.time,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMuted
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onToggleRoutine(routine.id, !routine.isCompleted) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (routine.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                        contentDescription = "Toggle Complete",
                                        modifier = Modifier.size(26.dp),
                                        tint= MintPrimary
                                    )
                                }
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
                    onClick = { onNavigateToFeature("weight") }
                )
                HomeShortcutButton(
                    title = "+ Medical",
                    color = MintCardSurface,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToFeature("medical_record") }
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

fun parseHexColor(hexColor: String?, defaultColor: Color = MintDarkGreen): Color {
    if (hexColor.isNullOrBlank()) return defaultColor
    return try {
        val colorString = hexColor.removePrefix("#")
        val parsed = colorString.toLong(16)
        if (colorString.length == 6) {
            Color(parsed or 0xFF000000)
        } else if (colorString.length == 8) {
            Color(parsed)
        } else {
            defaultColor
        }
    } catch (e: Exception) {
        defaultColor
    }
}

fun getCalendarIcon(title: String?): Int {
    return when (title) {
        "Medical" -> R.drawable.checkupmed
        "Grooming" -> R.drawable.brushroutine
        "Vaccination" -> R.drawable.vaccinemed
        "Playdate" -> R.drawable.greenpaws
        "Other" -> R.drawable.customroutine
        else -> R.drawable.calendarnav
    }
}

