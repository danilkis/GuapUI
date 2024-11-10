package org.lightwork.guapui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.lightwork.guapui.helper.SupabaseHelper
enum class AuthStatus {
    AUTHENTICATED,
    UNAUTHENTICATED,
    INITIALIZING,
    REFRESH_FAILURE,
    SIGN_OUT
}

class AuthViewModel : ViewModel() {

    val supabaseHelper = SupabaseHelper(
        supabaseUrl = "https://vjfdmvrkriajftklozgf.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZqZmRtdnJrcmlhamZ0a2xvemdmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzA4MzYyMTUsImV4cCI6MjA0NjQxMjIxNX0.RgDjBvwNacNVLK2vqCt-2i-0kx6MaSKEUUWokfc_fcc"
    )

    val authStatus = MutableStateFlow(AuthStatus.INITIALIZING)

    init {
        // Collecting the session status to handle different states
        viewModelScope.launch {
            supabaseHelper.base.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        authStatus.value = AuthStatus.AUTHENTICATED
                        println("Authenticated")
                    }
                    SessionStatus.Initializing -> {
                        authStatus.value = AuthStatus.INITIALIZING
                        println("Init")
                    }
                    is SessionStatus.RefreshFailure -> {
                        authStatus.value = AuthStatus.REFRESH_FAILURE
                        println("Failed")
                    }
                    is SessionStatus.NotAuthenticated -> {
                         if(status.isSignOut) {
                             authStatus.value = AuthStatus.SIGN_OUT
                             println("User signed out")
                         } else {
                             authStatus.value = AuthStatus.UNAUTHENTICATED
                             println("User not signed in")
                         }
                    }
                }
            }
        }
    }

    // Sign up method
    fun signUp(userMail: String, userPassword: String) {
        viewModelScope.launch {
            supabaseHelper.signUp(userMail, userPassword)
        }
    }

    fun checkUrl(url: String) {
        viewModelScope.launch {
            supabaseHelper.handleOAuthRedirect(url)
        }
    }
    // Sign in method that returns JWT token
    suspend fun signIn(userMail: String, userPassword: String) {
        supabaseHelper.signIn(userMail, userPassword)
    }
    suspend fun signOut() {
        supabaseHelper.signOut()
    }
    suspend fun getUserInfo(): UserInfo? {
        val token = supabaseHelper.base.auth.currentAccessTokenOrNull()
        return if (token != null) {
            supabaseHelper.base.auth.retrieveUserForCurrentSession(updateSession = true)
        } else {
            null
        }
    }
}