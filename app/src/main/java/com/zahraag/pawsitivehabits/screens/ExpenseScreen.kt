package com.zahraag.pawsitivehabits.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zahraag.pawsitivehabits.data.Expenses
import com.zahraag.pawsitivehabits.toEpochMilli
import com.zahraag.pawsitivehabits.ui.theme.*
import com.zahraag.pawsitivehabits.toLocalDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter


val CategoryColors = mapOf(
    "Food" to Color(0xFFE39463),
    "Medical" to Color(0xFFF8E16D),
    "Grooming" to Color(0xFFC77BDC),
    "Supplies" to Color(0xFF52A6D3),
    "Insurance" to Color(0xFFD260A6),
    "Training" to Color(0xFF94C268),
    "Custom" to Color(0xFF6E65EA)
)

@Composable
fun ExpenseScreen(
    expensesList: List<Expenses> = emptyList(),
    petsMap: Map<String, String> = mapOf("pet1" to "Nala", "pet2" to "Milo"),
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (Expenses) -> Unit,
    onDeleteExpense: (Expenses) -> Unit
) {
    val totalSpending = expensesList.sumOf { it.amount }

    val categoryTotals = expensesList.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

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
                    text = "Expenses",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onNavigateToAddExpense) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Expense",
                        tint = MintDarkGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Monthly Spending",
                        fontWeight = FontWeight.SemiBold,
                        color = MintDarkGreen,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(170.dp)
                    ) {
                        ExpenseDonutChart(categoryTotals = categoryTotals, totalSpending = totalSpending)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Total",
                                fontSize = 12.sp,
                                color = MintDarkGreen.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$%.2f".format(totalSpending),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintDarkGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        categoryTotals.keys.take(3).forEach { cat ->
                            val color = CategoryColors[cat] ?: MintDarkGreen
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MintDarkGreen
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Recent Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onNavigateToAddExpense,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MintDarkGreen)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))


            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (expensesList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No expenses recorded yet.", color = MintDarkGreen.copy(alpha = 0.6f))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(expensesList.sortedByDescending { it.date }) { expense ->
                            TransactionRow(
                                expense = expense,
                                petName = petsMap[expense.petId] ?: "Pet",
                                onEdit = { onNavigateToEditExpense(expense) },
                                onDelete = { onDeleteExpense(expense) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseDonutChart(categoryTotals: Map<String, Double>, totalSpending: Double) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (totalSpending <= 0.0) {
            drawCircle(
                color = MintMediumGreen.copy(alpha = 0.3f),
                style = Stroke(width = 30f)
            )
            return@Canvas
        }

        var startAngle = -90f
        categoryTotals.forEach { (category, amount) ->
            val sweepAngle = ((amount / totalSpending) * 360f).toFloat()
            val color = CategoryColors[category] ?: MintDarkGreen
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle - 2f, // Small gap between slices
                useCenter = false,
                style = Stroke(width = 32f, cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun TransactionRow(
    expense: Expenses,
    petName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val dateStr = expense.date.toLocalDate().format(dateFormatter)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(CategoryColors[expense.category] ?: MintDarkGreen)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.title.ifEmpty { expense.category },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark
            )
            Text(
                text = "$petName • $dateStr",
                fontSize = 12.sp,
                color = MintDarkGreen.copy(alpha = 0.7f)
            )
        }

        Text(
            text = "-$%.2f".format(expense.amount),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MintDarkGreen
        )

        Spacer(modifier = Modifier.width(4.dp))


        Box {
            IconButton(onClick = { isMenuExpanded = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MintDarkGreen
                )
            }

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                modifier = Modifier.background(SurfaceWhite)
            ) {
                DropdownMenuItem(
                    text = { Text("Edit", color = TextDark, fontWeight = FontWeight.Medium) },
                    onClick = {
                        isMenuExpanded = false
                        onEdit()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = Color.Red, fontWeight = FontWeight.Medium) },
                    onClick = {
                        isMenuExpanded = false
                        onDelete()
                    }
                )
            }
        }
    }
}


