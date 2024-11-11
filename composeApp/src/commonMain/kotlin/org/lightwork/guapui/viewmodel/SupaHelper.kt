package org.lightwork.guapui.helper

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseSessionFromUrl
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.http.*
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

    fun handleOAuthRedirect(url: String) {
        // Check if the URL contains "access_token"
        if (url.contains("access_token")) {
            // Extract the access token from the URL
            val accessToken = extractAccessToken(url)

            // If an access token was successfully extracted, proceed with parsing the session
            if (accessToken != null) {
                GlobalScope.launch {
                    try {
                        // Supabase-kt automatically handles the session
                        val session = auth.parseSessionFromUrl(url)
                        auth.importSession(session)
                    } catch (e: Exception) {
                        e.printStackTrace() // For debugging
                    }
                }
            } else {
                println("Access token could not be extracted.")
            }
        }
    }
    // Function to extract the access token from the URL
    fun extractAccessToken(url: String): String? {
        val tokenPrefix = "#access_token="
        val startIndex = url.indexOf(tokenPrefix)

        if (startIndex != -1) {
            val tokenStart = startIndex + tokenPrefix.length
            val endIndex = url.indexOf("&", tokenStart)

            return if (endIndex != -1) {
                url.substring(tokenStart, endIndex)
            } else {
                url.substring(tokenStart) // Return until the end of the string if no '&' is found
            }
        }

        return null // Return null if "access_token" is not found
    }

    suspend fun signOut() {
        withContext(Dispatchers.Unconfined) {
            try {
                auth.signOut()
                delay(100)
                
            } catch (e: Exception) {
                e.printStackTrace() // For debugging purposes
            }
        }
    }

    // Sign in method that returns UserInfo?
    suspend fun signIn(userMail: String, userPassword: String) {
        val userInfo = auth.signInWith(Email) {
            email = userMail
            password = userPassword
        }
        val jwtToken = auth.currentAccessTokenOrNull()
        if (jwtToken != null) {
            println("JWT Token: $jwtToken")
        } else {
            println("JWT Token not found after sign-in")
        }
    }

    // Method to get the current JWT token
    fun getJwtToken(): String? {
        return base.auth.currentAccessTokenOrNull()
    }
}