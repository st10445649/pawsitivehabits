package com.zahraag.pawsitivehabits.screens

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.zahraag.pawsitivehabits.LabelText
import com.zahraag.pawsitivehabits.MintInputField
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.ui.theme.MintBackground
import com.zahraag.pawsitivehabits.ui.theme.MintCardSurface
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.MintPrimary
import com.zahraag.pawsitivehabits.ui.theme.SurfaceWhite
import com.zahraag.pawsitivehabits.ui.theme.TextDark
import com.zahraag.pawsitivehabits.ui.theme.TextMuted
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.zahraag.pawsitivehabits.data.models.FeatureItem
import com.zahraag.pawsitivehabits.data.models.featureItemsList
import com.zahraag.pawsitivehabits.viewmodel.PetViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetScreen(
    currentUserId: String,
    viewModel: PetViewModel = viewModel(),
    onViewDetails: (String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val pets by viewModel.getPets(currentUserId).collectAsStateWithLifecycle()
    val selectedPetId by viewModel.selectedPetId.collectAsStateWithLifecycle()

    var showAddPetModal by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)
            .padding(16.dp)
    ){
        Column{
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = MintDarkGreen,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "My Pets",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1.3f))
            }

            Spacer(modifier = Modifier.height(16.dp))

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
            }else{
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pets, key = { it.id }) { pet ->
                                DetailedPetProfileCard(
                                    pet = pet,
                                    isSelected = pet.id == selectedPetId,
                                    onClick = {
                                        viewModel.selectPet(pet.id)
                                        onViewDetails(pet.id) },
                                    latestWeightKg = 13.2,
                                    upcomingTaskCount = 2,
                                    onViewDetails = { onViewDetails(pet.id)},
                                    onEditClick = { },
                                    onDeleteClick = {}
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
        Dialog(
            onDismissRequest = { showAddPetModal = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AddPetScreen(
                onBackClick = { showAddPetModal = false },
                onSavePet = { newPet ->
                    val petWithUser = newPet.copy(userId = currentUserId)
                    viewModel.addPet(petWithUser)
                    showAddPetModal = false
                }
            )
        }
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
                Text(
                    text = "${pet.petType} • ${pet.microchipId ?: " "}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
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
    var petType by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var isNeutered by remember { mutableStateOf(false) }
    var colour by remember { mutableStateOf("") }
    var microchipNumber by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }
    var adoptionDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf(0xFFFF8A75) }

    var showBirthdatePicker by remember { mutableStateOf(false) }
    var showAdoptionDatePicker by remember { mutableStateOf(false) }
    val birthdatePickerState = rememberDatePickerState()
    val adoptionDatePickerState = rememberDatePickerState()

    // Dropdown States
    var petTypeExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    val petTypeOptions = listOf("Cat", "Dog", "Bird", "Rabbit", "Other")
    val genderOptions = listOf("Female", "Male", "Unknown")

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
                            tint = MintDarkGreen, modifier = Modifier.size(50.dp)
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showBirthdatePicker = true }
                ) {
                    MintInputField(
                        value = birthdate,
                        onValueChange = {},
                        placeholder = "Select birthdate",
                        trailingIcon = {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Select Birthdate",
                                tint = MintDarkGreen
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showBirthdatePicker = true }
                        )

                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 9: Adoption Date Picker
            Column(modifier = Modifier.fillMaxWidth()) {
                LabelText("Adoption Date")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAdoptionDatePicker = true }
                ) {
                    MintInputField(
                        value = adoptionDate,
                        onValueChange = {},
                        placeholder = "Select adoption date",
                        trailingIcon = {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = MintDarkGreen
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showAdoptionDatePicker = true }
                    )
                }
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
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Select a colour to associate with your pet below",
                        style = MaterialTheme.typography.bodySmall,
                        color = MintDarkGreen,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PetColorPickerSection()
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
                    placeholder = { Text("Enter additional notes...", color = MintDarkGreen.copy(alpha = 0.5f),
                        style= MaterialTheme.typography.bodySmall) },
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
                            userId = "",
                            name = name.trim(),
                            petType = petType,
                            breed = breed.ifBlank { null },
                            gender = gender,
                            microchipId = microchipNumber.ifBlank { null },
                            isNeutered = isNeutered,
                            colour = colour.ifBlank { null },
                            dateOfBirth = birthdatePickerState.selectedDateMillis ?: System.currentTimeMillis(),
                            adoptionDate = adoptionDatePickerState.selectedDateMillis ?: System.currentTimeMillis(),
                            notes = notes.ifBlank { null },
                            isSynced = false
                        )
                        onSavePet(newPet)
                    }
                },
                enabled = name.isNotBlank(),
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

    if (showBirthdatePicker) {
        DatePickerDialog(
            onDismissRequest = { showBirthdatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        birthdate = formatDateMillis(birthdatePickerState.selectedDateMillis)
                        showBirthdatePicker = false
                    }
                ) {
                    Text("OK", color = MintDarkGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBirthdatePicker = false }) {
                    Text("Cancel", color = MintDarkGreen)
                }
            }
        ) {
            DatePicker(state = birthdatePickerState)
        }
    }

    // Adoption Date Dialog
    if (showAdoptionDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showAdoptionDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        adoptionDate = formatDateMillis(adoptionDatePickerState.selectedDateMillis)
                        showAdoptionDatePicker = false
                    }
                ) {
                    Text("OK", color = MintDarkGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdoptionDatePicker = false }) {
                    Text("Cancel", color = MintDarkGreen)
                }
            }
        ) {
            DatePicker(state = adoptionDatePickerState)
        }
    }
}



