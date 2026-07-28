package com.zahraag.pawsitivehabits.data


    sealed class AgendaDisplayItem {
        data class RoutineItem(val routine: Routine) : AgendaDisplayItem()
        data class EventItem(val event: CalendarEvents) : AgendaDisplayItem()
    }
