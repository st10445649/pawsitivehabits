package com.zahraag.pawsitivehabits.ui.theme

import android.R.attr.fontFamily
import android.hardware.lights.Light
import androidx.compose.material3.Typography
import androidx.compose.ui.input.key.Key.Companion.F
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.zahraag.pawsitivehabits.R

// Set of Material typography styles to start with

val MontserratFontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = MontserratFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
    fontFamily = MontserratFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
    fontFamily = MontserratFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp
    ),
    labelSmall = TextStyle(
    fontFamily = MontserratFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp
    ),

    displayLarge = TextStyle(fontFamily = MontserratFontFamily),
    displayMedium = TextStyle(fontFamily = MontserratFontFamily),
    displaySmall = TextStyle(fontFamily = MontserratFontFamily),
    headlineSmall = TextStyle(fontFamily = MontserratFontFamily),
    titleLarge = TextStyle(fontFamily = MontserratFontFamily),
    titleMedium = TextStyle(fontFamily = MontserratFontFamily),
    titleSmall = TextStyle(fontFamily = MontserratFontFamily),
    bodySmall = TextStyle(fontFamily = MontserratFontFamily),
    labelLarge = TextStyle(fontFamily = MontserratFontFamily),
    labelMedium = TextStyle(fontFamily = MontserratFontFamily),
)