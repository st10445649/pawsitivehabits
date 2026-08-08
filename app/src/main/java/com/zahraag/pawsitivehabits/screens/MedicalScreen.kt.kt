package com.zahraag.pawsitivehabits.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.data.MedicTypeOption
import com.zahraag.pawsitivehabits.data.MedicalRecords
import com.zahraag.pawsitivehabits.data.RoutineTypeOption
import com.zahraag.pawsitivehabits.toEpochMilli
import com.zahraag.pawsitivehabits.toLocalDate
import com.zahraag.pawsitivehabits.ui.theme.MintBackground
import com.zahraag.pawsitivehabits.ui.theme.MintCardSurface
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.MintMediumGreen
import com.zahraag.pawsitivehabits.ui.theme.SurfaceWhite
import com.zahraag.pawsitivehabits.ui.theme.TextDark
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.filter

@Composable
fun MedicalRecordsScreen(
    medicalList: List<MedicalRecords> = emptyList(),
    petsMap: Map<String, String> = mapOf("pet1" to "Nala", "pet2" to "Milo"),
    onNavigateBack: () -> Unit,
    onNavigateToAddRecord: () -> Unit,
    onNavigateToEditRecord: (MedicalRecords) -> Unit,
    onDeleteRecord: (MedicalRecords) -> Unit
) {
    var selectedPetId by remember { mutableStateOf<String?>(null) } // null = All Pets
    var selectedTypeFilter by remember { mutableStateOf("All") }
    var isPetDropdownExpanded by remember { mutableStateOf(false) }

    val filterTypes = remember {
        listOf(
            MedicTypeOption("All", R.drawable.greenpaws),
            MedicTypeOption("Vaccine", R.drawable.vaccinemed),
            MedicTypeOption("Medication", R.drawable.medmed),
            MedicTypeOption("Treatment", R.drawable.treatmentmed),
            MedicTypeOption("Surgery", R.drawable.surgerymed),
            MedicTypeOption("Check-up", R.drawable.checkupmed),
            MedicTypeOption("Exam", R.drawable.exammed),
        )
    }

    // Filtered Records List
    val filteredRecords = medicalList.filter { record ->
        (selectedPetId == null || record.petId == selectedPetId) &&
                (selectedTypeFilter == "All" || record.category.equals(selectedTypeFilter, ignoreCase = true))
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
            // Top App Bar
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
                    text = "Medical Records",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onNavigateToAddRecord) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Record",
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
                            text = if (selectedPetId == null) "All Pets" else "${petsMap[selectedPetId]}",
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

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterTypes) { type ->
                    val isSelected = selectedTypeFilter == type.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTypeFilter = type.name },
                        label = {
                            Text(
                                text = type.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SurfaceWhite else MintDarkGreen
                            )
                        },
                        leadingIcon = {

                                Icon(
                                    painter = painterResource(id = type.iconRes),
                                    contentDescription = type.name,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )

                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MintDarkGreen,
                            containerColor = MintCardSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MintCardSurface,
                            selectedBorderColor = MintDarkGreen,
                            borderWidth = 1.dp
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Medical Records List
            if (filteredRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No medical records found.",
                        color = MintDarkGreen.copy(alpha = 0.6f),
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredRecords) { record ->
                        MedicalRecordCard(
                            record = record,
                            petName = petsMap[record.petId] ?: "Pet",
                            onEdit = { onNavigateToEditRecord(record) },
                            onDelete = { onDeleteRecord(record) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicalRecordCard(
    record: MedicalRecords,
    petName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val dateStr = record.date.toLocalDate().format(dateFormatter)

    val typeIcon = when (record.category.lowercase()) {
        "vaccine" -> "💉"
        "medication" -> "💊"
        "treatment" -> "🏥"
        "check-up" -> "🩺"
        "exam" -> "🔬"
        "surgery" -> "🔪"
        "allergy" -> "🤧"
        else -> "📋"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MintCardSurface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Type Icon Badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MintMediumGreen.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = typeIcon, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextDark
                    )
                    if (record.reason.isNotEmpty()) {
                        Text(
                            text = record.reason,
                            fontSize = 13.sp,
                            color = MintDarkGreen.copy(alpha = 0.7f)
                        )
                    }
                }

                // 3-Dots Menu
                Box {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = MintDarkGreen)
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.background(SurfaceWhite)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit", color = TextDark) },
                            onClick = {
                                isMenuExpanded = false
                                onEdit()
                            }
                        )
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

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-details (Pet tag, Type chip, Cost, Date)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🐾 $petName", fontSize = 12.sp, color = MintDarkGreen, fontWeight = FontWeight.Medium)

                // Type Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MintDarkGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = record.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MintDarkGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MintDarkGreen.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = dateStr,
                    fontSize = 12.sp,
                    color = MintDarkGreen.copy(alpha = 0.7f)
                )
            }
        }
    }
}


