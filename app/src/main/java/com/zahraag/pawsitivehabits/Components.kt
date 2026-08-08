package com.zahraag.pawsitivehabits

import android.R.attr.singleLine
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zahraag.pawsitivehabits.ui.theme.MintCardSurface
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.TextDark

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: Int
){

    object Home: BottomNavItem("home", "Home", R.drawable.homenav)
    object Pets: BottomNavItem("pets", "Pets", R.drawable.petnav)
    object Agenda: BottomNavItem("agenda", "Agenda", R.drawable.calendarnav)
    object Features: BottomNavItem("features", "Features", R.drawable.featuresnav)
    object Profile: BottomNavItem("setting", "Profile", R.drawable.profilenav)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MintInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = MintDarkGreen.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall) },
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MintCardSurface.copy(alpha = 0.6f),
            unfocusedContainerColor = MintCardSurface.copy(alpha = 0.6f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark
        ),
        shape = RoundedCornerShape(28.dp),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    )
}

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = MintDarkGreen,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}