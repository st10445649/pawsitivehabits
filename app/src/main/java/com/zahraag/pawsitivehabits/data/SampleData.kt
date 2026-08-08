package com.zahraag.pawsitivehabits.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Collections.frequency
import java.util.concurrent.TimeUnit

object SampleData {
    val samplePets = listOf(
        Pet(
            id = "1",
            userId = "user_1",
            name = "Milo",
            petType = "Dog",
            breed = "Golden Retriever",
            dateOfBirth = System.currentTimeMillis() - 63072000000L, // ~2 yrs old
            adoptionDate = System.currentTimeMillis() - 63072000000L,
            microchipId = "985141002341234",
            isNeutered = true,
            imageUrl = null
        ),
        Pet(
            id = "2",
            userId = "user_1",
            name = "Luna",
            petType = "Cat",
            breed = "Siamese",
            dateOfBirth = System.currentTimeMillis() - 31536000000L,
            adoptionDate = System.currentTimeMillis() - 31536000000L,// ~1 yr old
            microchipId = "985141002341999",
            isNeutered = true,
            imageUrl = null
        )
    )

    @RequiresApi(Build.VERSION_CODES.O)
    val sampleTasks = listOf(
        Routine(
            id = "101",
            userId= "user_1",
            petId = "1",
            title = "Morning Teeth Brushing",
            time = TimeUnit.HOURS.toMillis(8),
            frequency = "DAILY",
            repeatDays = "MON,TUE,WED,THU,FRI,SAT,SUN",
            startDate = System.currentTimeMillis(),
            reminderMinutes = 30
        ),
        Routine(
            id = "102",
            userId= "user_1",
            petId = "1",
            title = "Evening Walk & Fetch",
            time = TimeUnit.HOURS.toMillis(17),
            frequency = "DAILY",
            repeatDays = "MON,TUE,WED,THU,FRI,SAT,SUN",
            startDate = System.currentTimeMillis(),
            reminderMinutes = 30
        )
    )

    // SampleData.kt

    val samplePetNamesMap = mapOf(
        "pet1" to "Nala",
        "pet2" to "Milo"
    )

    val sampleRoutines = listOf(
        Routine(
            userId = "user123",
            petId = "pet1",
            title = "Bath",
            frequency = "Weekly",
            startDate = System.currentTimeMillis(),
            repeatDays = "Wed,Sat",
            time = System.currentTimeMillis()
        ),
        Routine(
            userId = "user123",
            petId = "pet2",
            title = "Teeth Cleaning",
            frequency = "Daily",
            startDate = System.currentTimeMillis(),
            repeatDays = "Mon,Tue,Wed,Thu,Fri,Sat,Sun",
            time = System.currentTimeMillis() + 3600000
        )
    )

    val sampleCalendarEvents = listOf(
        CalendarEvents(
            userId = "user123",
            petId = "pet1",
            title = "Rabies Vaccination",
            category = "Medical",
            time = System.currentTimeMillis(),
            notes = "Annual booster shot at Vet Clinic."
        ),
        CalendarEvents(
            userId = "user123",
            petId = "pet2",
            title = "Grooming Appointment",
            category = "Grooming",
            time = System.currentTimeMillis() + 86400000,
            notes = "Full coat trim and nail grinding."
        )
    )
}