package org.lightwork.guapui.helper

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.*

class SupabaseHelper(supabaseUrl: String, supabaseKey: String) {
    public val base = createSupabaseClient(supabaseUrl, supabaseKey) {
        install(Auth)
        install(Postgrest)
        install(ComposeAuth) {
            googleNativeLogin(serverClientId = "530606051062-du7jddpftfuk7u1o1e487d0rer3d890d.apps.googleusercontent.com")
        }
    }

    private val auth = base.auth

    // Sign up method that returns UserInfo?
    suspend fun signUp(userMail: String, userPassword: String) {
       auth.signUpWith(Email) {
            email = userMail
            password = userPassword
        }
    }

    suspend fun signOut() {
        withContext(Dispatchers.Unconfined) {
            try {
                auth.signOut()
                delay(100) // Небольшая задержка, чтобы избежать конфликтов
                base.auth.refreshCurrentSession()
            } catch (e: Exception) {
                e.printStackTrace() // Для отладки ошибки отмены запроса
            }
        }
    }

    // Sign in method that returns UserInfo?
    suspend fun signIn(userMail: String, userPassword: String) {
        val userInfo = auth.signInWith(Email) {
            email = userMail
            password = userPassword
        }
        //return base.auth.currentAccessTokenOrNull() // JWT token
    }

    // Method to get the current JWT token
    fun getJwtToken(): String? {
        return base.auth.currentAccessTokenOrNull()
    }
}