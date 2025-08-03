package com.example.to_do_list.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Data class đại diện cho một thói quen
data class Habit(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val color: String = "#4CAF50", // Lưu màu dưới dạng mã hex
    val repetitionDates: List<String> = emptyList(), // Lưu ngày dưới dạng "yyyy-MM-dd"
    val reminderTime: String? = null, // Lưu giờ dưới dạng "HH:mm"
    val isCompleted: Boolean = false,
    @ServerTimestamp
    val createdAt: Date = Date()
)
