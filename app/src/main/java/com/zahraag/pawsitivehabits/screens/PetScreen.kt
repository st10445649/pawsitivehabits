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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zahraag.pawsitivehabits.LabelText
import com.zahraag.pawsitivehabits.MintInputField
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
        AddPetScreen(
           onBackClick = {},
            onSavePet = { newPet ->
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
fun AddPetScreen(
    onBackClick: () -> Unit,
    onSavePet: (Pet) -> Unit
) {
    val scrollState = rememberScrollState()

    // Form States
    var petType by remember { mutableStateOf("Cat") }
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var isNeutered by remember { mutableStateOf(false) }
    var colour by remember { mutableStateOf("") }
    var microchipNumber by remember { mutableStateOf("04040303") }
    var birthdate by remember { mutableStateOf("") }
    var adoptionDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf(0xFFFF8A75) }

    // Dropdown States
    var petTypeExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    val petTypeOptions = listOf("Cat", "Dog", "Bird", "Rabbit", "Reptile", "Other")
    val genderOptions = listOf("Female", "Male")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Add Pet",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintDarkGreen,
                            modifier = Modifier.padding(end = 48.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MintBackground)
            )
        },
        containerColor = MintBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Photo Placeholder Frame
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MintPrimary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .size(130.dp)
                    .clickable { /* Trigger Image Picker */ }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Add Photo",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add Photo",
                        color = SurfaceWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Field 1: Pet Type (Dropdown)
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Pet Type")
                ExposedDropdownMenuBox(
                    expanded = petTypeExpanded,
                    onExpandedChange = { petTypeExpanded = !petTypeExpanded }
                ) {
                    MintInputField(
                        value = petType,
                        onValueChange = {},
                        placeholder = "Select Type",
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MintDarkGreen)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = petTypeExpanded,
                        onDismissRequest = { petTypeExpanded = false }
                    ) {
                        petTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    petType = option
                                    petTypeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 2: Name
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Name")
                MintInputField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Pet's name"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 3: Breed
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Breed")
                MintInputField(
                    value = breed,
                    onValueChange = { breed = it },
                    placeholder = "Pet's breed"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 4: Gender (Dropdown)
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Gender")
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    MintInputField(
                        value = gender,
                        onValueChange = {},
                        placeholder = "Select Gender",
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MintDarkGreen)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 5: Spayed / Neutered Toggle Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Spayed/Neutered",
                            fontWeight = FontWeight.Bold,
                            color = MintDarkGreen,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Has your pet been spayed or neutered?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MintDarkGreen.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = isNeutered,
                        onCheckedChange = { isNeutered = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SurfaceWhite,
                            checkedTrackColor = MintDarkGreen,
                            uncheckedThumbColor = SurfaceWhite,
                            uncheckedTrackColor = MintCardSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 6: Colour
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Colour")
                MintInputField(
                    value = colour,
                    onValueChange = { colour = it },
                    placeholder = "Coat colour eg. Gray, White ..."
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 7: Microchip Number
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Microchip Number")
                MintInputField(
                    value = microchipNumber,
                    onValueChange = { microchipNumber = it },
                    placeholder = "Enter Microchip ID"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 8: Birthdate Picker
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Birthdate")
                MintInputField(
                    value = birthdate,
                    onValueChange = { birthdate = it },
                    placeholder = "Select birthdate",
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = MintDarkGreen)
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 9: Adoption Date Picker
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Adoption Date")
                MintInputField(
                    value = adoptionDate,
                    onValueChange = { adoptionDate = it },
                    placeholder = "Select adoption date",
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = MintDarkGreen)
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 10: Pet Theme Color Picker Box (6_2.png)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pet Theme",
                        fontWeight = FontWeight.Bold,
                        color = MintDarkGreen,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Select a colour to associate with your pet below",
                        style = MaterialTheme.typography.bodySmall,
                        color = MintDarkGreen.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rainbow spectrum wheel icon representation
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(MintDarkGreen)
                        )

                        // Color Preview Block
                        Box(
                            modifier = Modifier
                                .size(width = 90.dp, height = 70.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(selectedColorHex))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 11: Notes Box
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Notes")
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Enter additional notes...", color = MintDarkGreen.copy(alpha = 0.5f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MintCardSurface.copy(alpha = 0.6f),
                        unfocusedContainerColor = MintCardSurface.copy(alpha = 0.6f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save Pet Button
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val newPet = Pet(
                            userId = "user_default",
                            name = name,
                            petType = petType,
                            breed = breed.ifBlank { null },
                            microchipId = microchipNumber.ifBlank { null },
                            isNeutered = isNeutered,
                            dateOfBirth = System.currentTimeMillis(),
                            adoptionDate = System.currentTimeMillis()
                        )
                        onSavePet(newPet)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintDarkGreen),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
            ) {
                Text(
                    text = "Save Pet",
                    color = SurfaceWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}