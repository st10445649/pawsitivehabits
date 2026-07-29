package com.zahraag.pawsitivehabits.data

import androidx.annotation.DrawableRes


sealed class AgendaDisplayItem {
        data class RoutineItem(val routine: Routine) : AgendaDisplayItem()
        data class EventItem(val event: CalendarEvents) : AgendaDisplayItem()
    }

data class RoutineTypeOption(
    val name: String,
    @DrawableRes val iconRes: Int
)
