package com.zahraag.pawsitivehabits.screens

import android.graphics.drawable.shapes.Shape
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.common.fill
import com.zahraag.pawsitivehabits.data.models.Weight
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
fun WeightScreen(
    petsMap: Map<String, String> = mapOf("pet1" to "Cat", "pet2" to "Dog"),
    weightList: List<Weight> = emptyList(),
    currentUserId: String = "user123",
    onNavigateBack: () -> Unit,
    onSaveWeight: (Weight) -> Unit
) {
    var selectedPetId by remember { mutableStateOf(petsMap.keys.firstOrNull() ?: "") }
    var isPetDropdownExpanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val petWeights = weightList.filter { it.petId == selectedPetId }.sortedByDescending { it.date }
    val currentWeight = petWeights.firstOrNull()?.weightValue ?: 0.0
    val minWeight = petWeights.minOfOrNull { it.weightValue } ?: 0.0
    val maxWeight = petWeights.maxOfOrNull { it.weightValue } ?: 0.0

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(petWeights) {
        val yValues = if (petWeights.isNotEmpty()) {
            petWeights.reversed().map { it.weightValue }
        } else {
            listOf(0.0)
        }

        modelProducer.runTransaction {
            lineSeries {
                series(yValues)
            }
        }
    }

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
                    text = "Weight",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Weight",
                        tint = MintDarkGreen,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pet Dropdown Selector
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
                            text = petsMap[selectedPetId] ?: "Select Pet",
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
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
                            text = { Text(name, color = TextDark) },
                            onClick = {
                                selectedPetId = id
                                isPetDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    title = "Current Weight",
                    value = "%.2f kg".format(currentWeight),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Weight Range",
                    value = "%.2f - %.2f kg".format(minWeight, maxWeight),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Weight History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MintDarkGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(fill(MintDarkGreen)),
                                pointProvider = LineCartesianLayer.PointProvider.single(
                                    point = LineCartesianLayer.Point(
                                        component = rememberShapeComponent(
                                            fill = fill(MintDarkGreen)
                                        ),
                                        sizeDp = 8f
                                    )
                                )
                            )
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom()
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Weight History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MintDarkGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = SurfaceWhite, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (petWeights.isEmpty()) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No weight records found.", color = MintDarkGreen.copy(alpha = 0.6f))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(petWeights) { weightItem ->
                            WeightHistoryRow(weight = weightItem)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddWeightDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { value, date ->
                    val newWeight = Weight(
                        userId = currentUserId,
                        petId = selectedPetId,
                        weightValue = value,
                        unit = "kg",
                        date = date.toEpochMilli()
                    )
                    onSaveWeight(newWeight)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MintMediumGreen),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 13.sp, color = SurfaceWhite.copy(alpha = 0.85f), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, color = SurfaceWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WeightHistoryRow(weight: Weight) {
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val dateStr = weight.date.toLocalDate().format(dateFormatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "%.2f %s".format(weight.weightValue, weight.unit),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MintDarkGreen
        )
        Text(
            text = dateStr,
            fontSize = 13.sp,
            color = MintDarkGreen.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MintDarkGreen.copy(alpha = 0.15f))
    }
}

@Composable
fun AddWeightDialog(
    onDismiss: () -> Unit,
    onAdd: (Double, LocalDate) -> Unit
) {
    var weightInput by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text("Weight", fontWeight = FontWeight.Bold, color = MintDarkGreen, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    placeholder = { Text("0.0 kg", color = MintDarkGreen.copy(alpha = 0.4f)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintDarkGreen,
                        unfocusedBorderColor = MintCardSurface,
                        focusedContainerColor = MintCardSurface,
                        unfocusedContainerColor = MintCardSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Date", fontWeight = FontWeight.Bold, color = MintDarkGreen, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedDate.format(dateFormatter), color = MintDarkGreen, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = MintDarkGreen)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = MintDarkGreen, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val value = weightInput.toDoubleOrNull() ?: 0.0
                            onAdd(value, selectedDate)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MintMediumGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}