
package com.example.biztrack

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

private val RegisterBrandGreen = Color(0xFF164B38)
private val RegisterAccentGreen = Color(0xFF23845B)
private val RegisterPageBackground = Color(0xFFF4F8F5)

class RegisterActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authRepository = AuthRepository(applicationContext)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = RegisterAccentGreen,
                    onPrimary = Color.White,
                    background = RegisterPageBackground,
                    surface = Color.White
                )
            ) {
                RegisterScreen(
                    onRegister = { fullName, businessName, email, password ->
                        register(fullName, businessName, email, password)
                    },
                    onLogin = {
                        finish()
                    }
                )
            }
        }
    }

    private fun register(
        fullName: String,
        businessName: String,
        email: String,
        password: String
    ) {
        lifecycleScope.launch {
            val userId = authRepository.register(
                fullName = fullName,
                businessName = businessName,
                email = email,
                password = password
            )

            if (userId != null) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Account created successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(this@RegisterActivity, MainActivity::class.java)
                )
                finishAffinity()
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "Registration failed. This email may already be registered.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

@Composable
private fun RegisterScreen(
    onRegister: (String, String, String, String) -> Unit,
    onLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RegisterPageBackground)
            .verticalScroll(rememberScrollState())
    ) {

        // Branded header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    RegisterBrandGreen,
                    RoundedCornerShape(
                        bottomStart = 30.dp,
                        bottomEnd = 30.dp
                    )
                )
                .padding(
                    horizontal = 26.dp,
                    vertical = 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BIZTRACK",
                color = Color(0xFFC5DC69),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start managing your business smarter.",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Registration form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-18).dp)
                .background(
                    Color.White,
                    RoundedCornerShape(22.dp)
                )
                .padding(22.dp)
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineSmall,
                color = RegisterBrandGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Set up your BizTrack business profile.",
                color = Color(0xFF718078),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = ""
                },
                label = { Text("Full name") },
                placeholder = { Text("Enter your full name") },
                singleLine = true,
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegisterAccentGreen,
                    focusedLabelColor = RegisterAccentGreen,
                    cursorColor = RegisterAccentGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(13.dp))

            OutlinedTextField(
                value = businessName,
                onValueChange = {
                    businessName = it
                    errorMessage = ""
                },
                label = { Text("Business name") },
                placeholder = { Text("Enter your business name") },
                singleLine = true,
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegisterAccentGreen,
                    focusedLabelColor = RegisterAccentGreen,
                    cursorColor = RegisterAccentGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(13.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = { Text("Email address") },
                placeholder = { Text("Enter your email") },
                singleLine = true,
                shape = RoundedCornerShape(13.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegisterAccentGreen,
                    focusedLabelColor = RegisterAccentGreen,
                    cursorColor = RegisterAccentGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(13.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = { Text("Password") },
                placeholder = { Text("Minimum 8 characters") },
                singleLine = true,
                shape = RoundedCornerShape(13.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegisterAccentGreen,
                    focusedLabelColor = RegisterAccentGreen,
                    cursorColor = RegisterAccentGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(13.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                label = { Text("Confirm password") },
                placeholder = { Text("Re-enter your password") },
                singleLine = true,
                shape = RoundedCornerShape(13.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegisterAccentGreen,
                    focusedLabelColor = RegisterAccentGreen,
                    cursorColor = RegisterAccentGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    when {
                        fullName.isBlank() ->
                            errorMessage = "Enter your full name."

                        businessName.isBlank() ->
                            errorMessage = "Enter your business name."

                        !Patterns.EMAIL_ADDRESS
                            .matcher(email.trim()).matches() ->
                            errorMessage = "Enter a valid email address."

                        password.length < 8 ->
                            errorMessage = "Password must have at least 8 characters."

                        password != confirmPassword ->
                            errorMessage = "Passwords do not match."

                        else -> onRegister(
                            fullName.trim(),
                            businessName.trim(),
                            email.trim(),
                            password
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegisterAccentGreen
                )
            ) {
                Text(
                    text = "Create Account",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    color = Color(0xFF718078)
                )

                TextButton(onClick = onLogin) {
                    Text(
                        text = "Log In",
                        color = RegisterAccentGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
