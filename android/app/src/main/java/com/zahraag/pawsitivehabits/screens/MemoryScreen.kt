package com.zahraag.pawsitivehabits.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.data.Memories
import com.zahraag.pawsitivehabits.toLocalDate
import com.zahraag.pawsitivehabits.ui.theme.MintBackground
import com.zahraag.pawsitivehabits.ui.theme.MintCardSurface
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.MintMediumGreen
import com.zahraag.pawsitivehabits.ui.theme.SurfaceWhite
import com.zahraag.pawsitivehabits.ui.theme.TextDark
import java.time.format.DateTimeFormatter
import kotlin.collections.filter

@Composable
fun MemoriesScreen(
    memoriesList: List<Memories> = emptyList(),
    petsMap: Map<String, String> = mapOf("pet1" to "Nala", "pet2" to "Milo"),
    currentUserId: String = "user123",
    onNavigateBack: () -> Unit,
    onSaveMemory: (Memories) -> Unit,
    onDeleteMemory: (Memories) -> Unit
) {
    var selectedPetId by remember { mutableStateOf<String?>(null) }
    var isPetDropdownExpanded by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }

    // Filter memories by selected pet
    val filteredMemories = memoriesList.filter { memory ->
        selectedPetId == null || memory.petId == selectedPetId
    }.sortedByDescending { it.date }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
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
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Pet Memories",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Memory",
                        tint = MintDarkGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pet Selector Dropdown
            Box {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPetDropdownExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedPetId == null) "All Pets" else " ${petsMap[selectedPetId]}",
                            fontWeight = FontWeight.SemiBold,
                            color = MintDarkGreen
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text("▼", fontSize = 12.sp, color = MintDarkGreen)
                    }
                }

                DropdownMenu(
                    expanded = isPetDropdownExpanded,
                    onDismissRequest = { isPetDropdownExpanded = false },
                    modifier = Modifier.background(SurfaceWhite)
                ) {
                    DropdownMenuItem(
                        text = { Text(" All Pets", color = TextDark) },
                        onClick = {
                            selectedPetId = null
                            isPetDropdownExpanded = false
                        }
                    )
                    petsMap.forEach { (id, name) ->
                        DropdownMenuItem(
                            text = { Text(" $name", color = TextDark) },
                            onClick = {
                                selectedPetId = id
                                isPetDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))


            if (filteredMemories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No memories added yet.",
                        color = MintDarkGreen.copy(alpha = 0.6f),
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredMemories) { memory ->
                        MemoryCard(
                            memory = memory,
                            petName = petsMap[memory.petId] ?: "Pet",
                            onDelete = { onDeleteMemory(memory) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddMemoryDialog(
                petsMap = petsMap,
                currentUserId = currentUserId,
                onDismiss = { showAddDialog = false },
                onSaveMemory = { newMemory ->
                    onSaveMemory(newMemory)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun MemoryCard(
    memory: Memories,
    petName: String,
    onDelete: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val dateStr = memory.date.toLocalDate().format(dateFormatter)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MintCardSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Photo Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(MintMediumGreen.copy(alpha = 0.2f))
            ) {
                if (memory.imageUri.isNotEmpty()) {
                    AsyncImage(
                        model = memory.imageUri,
                        contentDescription = memory.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "📸",
                        fontSize = 36.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Delete Menu Options (Top Right)
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(
                        onClick = { isMenuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = SurfaceWhite
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.background(SurfaceWhite)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color.Red) },
                            onClick = {
                                isMenuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            // Info Details
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = memory.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextDark,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = " $petName",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MintDarkGreen
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = dateStr,
                        fontSize = 10.sp,
                        color = MintDarkGreen.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun AsyncImage(
    model: String,
    contentDescription: String,
    contentScale: ContentScale,
    modifier: Modifier
) {
    TODO("Not yet implemented")
}

@Composable
fun AddMemoryDialog(
    petsMap: Map<String, String>,
    currentUserId: String = "user123",
    onDismiss: () -> Unit,
    onSaveMemory: (Memories) -> Unit
) {
    var selectedPetId by remember { mutableStateOf(petsMap.keys.firstOrNull() ?: "") }
    var titleInput by remember { mutableStateOf("") }
    var imageUriString by remember { mutableStateOf("") }
    var isPetDropdownExpanded by remember { mutableStateOf(false) }

    // Launcher for image picker from gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUriString = it.toString() }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MintBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "New Pet Memory",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MintDarkGreen
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Image Picker Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MintCardSurface, RoundedCornerShape(16.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUriString.isNotEmpty()) {
                        AsyncImage(
                            model = imageUriString,
                            contentDescription = "Selected Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MintCardSurface, RoundedCornerShape(16.dp))
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MintDarkGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tap to add photo", fontSize = 12.sp, color = MintDarkGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pet Dropdown
                Box {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPetDropdownExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.greenpaws),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp))
                            Text(" ${petsMap[selectedPetId] ?: " Select Pet"}", fontWeight = FontWeight.Medium, color = TextDark)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MintDarkGreen)
                        }
                    }

                    DropdownMenu(
                        expanded = isPetDropdownExpanded,
                        onDismissRequest = { isPetDropdownExpanded = false },
                        modifier = Modifier.background(SurfaceWhite)
                    ) {
                        petsMap.forEach { (id, name) ->
                            DropdownMenuItem(
                                text = { Text(" $name", color = TextDark) },
                                onClick = {
                                    selectedPetId = id
                                    isPetDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title Input
                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    placeholder = { Text("Memory Title *", color = MintDarkGreen.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintDarkGreen,
                        unfocusedBorderColor = MintCardSurface,
                        focusedContainerColor = MintCardSurface,
                        unfocusedContainerColor = MintCardSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save / Cancel Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = MintDarkGreen)
                    }

                    Button(
                        onClick = {
                            if (titleInput.isNotEmpty() && imageUriString.isNotEmpty()) {
                                onSaveMemory(
                                    Memories(
                                        id = java.util.UUID.randomUUID().toString(),
                                        userId = currentUserId,
                                        petId = selectedPetId,
                                        title = titleInput,
                                        imageUri = imageUriString,
                                        date = System.currentTimeMillis(),
                                        isSynced = false
                                    )
                                )
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MintMediumGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save", color = SurfaceWhite)
                    }
                }
            }
        }
    }
}