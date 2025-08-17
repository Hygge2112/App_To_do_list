package com.example.to_do_list.ai_chat

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_do_list.data.*
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class AIChatViewModel(
    private val habitViewModel: HabitViewModel
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _suggestedEvent = MutableStateFlow<SuggestedEvent?>(null)
    val suggestedEvent = _suggestedEvent.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyCQf2vAPiFTWLxZlzuar-p-ogdJ4syEO1w" // Nhớ thay API Key của bạn
    )

    fun sendMessage(userInput: String) {
        _messages.value += ChatMessage(text = userInput, sender = Sender.USER)
        _suggestedEvent.value = null

        viewModelScope.launch {
            val processingMessage = ChatMessage(text = "...", sender = Sender.AI, isProcessing = true)
            _messages.value += processingMessage

            try {
                val habitsState = habitViewModel.uiState.first()
                val currentHabits = if (habitsState is HabitUiState.Success) habitsState.habits else emptyList()
                val prompt = buildDetailedPrompt(userInput, currentHabits)
                val response = generativeModel.generateContent(prompt)
                handleApiResponse(response)
            } catch (e: Exception) {
                Log.e("AIChatViewModel", "Lỗi khi gọi API Gemini: ", e)
                handleApiError()
            }
        }
    }

    fun scheduleSuggestedEvent() {
        viewModelScope.launch {
            _suggestedEvent.value?.let { event ->
                val newHabit = Habit(
                    name = event.title,
                    repetitionDates = listOf(event.date.format(DateTimeFormatter.ISO_LOCAL_DATE)),
                    reminderTime = event.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    iconName = "Event"
                )
                habitViewModel.addHabit(newHabit) {
                    // Callback sau khi thêm thói quen
                }
                _messages.value += ChatMessage(text = "OK! Đã đặt lịch thành công cho '${event.title}'.", sender = Sender.AI)
                _suggestedEvent.value = null
            }
        }
    }

    // <-- SỬA ĐỔI: Nâng cấp hàm xử lý phản hồi -->
    private fun handleApiResponse(response: GenerateContentResponse) {
        _messages.value = _messages.value.filterNot { it.isProcessing }
        val responseText = response.text ?: ""

        // Cố gắng trích xuất chuỗi JSON từ bên trong khối mã Markdown
        val jsonStartIndex = responseText.indexOf('{')
        val jsonEndIndex = responseText.lastIndexOf('}')

        if (jsonStartIndex != -1 && jsonEndIndex != -1 && jsonEndIndex > jsonStartIndex) {
            val jsonString = responseText.substring(jsonStartIndex, jsonEndIndex + 1)
            try {
                // Phân tích chuỗi JSON đã được trích xuất
                val jsonObject = JSONObject(jsonString)
                val title = jsonObject.getString("title")
                val dateStr = jsonObject.getString("date")
                val timeStr = jsonObject.getString("time")
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                val time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))

                _suggestedEvent.value = SuggestedEvent(title, date, time)

                val suggestionMessage = "Tôi tìm thấy một khoảng thời gian phù hợp: " +
                        "**${title}** vào lúc **$timeStr** ngày **$dateStr**. Bạn có muốn đặt lịch không?"
                _messages.value += ChatMessage(text = suggestionMessage, sender = Sender.AI)
                return // Thoát hàm sau khi xử lý thành công
            } catch (e: Exception) {
                // Nếu trích xuất được nhưng không phân tích được, coi như văn bản thường
                Log.w("AIChatViewModel", "Không thể phân tích chuỗi JSON được trích xuất.", e)
            }
        }

        // Nếu không tìm thấy JSON hoặc phân tích lỗi, hiển thị như văn bản thường
        _suggestedEvent.value = null
        _messages.value += ChatMessage(text = responseText, sender = Sender.AI)
    }

    private fun handleApiError() {
        _messages.value = _messages.value.filterNot { it.isProcessing }
        _messages.value += ChatMessage(text = "Rất tiếc, đã có lỗi xảy ra. Vui lòng thử lại.", sender = Sender.AI)
    }

    private fun buildDetailedPrompt(userInput: String, habits: List<Habit>): String {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val habitsJson = habits.joinToString(",\n") {
            "  { \"name\": \"${it.name}\", \"dates\": ${it.repetitionDates}, \"time\": \"${it.reminderTime}\" }"
        }

        return """
        Bạn là một trợ lý AI thông minh trong một ứng dụng To-Do List.
        Nhiệm vụ của bạn là giúp người dùng tìm thời gian và đặt lịch.
        Hôm nay là ngày: $today.
        Lịch trình hiện tại của người dùng (chỉ chứa các sự kiện trong tương lai):
        [
        $habitsJson
        ]
        Yêu cầu của người dùng: "$userInput"

        Phân tích yêu cầu và lịch trình.
        1. Nếu bạn có thể tìm thấy một thời gian trống hợp lệ, CHỈ trả lời bằng một đối tượng JSON duy nhất có dạng:
           {"title": "tên công việc", "date": "YYYY-MM-DD", "time": "HH:mm"}
        2. Nếu không tìm thấy thời gian trống, hoặc nếu yêu cầu của người dùng không rõ ràng hoặc không phải là yêu cầu đặt lịch, hãy trò chuyện bình thường để làm rõ hoặc trả lời câu hỏi.
        """.trimIndent()
    }
}