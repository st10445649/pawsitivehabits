package com.zahraag.pawsitivehabits.screens

import android.R.attr.fontWeight
import android.R.attr.onClick
import android.R.attr.text
import android.R.attr.type
import android.os.Build
import android.text.format.DateUtils.isToday
import android.view.RoundedCorner
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults.dateFormatter
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.zahraag.pawsitivehabits.data.CategoryOption
import com.zahraag.pawsitivehabits.data.RoutineTypeOption
import com.zahraag.pawsitivehabits.data.SampleData.samplePetNamesMap
import com.zahraag.pawsitivehabits.ui.theme.MintMediumGreen
import java.util.Locale.getDefault

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    routinesList: List<Routine> = emptyList(),
    calendarEventsList: List<CalendarEvents> = emptyList(),
    petNamesMap: Map<String, String> = emptyMap(),
    onNavigateBack: () -> Unit,
    onNavigateToAddRoutine: () -> Unit,
    onNavigateToAddCalendarEvent: () -> Unit,
    onNavigateToEditCalendarEvent: (String) -> Unit = {},
    onDeleteCalendarEvent: (CalendarEvents) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

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
                    petNamesMap = petNamesMap,
                    onEditEvent = onNavigateToEditCalendarEvent,
                    onDeleteEvent = onDeleteCalendarEvent
                )
            }else {
                RoutinesListView(routines = routinesList,petNamesMap = petNamesMap)
            }
        }
        FloatingActionButton(
            onClick = { showBottomSheet = true },
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

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = SurfaceWhite,
                dragHandle = { BottomSheetDefaults.DragHandle(color = MintDarkGreen.copy(alpha = 0.4f)) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Create New",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MintDarkGreen,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Add Routine Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MintCardSurface)
                            .clickable {
                                showBottomSheet = false
                                onNavigateToAddRoutine()
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MintDarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.customroutine),
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Add Routine",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "Set recurring tasks like bath or grooming",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add Calendar Event Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MintCardSurface)
                            .clickable {
                                showBottomSheet = false
                                onNavigateToAddCalendarEvent()
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFC8369)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.calendarnav),
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Add Calendar Event",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "Schedule one-time vet visits or playdates",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun CalendarView(
    routines: List<Routine>,
    events: List<CalendarEvents>,
    petNamesMap: Map<String, String>,
    onEditEvent: (String) -> Unit,
    onDeleteEvent: (CalendarEvents) -> Unit
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
                                badgeColor = MintDarkGreen,
                                iconRes = getRoutineIconRes(item.routine.title)
                            )
                        }
                        is AgendaDisplayItem.EventItem -> {
                            val petName = petNamesMap[item.event.petId] ?: "Pet"
                            AgendaCard(
                                title = item.event.title,
                                petName = petName,
                                timeStr = item.event.time.toFormattedTime(),
                                badgeText = item.event.category,
                                badgeColor = Color(0xFFFC8369),
                                iconRes = R.drawable.calendarnav,
                                onEditClick = { onEditEvent(item.event.id) },
                                onDeleteClick = { onDeleteEvent(item.event) }
                            )
                        }
                    }
                }
            }
        }
    }
}
fun getRoutineIconRes(title: String): Int {
    return when (title.lowercase()) {
        "bath" -> R.drawable.bathroutine
        "ear cleaning" -> R.drawable.earroutine
        "teeth cleaning" -> R.drawable.teethroutine
        "brushing" -> R.drawable.brushroutine
        "nail trimming" -> R.drawable.nailtrimroutine
        else -> R.drawable.customroutine
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
    badgeColor: Color,
    iconRes: Int,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

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
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MintMediumGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
            }

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
            if (onEditClick != null || onDeleteClick != null) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextDark
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(SurfaceWhite)
                    ) {
                        onEditClick?.let {
                            DropdownMenuItem(
                                text = { Text("Edit", color = TextDark) },
                                onClick = {
                                    showMenu = false
                                    it()
                                }
                            )
                        }
                        onDeleteClick?.let {
                            DropdownMenuItem(
                                text = { Text("Delete", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    it()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEditCalendarEventScreen(
    petsMap: Map<String, String> = samplePetNamesMap,
    existingEvent: CalendarEvents? = null,
    currentUserId: String = "user123",
    onNavigateBack: () -> Unit,
    onSaveEvent: (CalendarEvents) -> Unit
) {
    var date by remember { mutableStateOf(existingEvent?.date?.toLocalDate() ?: LocalDate.now()) }
    var title by remember { mutableStateOf(existingEvent?.title ?: "") }
    var category by remember { mutableStateOf(existingEvent?.category ?: "Medical") }
    var notes by remember { mutableStateOf(existingEvent?.notes ?: "") }
    var selectedPetId by remember { mutableStateOf(existingEvent?.petId ?: petsMap.keys.firstOrNull() ?: "") }
    var isPetDropdownExpanded by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val categories = remember {
        listOf(
            CategoryOption("Medical", R.drawable.checkupmed), // adjust drawable names as needed
            CategoryOption("Grooming", R.drawable.brushroutine),
            CategoryOption("Vaccination", R.drawable.vaccinemed),
            CategoryOption("Playdate", R.drawable.greenpaws),
            CategoryOption("Other", R.drawable.customroutine)
        )
    }
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
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = MintDarkGreen,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (existingEvent == null) "New Event" else "Edit Event",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintDarkGreen
                )
                Spacer(modifier = Modifier.weight(1.3f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pet Selection Dropdown
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = petsMap[selectedPetId] ?: "Select Pet",
                            fontWeight = FontWeight.Medium,
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

            Spacer(modifier = Modifier.height(20.dp))

            // Event Title Input
            Text("Event Title", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        "e.g. Vet Checkup",
                        color = MintDarkGreen.copy(alpha = 0.3f),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintDarkGreen,
                    unfocusedBorderColor = MintCardSurface,
                    focusedContainerColor = MintCardSurface,
                    unfocusedContainerColor = MintCardSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Category", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = category == cat.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { category = cat.name },
                        label = {
                            Text(
                                cat.name,
                                color = if (isSelected) SurfaceWhite else MintDarkGreen,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = cat.iconRes),
                                    tint = Color.Unspecified,
                                    contentDescription = cat.name,
                                    modifier = Modifier.size(22.dp)
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

            Spacer(modifier = Modifier.height(20.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Text("Date", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(10.dp))
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
                        Text(date.format(dateFormatter), fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MintDarkGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes Input Field
            Text("Notes", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = {
                    Text(
                        "Add extra details...",
                        color = MintDarkGreen.copy(alpha = 0.3f),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintDarkGreen,
                    unfocusedBorderColor = MintCardSurface,
                    focusedContainerColor = MintCardSurface,
                    unfocusedContainerColor = MintCardSurface
                ),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Save Button
            Button(
                onClick = {
                    val eventToSave = existingEvent?.copy(
                        title = title,
                        category = category,
                        notes = notes,
                        petId = selectedPetId
                    ) ?: CalendarEvents(
                        userId = currentUserId,
                        petId = selectedPetId,
                        title = title,
                        category = category,
                        time = System.currentTimeMillis(),
                        notes = notes
                    )
                    onSaveEvent(eventToSave)
                    onNavigateBack()
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintDarkGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (existingEvent == null) "CREATE EVENT" else "SAVE CHANGES",
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
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
                    badgeColor = MintDarkGreen,
                    iconRes = getRoutineIconRes(routine.title)
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

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

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

            Spacer(modifier = Modifier.height(16.dp))

            Text("Start Date", fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
            Spacer(modifier = Modifier.height(10.dp))
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
                        Text(startDate.format(dateFormatter), fontWeight = FontWeight.SemiBold, color = MintDarkGreen)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MintDarkGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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


