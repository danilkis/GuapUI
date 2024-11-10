package org.lightwork.guapui.viewmodel

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient

class SupaHelper {
    val supabase = createSupabaseClient(supabaseUrl = "https://vjfdmvrkriajftklozgf.supabase.co", supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZqZmRtdnJrcmlhamZ0a2xvemdmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzA4MzYyMTUsImV4cCI6MjA0NjQxMjIxNX0.RgDjBvwNacNVLK2vqCt-2i-0kx6MaSKEUUWokfc_fcc") {}

    public fun fetchAuth(): Auth {
        return supabase.auth
    }
}