@Composable
fun AddEditMedicalRecordScreen(
    existingRecord: MedicalRecords? = null,
    petsMap: Map<String, String> = mapOf("pet1" to "Nala", "pet2" to "Milo"),
    currentUserId: String = "user123",
    onNavigateBack: () -> Unit,
    onSaveRecord: (MedicalRecords) -> Unit,
    onDeleteRecord: ((MedicalRecords) -> Unit)? = null
) {
    val isEditMode = existingRecord != null

    var selectedPetId by remember { mutableStateOf(existingRecord?.petId ?: petsMap.keys.firstOrNull() ?: "") }
    var isPetDropdownExpanded by remember { mutableStateOf(false) }
    var customMedicText by remember { mutableStateOf("") }

    val typesList = remember {
        listOf(
            MedicTypeOption("Vaccine", R.drawable.vaccinemed),
            MedicTypeOption("Medication", R.drawable.medmed),
            MedicTypeOption("Treatment", R.drawable.treatmentmed),
            MedicTypeOption("Surgery", R.drawable.surgerymed),
            MedicTypeOption("Check-up", R.drawable.checkupmed),
            MedicTypeOption("Exam", R.drawable.exammed),
            MedicTypeOption("Allergy", R.drawable.allergymed),
            MedicTypeOption("Condition", R.drawable.conditionmed),
            MedicTypeOption("Other", R.drawable.customroutine)
        )
    }


    var selectedType by remember { mutableStateOf( "Vaccine") }
    var titleInput by remember { mutableStateOf(existingRecord?.title ?: "") }
    var reasonInput by remember { mutableStateOf(existingRecord?.reason ?: "") }

    var recordDate by remember { mutableStateOf(existingRecord?.date?.toLocalDate() ?: LocalDate.now()) }
    var nextDueDate by remember { mutableStateOf(existingRecord?.nextDueDate?.toLocalDate()) }

    var notesInput by remember { mutableStateOf(existingRecord?.notes ?: "") }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = if (isEditMode) "Edit Medical Record" else "New Medical Record",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1f))

                if (isEditMode && onDeleteRecord != null) {
                    IconButton(onClick = {
                        onDeleteRecord(existingRecord!!)
                        onNavigateBack()
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Record",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(28.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Pet", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))

            Box {
                Card(
                    shape = RoundedCornerShape(16.dp),
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
                        Text(
                            text = "${petsMap[selectedPetId] ?: "Select Pet"}",
                            color = MintDarkGreen,
                            fontWeight = FontWeight.SemiBold
                        )
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
                            text = { Text("$name", color = TextDark) },
                            onClick = {
                                selectedPetId = id
                                isPetDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Medical Record Type Selector Grid
            Text("Type", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                typesList.forEach { type ->
                    val isSelected = selectedType == type.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedType = type.name },
                        label = { Text(type.name, color = if (isSelected) SurfaceWhite else MintDarkGreen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                        leadingIcon = {
                            if(isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(18.dp))
                            } else {
                                Icon(
                                    painter = painterResource(id=type.iconRes),
                                    tint = Color.Unspecified,
                                    contentDescription = type.name,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MintDarkGreen,
                            containerColor = MintCardSurface,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MintCardSurface,
                            selectedBorderColor = MintDarkGreen,
                            borderWidth = 1.dp
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            if (selectedType == "Custom") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = customMedicText,
                    onValueChange = { customMedicText = it },
                    placeholder = { Text("Type custom routine...", color = MintDarkGreen.copy(alpha = 0.3f),
                        fontWeight = FontWeight.SemiBold) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintDarkGreen,
                        unfocusedBorderColor = MintCardSurface,
                        focusedContainerColor = MintCardSurface,
                        unfocusedContainerColor = MintCardSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            // 3. Title Field
            OutlinedTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                placeholder = { Text("Title", color = MintDarkGreen.copy(alpha = 0.5f)) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintDarkGreen,
                    unfocusedBorderColor = MintCardSurface,
                    focusedContainerColor = MintCardSurface,
                    unfocusedContainerColor = MintCardSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Reason / Diagnosis Field
            OutlinedTextField(
                value = reasonInput,
                onValueChange = { reasonInput = it },
                placeholder = { Text("Reason/Diagnosis", color = MintDarkGreen.copy(alpha = 0.5f)) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintDarkGreen,
                    unfocusedBorderColor = MintCardSurface,
                    focusedContainerColor = MintCardSurface,
                    unfocusedContainerColor = MintCardSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Date Field
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Date", fontSize = 12.sp, color = MintDarkGreen.copy(alpha = 0.7f))
                        Text(recordDate.format(dateFormatter), fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MintDarkGreen)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 6. Next Due Date Field (Optional)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Next Due Date", fontSize = 12.sp, color = MintDarkGreen.copy(alpha = 0.7f))
                        Text(
                            text = nextDueDate?.format(dateFormatter) ?: "Not set (optional)",
                            fontWeight = FontWeight.SemiBold,
                            color = if (nextDueDate != null) MintDarkGreen else MintDarkGreen.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MintDarkGreen)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7. Vet & Clinic Placeholders
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No veterinarians/clinics attached", fontSize = 13.sp, color = MintDarkGreen.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { /* Add Vet Contact logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MintDarkGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Vet Contact", fontSize = 12.sp)
                    }
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            // 9. Notes Field
            OutlinedTextField(
                value = notesInput,
                onValueChange = { notesInput = it },
                placeholder = { Text("Notes", color = MintDarkGreen.copy(alpha = 0.5f)) },
                minLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintDarkGreen,
                    unfocusedBorderColor = MintCardSurface,
                    focusedContainerColor = MintCardSurface,
                    unfocusedContainerColor = MintCardSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Save / Update Button
            Button(
                onClick = {
                    val newRecord = MedicalRecords(
                        id = existingRecord?.id ?: java.util.UUID.randomUUID().toString(),
                        userId = currentUserId,
                        petId = selectedPetId,
                        category = selectedType,
                        title = titleInput.ifEmpty { selectedType },
                        reason = reasonInput,
                        date = recordDate.toEpochMilli(),
                        nextDueDate = nextDueDate?.toEpochMilli(),
                        vet = "",
                        notes = notesInput
                    )
                    onSaveRecord(newRecord)
                    onNavigateBack()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintMediumGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (isEditMode) "Save Changes" else "Save Record",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SurfaceWhite
                )
            }
        }
    }
}