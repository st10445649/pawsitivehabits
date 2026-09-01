package com.zahraag.pawsitivehabits.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zahraag.pawsitivehabits.data.models.UserSettings
import com.zahraag.pawsitivehabits.ui.theme.*

@Composable
fun SettingsScreen(
    userSettings: UserSettings = UserSettings(id = "user123"),
    userName: String = "User",
    userEmail: String = "user@example.com",
    onNavigateBack: () -> Unit,
    onSaveSettings: (UserSettings) -> Unit,
    onSyncDataClick: () -> Unit,
    onExportDataClick: (android.net.Uri) -> Unit
) {

    var notificationsEnabled by remember(userSettings) { mutableStateOf(userSettings.notificationsEnabled) }
    var weightUnit by remember(userSettings) { mutableStateOf(userSettings.weightUnit) }
    var language by remember(userSettings) { mutableStateOf(userSettings.language) }
    var biometricEnabled by remember(userSettings) { mutableStateOf(userSettings.biometricLockEnabled) }

    var isLanguageDropdownExpanded by remember { mutableStateOf(false) }

    fun triggerSave(
        newNotifications: Boolean = notificationsEnabled,
        newWeight: String = weightUnit,
        newLang: String = language,
        newBio: Boolean = biometricEnabled
    ) {
        onSaveSettings(
            userSettings.copy(
                notificationsEnabled = newNotifications,
                weightUnit = newWeight,
                language = newLang,
                biometricLockEnabled = newBio,
                isSynced = false
            )
        )
    }

    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { onExportDataClick(it) }
    }

    val languageMap = mapOf("en" to "English", "es" to "Spanish", "fr" to "French")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = MintDarkGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Profile & Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Profile Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(MintMediumGreen, RoundedCornerShape(27.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = userEmail,
                            fontSize = 13.sp,
                            color = MintDarkGreen.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Preferences",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MintDarkGreen.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Preferences Card Container
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Push Notifications Toggle
                    SettingsSwitchRow(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            notificationsEnabled = it
                            triggerSave(newNotifications = it)
                        }
                    )

                    HorizontalDivider(
                        color = MintBackground,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Weight Unit Switcher (kg vs lbs)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Scale,
                            contentDescription = null,
                            tint = MintDarkGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Weight Unit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        // Segmented Toggle
                        Row(
                            modifier = Modifier
                                .background(MintBackground, RoundedCornerShape(12.dp))
                                .padding(2.dp)
                        ) {
                            listOf("kg", "lbs").forEach { unit ->
                                val isSelected = weightUnit == unit
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) MintDarkGreen else MintBackground,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            weightUnit = unit
                                            triggerSave(newWeight = unit)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = unit.uppercase(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SurfaceWhite else MintDarkGreen
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        color = MintBackground,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Language Dropdown Selector
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isLanguageDropdownExpanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = MintDarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Language",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = languageMap[language] ?: "English",
                                fontSize = 14.sp,
                                color = MintDarkGreen,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MintDarkGreen
                            )
                        }

                        DropdownMenu(
                            expanded = isLanguageDropdownExpanded,
                            onDismissRequest = { isLanguageDropdownExpanded = false },
                            modifier = Modifier.background(SurfaceWhite)
                        ) {
                            languageMap.forEach { (code, name) ->
                                DropdownMenuItem(
                                    text = { Text(name, color = TextDark) },
                                    onClick = {
                                        language = code
                                        triggerSave(newLang = code)
                                        isLanguageDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = MintBackground,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Biometric Lock Toggle
                    SettingsSwitchRow(
                        icon = Icons.Default.Lock,
                        title = "Biometric Lock",
                        checked = biometricEnabled,
                        onCheckedChange = {
                            biometricEnabled = it
                            triggerSave(newBio = it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Data & Cloud",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MintDarkGreen.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Data & Actions Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Manual Cloud Sync
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSyncDataClick() }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = MintDarkGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sync Cloud Data",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Text(
                                text = "Force manual backup to server",
                                fontSize = 11.sp,
                                color = MintDarkGreen.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MintDarkGreen
                        )
                    }

                    HorizontalDivider(
                        color = MintBackground,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Export Pet Data Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Prompts file launcher to create "pet_records.csv"
                                exportFileLauncher.launch("pet_records.csv")
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = MintDarkGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Export Pet Data",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Text(
                                text = "Download medical, weight & habit logs",
                                fontSize = 11.sp,
                                color = MintDarkGreen.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MintDarkGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MintDarkGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SurfaceWhite,
                checkedTrackColor = MintDarkGreen,
                uncheckedThumbColor = MintDarkGreen,
                uncheckedTrackColor = MintBackground
            )
        )
    }
}