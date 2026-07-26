package com.zahraag.pawsitivehabits.screens

import android.R.attr.fontWeight
import android.R.attr.text
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.data.Pet
import com.zahraag.pawsitivehabits.ui.theme.MintBackground
import com.zahraag.pawsitivehabits.ui.theme.MintCardSurface
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.MintPrimary
import com.zahraag.pawsitivehabits.ui.theme.SurfaceWhite
import com.zahraag.pawsitivehabits.ui.theme.TextDark
import com.zahraag.pawsitivehabits.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetScreen(
    pets: List<Pet>,
    selectedPetId: String?,
    onSelectPet: (String) -> Unit,
    onAddPetSubmitted:(Pet) -> Unit
) {
    var showAddPetModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)
            .padding(16.dp)
    ){
        Column{
            Text(
                text= "My Pets" ,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = TextDark,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Tap a pet to switch active profile",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (pets.isEmpty()) {

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.petnav),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MintPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Pets Added Yet",
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Click the '+' button to create your first pet profile.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pets) { pet ->
                        PetProfileCard(
                            pet = pet,
                            isSelected = pet.id == selectedPetId,
                            onClick = { onSelectPet(pet.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddPetModal = true },
            containerColor = MintPrimary,
            contentColor = SurfaceWhite,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Pet")
        }
    }

    if (showAddPetModal) {
        AddPetBottomSheet(
            onDismiss = { showAddPetModal = false },
            onSave = { newPet ->
                onAddPetSubmitted(newPet)
                showAddPetModal = false
            }
        )
    }
}

@Composable
fun PetProfileCard(
    pet: Pet,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = if (isSelected) BorderStroke(3.dp, MintDarkGreen) else null,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(MintCardSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.petnav),
                    contentDescription = null,
                    tint = MintDarkGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pet.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextDark
            )
            Text(
                text = "${pet.petType} • ${pet.breed ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            if (pet.microchipId != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Chip(text = "Chip: ${pet.microchipId}")
            }
        }
    }
}

@Composable
fun Chip(text: String) {
    Surface(
        color = MintBackground,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MintDarkGreen,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Pet) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("Dog") }
    var breed by remember { mutableStateOf("") }
    var microchipId by remember { mutableStateOf("") }
    var isNeutered by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Add New Pet Profile",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Pet Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Species (e.g. Dog, Cat)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = microchipId,
                onValueChange = { microchipId = it },
                label = { Text("Microchip ID (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isNeutered,
                    onCheckedChange = { isNeutered = it },
                    colors = CheckboxDefaults.colors(checkedColor = MintPrimary)
                )
                Text(text = "Spayed / Neutered", color = TextDark)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val newPet = Pet(
                            userId = "user_default", // Matches standard active user ID
                            name = name,
                            petType = species,
                            breed = breed.ifBlank { null },
                            microchipId = microchipId.ifBlank { null },
                            isNeutered = isNeutered,
                            dateOfBirth = System.currentTimeMillis(),
                            adoptionDate = System.currentTimeMillis()
                        )
                        onSave(newPet)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MintPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("SAVE PET PROFILE", fontWeight = FontWeight.Bold, color = SurfaceWhite)
            }
        }
    }
}