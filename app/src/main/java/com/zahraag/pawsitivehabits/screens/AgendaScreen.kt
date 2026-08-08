package com.zahraag.pawsitivehabits.screens

import android.R.attr.fontWeight
import android.R.attr.onClick
import android.R.attr.text
import android.R.attr.type
import android.text.format.DateUtils.isToday
import android.view.RoundedCorner
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.zahraag.pawsitivehabits.data.AgendaDisplayItem
import com.zahraag.pawsitivehabits.data.CalendarEvents
import com.zahraag.pawsitivehabits.data.Routine
import com.zahraag.pawsitivehabits.toEpochMilli
import com.zahraag.pawsitivehabits.toFormattedTime
import com.zahraag.pawsitivehabits.toLocalDate
import com.zahraag.pawsitivehabits.ui.theme.MintBackground
import com.zahraag.pawsitivehabits.ui.theme.MintCardSurface
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.SurfaceWhite
import com.zahraag.pawsitivehabits.ui.theme.TextDark
import com.zahraag.pawsitivehabits.ui.theme.TextMuted
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.data.RoutineTypeOption
import java.util.Locale.getDefault

@Composable
fun AgendaScreen(
    routinesList: List<Routine> = emptyList(),
    calendarEventsList: List<CalendarEvents> = emptyList(),
    petNamesMap: Map<String, String> = emptyMap(),
    onNavigateBack: () -> Unit,
    onNavigateToAddRoutine: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

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
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = MintDarkGreen,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Agenda",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1.3f))
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(modifier = Modifier.fillMaxWidth()) {
                AgendaTabButton(
                    title = "Calendar",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                AgendaTabButton(
                    title = "Routine",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if(selectedTab == 0) {
                CalendarView(
                    routines = routinesList,
                    events = calendarEventsList,
                    petNamesMap = petNamesMap
                )
            }else {
                RoutinesListView(routines = routinesList,petNamesMap = petNamesMap)
            }
        }
        FloatingActionButton(
            onClick = onNavigateToAddRoutine,
            containerColor = MintDarkGreen,
            contentColor = SurfaceWhite,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Routine", modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun CalendarView(
    routines: List<Routine>,
    events: List<CalendarEvents>,
    petNamesMap: Map<String, String>
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val today = remember { LocalDate.now() }

    val currentMonth = remember { YearMonth.now() }
    val calendarState = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth =  currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale()
    )

    val coroutineScope = rememberCoroutineScope()

    val itemsByDate : Map<LocalDate, List<AgendaDisplayItem>> = remember(
        routines, events) {
        val map = mutableMapOf<LocalDate, MutableList<AgendaDisplayItem>>()
        routines.forEach { routine ->
            val date = routine.startDate.toLocalDate()
        map.getOrPut(date){mutableListOf()}.add(AgendaDisplayItem.RoutineItem(routine))}
        events.forEach { event ->
            val date = (event.time ?: System.currentTimeMillis()).toLocalDate()
            map.getOrPut(date){mutableListOf()}.add(AgendaDisplayItem.EventItem(event))
        }
        map
    }

    val selectedDayItems = itemsByDate[selectedDate] ?: emptyList()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")

    Column(modifier = Modifier.fillMaxSize()) {
        val visibleMonth = calendarState.firstVisibleMonth.yearMonth
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ){
            IconButton(
                onClick ={
                    coroutineScope.launch {
                        calendarState.animateScrollToMonth(visibleMonth.minusMonths(1))
                    }
                }
            ){
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month", tint = MintDarkGreen
                , modifier = Modifier.size(20.dp))
            }
            Text(
                text = "${visibleMonth.month.getDisplayName(TextStyle.FULL, getDefault())} ${visibleMonth.year}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MintDarkGreen
            )

            IconButton(
                onClick ={
                    coroutineScope.launch {
                        calendarState.animateScrollToMonth(visibleMonth.plusMonths((1)))
                    }
                }
            ){
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Previous Month", tint = MintDarkGreen
                        , modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        DaysOfWeekHeader(firstDayOfWeek = calendarState.firstDayOfWeek)

        Spacer(modifier = Modifier.height(8.dp))


        HorizontalCalendar(
            state = calendarState,
            dayContent = {
                day ->
                val hasItems = itemsByDate[day.date]?.isNotEmpty() == true
                CalendarDayCell(
                    day=day,
                    isSelected = selectedDate == day.date,
                    isToday = day.date == today,
                    hasItems = hasItems,
                    onClick = {selectedDate = day.date}
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = selectedDate.format(dateFormatter),
            fontSize = 16.sp,
            fontWeight= FontWeight.Bold,
            color = MintDarkGreen,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (selectedDayItems.isEmpty()){
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ){
                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center){
                    Text(
                        text = "No routines or events scheduled for this day.",
                        fontSize = 14.sp,
                        color = MintDarkGreen.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else{
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)){
                items(selectedDayItems){ item ->
                    when (item){
                        is AgendaDisplayItem.RoutineItem -> {
                            val petName = petNamesMap[item.routine.petId] ?: "Pet"
                            AgendaCard(
                                title = item.routine.title,
                                petName = petName,
                                timeStr = item.routine.time.toFormattedTime(),
                                badgeText = "Routine",
                                badgeColor = MintDarkGreen
                            )
                        }
                        is AgendaDisplayItem.EventItem -> {
                            val petName = petNamesMap[item.event.petId] ?: "Pet"
                            AgendaCard(
                                title = item.event.title,
                                petName = petName,
                                timeStr = item.event.time.toFormattedTime(),
                                badgeText = item.event.category,
                                badgeColor = Color(0xFFFC8369)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekHeader(firstDayOfWeek: DayOfWeek) {
    val daysOfWeek = remember(firstDayOfWeek) {
        val days = DayOfWeek.values()
        val index = days.indexOf(firstDayOfWeek)
        days.copyOfRange(index, days.size) + days.copyOfRange(0, index)
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, getDefault()),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MintDarkGreen.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    isToday: Boolean,
    hasItems: Boolean,
    onClick: () -> Unit
){
    val isCurrentMonth = day.position == DayPosition.MonthDate

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(2.dp, MintDarkGreen, CircleShape)
                } else Modifier
            )
            .background( if (isSelected) MintDarkGreen else Color.Transparent)
            .clickable(enabled = isCurrentMonth) {onClick()}
            .padding(2.dp)
    ){
        Text(
            text = day.date.dayOfMonth.toString(),
            fontSize =  15.sp,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else
            FontWeight.Normal,
            color = when { isSelected -> SurfaceWhite
                isCurrentMonth -> MintDarkGreen
                else -> MintDarkGreen.copy(alpha =0.3f)
            }
        )
        Spacer(modifier = Modifier.height(2.dp))

        if(isCurrentMonth && hasItems){
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) SurfaceWhite else MintDarkGreen)
            )
        } else{
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}


@Composable
fun AgendaCard(
    title: String,
    petName: String,
    timeStr: String,
    badgeText: String,
    badgeColor: Color
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 6.dp, height = 44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "$petName  • $timeStr", fontSize = 13.sp, color = TextMuted)
            }

            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun RoutinesListView(
    routines: List<Routine>,
    petNamesMap: Map<String, String>
) {
    if (routines.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No Routines Created Yet", color = MintDarkGreen.copy(alpha = 0.6f))
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(routines) { routine ->
                val petName = petNamesMap[routine.petId] ?: "Pet"
                AgendaCard(
                    title = routine.title,
                    petName = petName,
                    timeStr = "${routine.frequency} • ${routine.time.toFormattedTime()}",
                    badgeText = "Routine",
                    badgeColor = MintDarkGreen
                )
            }
        }
    }
}

@Composable
fun AgendaTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MintDarkGreen else MintDarkGreen.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(if (isSelected) MintDarkGreen else Color.Transparent)
        )
    }
}

@Composable
fun AddRoutineScreen(
    petsMap: Map<String, String> = mapOf("pet1" to "Nala", "pet2" to "Milo"),
    currentUserId: String = "user123",
    onNavigateBack: () -> Unit,
    onSaveRoutine: (Routine) -> Unit
){
    var selectedPetId by remember { mutableStateOf(petsMap.keys.firstOrNull() ?: "") }
    var isPetDropdownExpanded by remember { mutableStateOf(false) }

    var selectedRoutineType by remember { mutableStateOf("Bath") }
    var customRoutineText by remember { mutableStateOf("") }

    var selectedFrequency by remember { mutableStateOf("Weekly") }
    var selectedDays by remember { mutableStateOf(setOf("Wed")) }

    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var showInCalendar by remember { mutableStateOf(true) }

    val routineTypes = remember {
        listOf(
            RoutineTypeOption("Bath", R.drawable.bathroutine),
            RoutineTypeOption("Ear Cleaning", R.drawable.earroutine),
            RoutineTypeOption("Teeth Cleaning", R.drawable.teethroutine),
            RoutineTypeOption("Brushing", R.drawable.brushroutine),
            RoutineTypeOption("Nail Trimming", R.drawable.nailtrimroutine),
            RoutineTypeOption("Custom", R.drawable.customroutine)
        )
    }
    val daysList = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
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
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", tint = MintDarkGreen,
                        modifier = Modifier.size(50.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("New Routine", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MintDarkGreen)
                Spacer(modifier = Modifier.weight(1.3f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            //Pet Dropdown
            Text("Pet", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(8.dp))
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
                            text = { Text(name, color = TextDark) },
                            onClick = {
                                selectedPetId = id
                                isPetDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //Routine Type Chips
            Text("Routine Type", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                routineTypes.forEach { type ->
                    val isSelected = selectedRoutineType == type.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRoutineType = type.name },
                        label = { Text(type.name, color = if (isSelected) SurfaceWhite else MintDarkGreen, fontWeight = FontWeight.SemiBold, fontSize = 14.sp) },
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

            if (selectedRoutineType == "Custom") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = customRoutineText,
                    onValueChange = { customRoutineText = it },
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

            // Frequency Options
            Text("Frequency", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Daily", "Weekly", "Monthly").forEach { freq ->
                    val isSelected = selectedFrequency == freq
                    Button(
                        onClick = { selectedFrequency = freq },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MintDarkGreen else MintCardSurface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(freq, color = if (isSelected) SurfaceWhite else MintDarkGreen,
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // On Days Selection
            Text("On days", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                daysList.forEach { day ->
                    val isSelected = selectedDays.contains(day)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MintDarkGreen else MintCardSurface)
                            .clickable {
                                selectedDays = if (isSelected) selectedDays - day else selectedDays + day
                            }
                    ) {
                        Text(day, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) SurfaceWhite else MintDarkGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Switches & Details Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Show in Calendar", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Display this routine as an event", fontSize = 12.sp, color = MintDarkGreen.copy(alpha = 0.7f))
                    }
                    Switch(
                        checked = showInCalendar,
                        onCheckedChange = { showInCalendar = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = SurfaceWhite, checkedTrackColor = MintDarkGreen,
                            uncheckedThumbColor = SurfaceWhite, uncheckedIconColor = MintBackground, uncheckedBorderColor = MintDarkGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))


            Button(
                onClick = {
                    val title = if (selectedRoutineType == "Custom") customRoutineText else selectedRoutineType
                    val newRoutine = Routine(
                        userId = currentUserId,
                        petId = selectedPetId,
                        title = title,
                        frequency = selectedFrequency,
                        startDate = startDate.toEpochMilli(),
                        repeatDays = selectedDays.joinToString(",")
                    )
                    onSaveRoutine(newRoutine)
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintDarkGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("CREATE ROUTINE", fontWeight = FontWeight.Bold, color = SurfaceWhite)
            }
        }
    }
}


