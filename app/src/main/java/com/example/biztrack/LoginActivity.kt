
package com.example.biztrack

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

private val BrandGreen = Color(0xFF164B38)
private val AccentGreen = Color(0xFF23845B)
private val LightGreen = Color(0xFFE5F1E9)
private val PageBackground = Color(0xFFF4F8F5)

class LoginActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authRepository = AuthRepository(applicationContext)

        if (authRepository.getCurrentUserId() != null) {
            openDashboard()
            return
        }

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = AccentGreen,
                    onPrimary = Color.White,
                    background = PageBackground,
                    surface = Color.White
                )
            ) {
                LoginScreen(
                    onLogin = { email, password ->
                        login(email, password)
                    },
                    onRegister = {
                        startActivity(
                            Intent(this, RegisterActivity::class.java)
                        )
                    }
                )
            }
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            val success = authRepository.login(email, password)

            if (success) {
                openDashboard()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Invalid email or password.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openDashboard() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@Composable
private fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {

        // Branded header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    BrandGreen,
                    RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
                .padding(
                    start = 28.dp,
                    end = 28.dp,
                    top = 65.dp,
                    bottom = 55.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BIZTRACK",
                color = Color(0xFFC5DC69),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your business. Your finances. Your growth.",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Login panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .offset(y = (-28).dp)
                .background(
                    Color.White,
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineSmall,
                color = BrandGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sign in to manage your business finances.",
                color = Color(0xFF718078),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(26.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = { Text("Email address") },
                placeholder = { Text("Enter your email") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGreen,
                    focusedLabelColor = AccentGreen,
                    cursorColor = AccentGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGreen,
                    focusedLabelColor = AccentGreen,
                    cursorColor = AccentGreen
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        email.isBlank() ->
                            errorMessage = "Enter your email address."

                        !Patterns.EMAIL_ADDRESS
                            .matcher(email.trim()).matches() ->
                            errorMessage = "Enter a valid email address."

                        password.isBlank() ->
                            errorMessage = "Enter your password."

                        else -> onLogin(email.trim(), password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGreen
                )
            ) {
                Text(
                    text = "Log In",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    color = Color(0xFF718078)
                )

                TextButton(onClick = onRegister) {
                    Text(
                        text = "Register",
                        color = AccentGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
