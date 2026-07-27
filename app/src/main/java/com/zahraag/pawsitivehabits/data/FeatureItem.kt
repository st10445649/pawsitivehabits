package com.zahraag.pawsitivehabits.data

import androidx.compose.ui.graphics.Color
import com.zahraag.pawsitivehabits.R

data class FeatureItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val backgroundColor: Color,
    val route: String
)

val featureItemsList = listOf(
    FeatureItem(
        title = "My Pets",
        subtitle = "Manage Pets",
        iconRes = R.drawable.bluepaws,
        backgroundColor = Color(0xFF5bc9ff),
        route = "pets"
    ),
    FeatureItem(
        title = "Calendar",
        subtitle = "Track tasks and\nreminders",
        iconRes = R.drawable.pinkcal,
        backgroundColor = Color(0xFFe2a9f1),
        route = "agenda"
    ),
    FeatureItem(
        title = "Expense\nTracker",
        subtitle = "Track pet expenses",
        iconRes = R.drawable.money,
        backgroundColor = Color(0xFF7875ff),
        route = "expenses"
    ),
    FeatureItem(
        title = "Medical\nRecords",
        subtitle = "Track vaccinations\nand medication",
        iconRes = R.drawable.meds,
        backgroundColor = Color(0xFF5ab0ff),
        route = "medical"
    ),
    FeatureItem(
        title = "Weight\nTracker",
        subtitle = "Monitor weight\nchanges",
        iconRes = R.drawable.weight,
        backgroundColor = Color(0xFFFBB870),
        route = "weight"
    ),
    FeatureItem(
        title = "Memories",
        subtitle = "Store precious\nmoments",
        iconRes = R.drawable.memories,
        backgroundColor = Color(0xFFF78BB4),
        route = "memories"
    ),
    FeatureItem(
        title = "Emergency\nContacts",
        subtitle = "Important contact\ndetails",
        iconRes = R.drawable.contacts,
        backgroundColor = Color(0xFFFC8369),
        route = "emergency"
    ),
    FeatureItem(
        title = "Settings",
        subtitle = "Edit Settings or\nProfile details",
        iconRes = R.drawable.profile,
        backgroundColor = Color(0xFF55A480),
        route = "settings"
    )
)