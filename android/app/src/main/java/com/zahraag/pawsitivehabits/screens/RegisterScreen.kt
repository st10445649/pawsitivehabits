package com.zahraag.pawsitivehabits.screens

import android.R.attr.fontWeight
import android.R.attr.onClick
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zahraag.pawsitivehabits.R
import com.zahraag.pawsitivehabits.ui.theme.*
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen
import com.zahraag.pawsitivehabits.ui.theme.TextDark
import com.zahraag.pawsitivehabits.viewmodel.AuthUiState

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegisterClick: (String, String, String, String) -> Unit,
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
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onSignUpSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.Start) {
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

                    if (errorMessage != null || uiState is AuthUiState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: (uiState as AuthUiState.Error).message,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = {
                            when {
                                password != confirmPassword -> {
                                    errorMessage = "Passwords do not match"
                                }

                                email.isBlank() || password.isBlank() -> {
                                    errorMessage = "Please fill in all required fields"
                                }

                                else -> {
                                    errorMessage = null
                                    onRegisterClick(email, password, firstName, lastName)
                                }
                            }
                        },
                        enabled = uiState !is AuthUiState.Loading,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                color = MintDarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
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
        Icon(
            painter = painterResource(id = R.drawable.greenpaws),
            contentDescription = null,
            tint = MintDarkGreen,
            modifier = Modifier.size(160.dp). offset(
                y= (-300).dp,
                x = (-90).dp
            )
        )
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
        placeholder = { Text(placeholder, color = MintDarkGreen.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall)
                      },
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