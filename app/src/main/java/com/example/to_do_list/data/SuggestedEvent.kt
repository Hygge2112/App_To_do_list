package com.example.to_do_list.data

import java.time.LocalDate
import java.time.LocalTime

// Data class để giữ thông tin sự kiện AI đề xuất
data class SuggestedEvent(
    val title: String,
    val date: LocalDate,
    val time: LocalTime
)