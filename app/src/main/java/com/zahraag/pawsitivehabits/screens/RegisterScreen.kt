package com.zahraag.pawsitivehabits.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.ui.theme.*
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.TextDark

@Composable
fun RegisterScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            // Paw print decor
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.petnav),
                    contentDescription = null,
                    tint = MintDarkGreen.copy(alpha = 0.7f),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.petnav),
                    contentDescription = null,
                    tint = MintDarkGreen.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(48.dp)
                        .offset(y = (-12).dp)
                )
            }

            // Main card with inputs
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MintCardSurface.copy(alpha = 0.85f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MintDarkGreen
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    UnderlineInputField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "First Name"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UnderlineInputField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Last Name"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UnderlineInputField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UnderlineInputField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "Username"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UnderlineInputField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Password",
                        isPassword = true,
                        isPasswordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UnderlineInputField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Confirm password",
                        isPassword = true,
                        isPasswordVisible = confirmPasswordVisible,
                        onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible }
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = onSignUpSuccess,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "SIGN UP",
                            color = MintDarkGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnderlineInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = MintDarkGreen.copy(alpha = 0.6f)) },
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword && onTogglePassword != null) {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Lock else Icons.Default.Lock,
                        contentDescription = "Toggle password",
                        tint = MintDarkGreen
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MintDarkGreen,
            unfocusedIndicatorColor = MintDarkGreen.copy(alpha = 0.5f),
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}