@Composable
fun AddExpenseScreen(
    existingExpense: Expenses? = null,
    petsMap: Map<String, String> = mapOf("pet1" to "Nala", "pet2" to "Milo"),
    currentUserId: String = "user123",
    onNavigateBack: () -> Unit,
    onSaveExpense: (Expenses) -> Unit,
    onDeleteExpense: ((Expenses) -> Unit)? = null
) {
    val isEditMode = existingExpense != null

    var selectedPetId by remember { mutableStateOf(existingExpense?.petId ?: petsMap.keys.firstOrNull() ?: "") }
    var isPetDropdownExpanded by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf(existingExpense?.title ?: "") }
    var amountInput by remember { mutableStateOf(existingExpense?.amount?.let { if (it > 0) it.toString() else "" } ?: "") }

    val defaultCategories = listOf("Food", "Medical", "Grooming", "Supplies", "Insurance", "Training")
    val initialCat = existingExpense?.category ?: "Medical"
    val isCustomInitial = initialCat !in defaultCategories && initialCat.isNotEmpty()

    var selectedCategory by remember { mutableStateOf(if (isCustomInitial) "Custom" else initialCat) }
    var customCategoryText by remember { mutableStateOf(if (isCustomInitial) initialCat else "") }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    var selectedDate by remember {
        mutableStateOf(
            existingExpense?.date?.toLocalDate() ?: LocalDate.now()
        )
    }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val categoriesList = listOf("Food", "Medical", "Grooming", "Supplies", "Insurance", "Training", "Custom")

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
                    text = if (isEditMode) "Edit Expense" else "Add Expense",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1f))

                if (isEditMode && onDeleteExpense != null) {
                    IconButton(onClick = {
                        onDeleteExpense(existingExpense!!)
                        onNavigateBack()
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Expense",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(28.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                        Text(
                            text = petsMap[selectedPetId] ?: "Pet",
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
                            text = { Text(name, color = TextDark) },
                            onClick = {
                                selectedPetId = id
                                isPetDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                placeholder = { Text("Title", color = MintDarkGreen.copy(alpha = 0.5f), fontWeight = FontWeight.SemiBold) },
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

            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                placeholder = { Text("Amount", color = MintDarkGreen.copy(alpha = 0.5f), fontWeight = FontWeight.SemiBold) },
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

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Expense Type ($selectedCategory)", color = MintDarkGreen, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MintDarkGreen)
                    }

                    if (isCategoryDropdownExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        categoriesList.forEach { category ->
                            val isSelected = selectedCategory == category
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) SurfaceWhite else MintCardSurface)
                                    .clickable {
                                        selectedCategory = category
                                        isCategoryDropdownExpanded = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = MintDarkGreen
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MintDarkGreen)
                                }
                            }
                        }
                    }
                }
            }

            if (selectedCategory == "Custom") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = customCategoryText,
                    onValueChange = { customCategoryText = it },
                    placeholder = { Text("Custom Category", color = MintDarkGreen.copy(alpha = 0.5f), fontWeight = FontWeight.SemiBold) },
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

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Date (${selectedDate.format(dateFormatter)})",
                        color = MintDarkGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MintDarkGreen)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))


            Button(
                onClick = {
                    val category = if (selectedCategory == "Custom") customCategoryText else selectedCategory
                    val updatedExpense = Expenses(
                        id = existingExpense?.id ?: java.util.UUID.randomUUID().toString(),
                        userId = currentUserId,
                        petId = selectedPetId,
                        title = titleInput,
                        amount = amountInput.toDoubleOrNull() ?: 0.0,
                        category = category,
                        date = selectedDate.toEpochMilli()
                    )
                    onSaveExpense(updatedExpense)
                    onNavigateBack()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintMediumGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (isEditMode) "Save Changes" else "Add Expense",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SurfaceWhite
                )
            }
        }
    }
}