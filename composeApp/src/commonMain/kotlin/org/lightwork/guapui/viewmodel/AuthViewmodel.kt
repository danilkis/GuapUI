package org.lightwork.guapui.viewmodel

import androidx.lifecycle.ViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class AuthViewmodel {
    val auth = createSupabaseClient(supabaseUrl = "https://vjfdmvrkriajftklozgf.supabase.co", supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZqZmRtdnJrcmlhamZ0a2xvemdmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzA4MzYyMTUsImV4cCI6MjA0NjQxMjIxNX0.RgDjBvwNacNVLK2vqCt-2i-0kx6MaSKEUUWokfc_fcc")
    {
        install(Auth)
        install(Postgrest)
    }.auth

    // Sign up method that returns UserInfo?
    suspend fun signUp(user_mail: String, user_password: String): UserInfo? {
        return auth.signUpWith(Email) {
            email = user_mail
            password = user_password
        }
    }

    // Sign in method that returns UserInfo?
    suspend fun signIn(user_mail: String, user_password: String) {
        return auth.signInWith(Email) {
            email = user_mail
            password = user_password
        }
    }
}
