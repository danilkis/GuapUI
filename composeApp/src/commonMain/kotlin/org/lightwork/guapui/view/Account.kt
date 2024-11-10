package org.lightwork.guapui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import kotlinx.coroutines.launch
import org.lightwork.guapui.viewmodel.AuthViewmodel

@OptIn(AuthUiExperimental::class)
@Composable
fun AccountPage(
    navController: NavHostController, // Accept NavController as a parameter //TODO: Авторизация как viewmodel
) {
    val authViewmodel = AuthViewmodel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    val corutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                // Sign up or sign in
                corutineScope.launch {
                    val userInfo = if (isLoginMode) {
                        authViewmodel.signIn(email, password)
                    } else {
                        authViewmodel.signUp(email, password)
                    }
                    isLoading = false
                    userInfo?.let {
                        println("User UUID: ${it.toString()}")
                        // Navigate back after successful sign-in or sign-up
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(text = if (isLoginMode) "Login" else "Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { isLoginMode = !isLoginMode }) {
            Text(text = if (isLoginMode) "Don't have an account? Register" else "Already have an account? Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {}, //Login with Google,
            content = { ProviderButtonContent(Google) }
        )
    }
}
