package com.example.to_do_list.data

enum class Sender {
    USER, AI
}

data class ChatMessage(
    val text: String,
    val sender: Sender,
    val isProcessing: Boolean = false // Dùng để hiển thị trạng thái "AI đang gõ..."
)