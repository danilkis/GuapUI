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
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import io.github.jan.supabase.compose.auth.ui.password.PasswordRule
import io.github.jan.supabase.compose.auth.ui.password.rememberPasswordRuleList
import kotlinx.coroutines.launch
import org.lightwork.guapui.viewmodel.AuthViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import guapui.composeapp.generated.resources.Res
import guapui.composeapp.generated.resources.github
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Github
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import org.jetbrains.compose.resources.painterResource
import org.lightwork.guapui.viewmodel.AuthStatus


@OptIn(AuthUiExperimental::class, ExperimentalMaterial3Api::class)
@Composable
fun AccountPage(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val corutineScope = rememberCoroutineScope()
    val authStatus by authViewModel.authStatus.collectAsState(initial = false)  // Observe authentication state

    // State for showing the alert dialog
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Handle Google and Github sign-ins
    val GoogleAction = authViewModel.supabaseHelper.base.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                is NativeSignInResult.Success -> {
                    navController.popBackStack() // Navigate back after successful login
                }

                is NativeSignInResult.Error -> {
                    errorMessage = "Не получилось войти, попробуйте еще раз"
                    showDialog = true
                }

                is NativeSignInResult.NetworkError -> {
                    errorMessage = "Сетевая ошибка, попробуйте еще раз через пару минут"
                    showDialog = true
                }

                else -> {}
            }
        }
    )

    if (authStatus == AuthStatus.AUTHENTICATED) {
        // Fetch user data when authenticated
        val userInfo = remember { mutableStateOf<UserInfo?>(null) }

        LaunchedEffect(Unit) {
            userInfo.value = authViewModel.getUserInfo()
        }

        userInfo.value?.let { user ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Email: ${user.email}")
                Text("Провайдер: ${user.identities?.get(0)?.provider}")
                Text("Запись созданна: ${user.identities?.get(0)?.createdAt}")
                Text("Последний вход: ${user.identities?.get(0)?.lastSignInAt}")
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    corutineScope.launch {
                        authViewModel.signOut()
                    }
                    navController.popBackStack() // Navigate back after logout
                }) {
                    Text("Выйти")
                }
            }
        }
    } else {
        // If the user is not authenticated, show login/register UI
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoginMode by remember { mutableStateOf(true) }
        var isLoading by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        // State for showing the alert dialog
        var showDialog by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedEmailField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Почта") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedPasswordField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                rules = rememberPasswordRuleList(
                    PasswordRule.minLength(6),
                    PasswordRule.containsDigit(),
                    PasswordRule.containsLowercase(),
                    PasswordRule.containsUppercase()
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        val userInfo = if (isLoginMode) {
                            authViewModel.signIn(email, password)
                        } else {
                            authViewModel.signUp(email, password)
                        }
                        isLoading = false
                        userInfo?.let {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(text = if (isLoginMode) "Вход" else "Регистрация")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { isLoginMode = !isLoginMode }) {
                Text(text = if (isLoginMode) "Еще нет аккаунта? Регистрация" else "Уже есть аккаунт? Вход")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google sign-in button
            OutlinedButton(
                onClick = { GoogleAction.startFlow() },
                content = { ProviderButtonContent(Google) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        authViewModel.supabaseHelper.base.auth.signInWith(Github)
                    }
                },
                content = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(Res.drawable.github),
                            contentDescription = "GitHub",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Войти через GitHub")
                    }
                }
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ошибка авторизации")
                }
            },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}