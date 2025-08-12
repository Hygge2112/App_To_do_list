package com.example.to_do_list.ai_chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Event // <-- THÊM MỚI
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_list.data.ChatMessage
import com.example.to_do_list.data.HabitViewModel
import com.example.to_do_list.data.Sender
import kotlinx.coroutines.launch

// Factory để tạo AIChatViewModel và truyền HabitViewModel vào
@RequiresApi(Build.VERSION_CODES.O)
class AIChatViewModelFactory(private val habitViewModel: HabitViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AIChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AIChatViewModel(habitViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    navController: NavController,
    habitViewModel: HabitViewModel
) {
    val viewModel: AIChatViewModel = viewModel(factory = AIChatViewModelFactory(habitViewModel))

    val messages by viewModel.messages.collectAsState()
    val suggestedEvent by viewModel.suggestedEvent.collectAsState()
    var userInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Agent") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages) { message ->
                    // Xác định xem tin nhắn hiện tại có phải là tin nhắn gợi ý đang hoạt động không
                    val isSuggestionMessage = suggestedEvent != null &&
                            message.sender == Sender.AI &&
                            message.text.contains("Bạn có muốn đặt lịch không?")

                    ChatMessageBubble(
                        message = message,
                        isSuggestion = isSuggestionMessage,
                        onScheduleClick = {
                            viewModel.scheduleSuggestedEvent()
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text("VD: Đặt lịch đi khám răng vào ngày mai") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            viewModel.sendMessage(userInput.trim())
                            userInput = ""
                        }
                    },
                    enabled = userInput.isNotBlank()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gửi")
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isSuggestion: Boolean,
    onScheduleClick: () -> Unit
) {
    val isUserMessage = message.sender == Sender.USER
    val horizontalAlignment = if (isUserMessage) Alignment.End else Alignment.Start
    val bubbleColor = if (isUserMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = horizontalAlignment
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = message.text, color = contentColorFor(backgroundColor = bubbleColor))
        }

        if (isSuggestion) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onScheduleClick) {
                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("Đặt lịch ngay")
            }
        }
    }
}