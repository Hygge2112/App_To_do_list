package com.example.to_do_list.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object Supabase {

    // QUAN TRỌNG: Hãy thay thế bằng Supabase URL và Key thật của bạn
    private const val SUPABASE_URL = "YOUR_SUPABASE_URL"
    private const val SUPABASE_KEY = "YOUR_SUPABASE_ANON_KEY"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        // Công cụ Ktor client engine sẽ được tự động nhận diện
        // nếu bạn đã thêm dependency 'io.ktor:ktor-client-android'.
        // Dòng httpEngine không hợp lệ ở đây.

        // Cài đặt các plugin bạn cần
        install(Auth)
        install(Postgrest)
        // install(Storage)
        // install(Realtime)
    }
}