@Composable
fun DetailedPetProfileCard(
    pet: Pet,
    latestWeightKg: Double?,
    upcomingTaskCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    onViewDetails: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        border = if (isSelected) BorderStroke(3.dp, MintDarkGreen) else BorderStroke(1.dp, MintCardSurface),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Selected Badge & Options Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    Surface(
                        color = MintDarkGreen,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "ACTIVE",
                            color = SurfaceWhite,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextMuted
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Full Stats") },
                            leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, tint = MintDarkGreen) },
                            onClick = {
                                menuExpanded = false
                                onViewDetails()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Profile") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MintDarkGreen) },
                            onClick = {
                                menuExpanded = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color(0xFFD32F2F)) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F)) },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Info: Avatar + Identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MintCardSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.petnav),
                        contentDescription = null,
                        tint = MintDarkGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextDark
                    )
                    Text(
                        text = "${pet.petType} • ${pet.breed ?: "Unknown Breed"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                    if (!pet.microchipId.isNullOrBlank()) {
                        Text(
                            text = "Chip: ${pet.microchipId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MintDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Stats Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Weight Pill
                Surface(
                    color = MintBackground,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.petnav), // Replace with a scale icon if available
                            contentDescription = null,
                            tint = MintDarkGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text("Weight", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = if (latestWeightKg != null) "$latestWeightKg kg" else "-- kg",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextDark
                            )
                        }
                    }
                }

                // Upcoming Tasks Pill
                Surface(
                    color = MintBackground,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MintDarkGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text("Upcoming", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "$upcomingTaskCount Tasks",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextDark
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    pet: Pet,
    latestWeightKg: Double? = 13.2,
    upcomingAppointments: List<Pair<String, String>> = listOf(
        "Annual Checkup" to "14 Aug 2026",
        "Rabies Booster" to "28 Aug 2026"
    ),
    recentExpenseTotal: Double = 130.99,
    onBackClick: () -> Unit,
    onEditPetClick: () -> Unit,
    onFeatureClick: (route: String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${pet.name} 's Profile",
                        fontWeight = FontWeight.Bold,
                        color = MintDarkGreen,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MintDarkGreen,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEditPetClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Pet",
                            tint = MintDarkGreen
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
                .verticalScroll(rememberScrollState()),

            ) {
            Spacer(modifier = Modifier.height(12.dp))
            // Profile Header
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),

                ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MintCardSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.petnav),
                                contentDescription = null,
                                tint = MintDarkGreen,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pet.name,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextDark
                            )
                            Text(
                                text = "${pet.petType} • ${pet.breed ?: "Unknown Breed"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = MintCardSurface,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (pet.isNeutered) "Spayed/Neutered" else "Intact",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MintDarkGreen,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 2.dp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MintBackground.copy(alpha = 0.5f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailInfoRow(
                            label = "Microchip ID",
                            value = pet.microchipId ?: "Not micro-chipped"
                        )
                        DetailInfoRow(label = "User ID", value = pet.userId)
                    }
                }
            }
                Spacer(modifier = Modifier.height(20.dp))

                // Quick Stats Grid
                Text(
                    text = "Quick Stats",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MintDarkGreen,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Weight Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Current Weight",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (latestWeightKg != null) "$latestWeightKg kg" else "N/A",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MintDarkGreen
                            )
                        }
                    }

                    // Recent Expenses Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Recent Spending",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "R${"%.2f".format(recentExpenseTotal)}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MintDarkGreen
                            )
                        }
                    }
                }

            Spacer(modifier = Modifier.height(20.dp))

                // Upcoming Appointments / Tasks Section
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Upcoming Care & Visits",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MintDarkGreen,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (upcomingAppointments.isEmpty()) {
                            Text(
                                text = "No upcoming visits scheduled.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                        } else {
                            upcomingAppointments.forEach { (title, date) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = MintDarkGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = TextDark
                                        )
                                    }
                                    Surface(
                                        color = MintBackground,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = date,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MintDarkGreen,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Pet Features",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MintDarkGreen,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    items(featureItemsList) { shortcut ->
                        FeatureShortcutCard(
                            shortcut = shortcut,
                            onClick = { onFeatureClick(shortcut.route) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextDark)
    }
}

@Composable
fun FeatureShortcutCard(
    shortcut: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(shortcut.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = shortcut.iconRes ?: R.drawable.petnav),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = shortcut.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextDark
            )
            Text(
                text = shortcut.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}
@Composable
fun PetColorPickerSection() {
    val controller = rememberColorPickerController()

    var selectedColor by remember { mutableStateOf(Color.White) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        HsvColorPicker(
            modifier = Modifier.size(100.dp),
            controller = controller,
            onColorChanged = { colorEnvelope ->
                selectedColor = colorEnvelope.color
            }
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .size(100.dp)
                .clip(shape = CircleShape)
                .background(selectedColor)
        )
    }
}

fun formatDateMillis(millis: Long?): String {
    if (millis == null) return ""
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.format(Date(millis))
}