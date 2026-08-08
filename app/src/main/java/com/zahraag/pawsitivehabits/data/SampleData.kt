package com.zahraag.pawsitivehabits.data

import android.health.connect.datatypes.WeightRecord
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

    val sampleExpenses = listOf(
        Expenses(
            userId = "user123",
            petId = "pet1", // Nala
            title = "Premium Salmon Kibble",
            amount = 45.99,
            category = "Food",
            date = System.currentTimeMillis() - 86400000L * 2, // 2 days ago
            notes = "Bought 10kg bag from Vet Shop"
        ),
        Expenses(
            userId = "user123",
            petId = "pet1", // Nala
            title = "Rabies Booster Shot",
            amount = 85.00,
            category = "Medical",
            date = System.currentTimeMillis() - 86400000L * 10, // 10 days ago
            notes = "Annual vaccination checkup"
        ),
        Expenses(
            userId = "user123",
            petId = "pet2", // Milo
            title = "Chew Toys & Ball Set",
            amount = 22.50,
            category = "Toys",
            date = System.currentTimeMillis() - 86400000L * 5, // 5 days ago
            notes = "Squeaky toys"
        ),
        Expenses(
            userId = "user123",
            petId = "pet2", // Milo
            title = "Full Coat Grooming",
            amount = 60.00,
            category = "Grooming",
            date = System.currentTimeMillis() - 86400000L * 14, // 14 days ago
            notes = "Includes nail clip and ear wash"
        )
    )

    val sampleWeightRecords = listOf(
        // Weight history for Nala (pet1) - Progressive monthly tracking
        Weight(
            userId = "user123",
            petId = "pet1",
            weightValue = 12.4,
            date = System.currentTimeMillis() - 86400000L * 90,
            unit = "kg"
        ),
        Weight(
            userId = "user123",
            petId = "pet1",
            weightValue =  12.8,
            date = System.currentTimeMillis() - 86400000L * 60,
            unit = "kg"

        ),
        Weight(
            userId = "user123",
            petId = "pet1",
            weightValue =  13.1,
            date = System.currentTimeMillis() - 86400000L * 30,
            unit = "kg"
        ),
        Weight(
            userId = "user123",
            petId = "pet1",
            weightValue =  12.8,
            date = System.currentTimeMillis(),
            unit = "kg"
        ),

        // Weight history for Milo (pet2)
        Weight(
            userId = "user123",
            petId = "pet2",
            weightValue = 8.4,
            date = System.currentTimeMillis() - 86400000L * 90,
            unit = "kg"
        ),
        Weight(
            userId = "user123",
            petId = "pet2",
            weightValue =  8.4,
            date = System.currentTimeMillis() - 86400000L * 60,
            unit = "kg"

        ),
        Weight(
            userId = "user123",
            petId = "pet2",
            weightValue =  8.7,
            date = System.currentTimeMillis() - 86400000L * 30,
            unit = "kg"
        ),
    )

    val sampleMedicalRecords = listOf(
        MedicalRecords(
            userId = "user123",
            petId = "pet1", // Nala
            title = "DHPP Vaccination",
            category = "Vaccination",
            date = System.currentTimeMillis() - 86400000L * 180, // 6 months ago
            nextDueDate = System.currentTimeMillis() + 86400000L * 185, // Due in ~6 months
            clinic = "Oakwood Veterinary Care",
            vet= "Dr Jones",
            notes = "Core 3-year core vaccine administered."
        ),
        MedicalRecords(
            userId = "user123",
            petId = "pet1", // Nala
            title = "Deworming Treatment",
            category = "Medication",
            date = System.currentTimeMillis() - 86400000L * 30,
            nextDueDate = System.currentTimeMillis() + 86400000L * 60,
            clinic = "Oakwood Veterinary Care",
            vet= "Dr Jones",
            notes = "Oral tablet given with meal."
        ),
        MedicalRecords(
            userId = "user123",
            petId = "pet2", // Milo
            title = "Rabies Vaccine",
            category = "Vaccination",
            date = System.currentTimeMillis() - 86400000L * 90,
            nextDueDate = System.currentTimeMillis() + 86400000L * 275,
            vet= "Dr Jones",
            clinic = "City Pet Hospital",
            notes = "No adverse reaction noticed."
        ),
        MedicalRecords(
            userId = "user123",
            petId = "pet2", // Milo
            title = "Chicken Protein Sensitivity",
            category = "Allergies",
            date = System.currentTimeMillis() - 86400000L * 120,
            nextDueDate = null,
            vet= "Dr Jones",
            clinic = "City Pet Hospital",
            notes = "Mild skin rash when fed chicken kibble. Switch to fish/lamb diet."
        )
    )
}