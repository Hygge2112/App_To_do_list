package com.example.to_do_list.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Habit(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val color: String = "#4CAF50",
    val repetitionDates: List<String> = emptyList(),
    val reminderTime: String? = null,
    val completedDates: List<String> = emptyList(),

    // THÊM MỚI: Trường để lưu tên định danh của icon
    val iconName: String = "EditNote", // "EditNote" là icon mặc định

    @ServerTimestamp
    val createdAt: Date = Date()
)
