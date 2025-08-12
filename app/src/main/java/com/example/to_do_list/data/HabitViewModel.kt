package com.example.to_do_list.data

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "HabitViewModel_DEBUG"

@RequiresApi(Build.VERSION_CODES.O)
class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val alarmScheduler = AlarmScheduler(application)

    private val _uiState = MutableStateFlow<HabitUiState>(HabitUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadAllHabits()
    }

    private fun loadAllHabits() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = HabitUiState.Success(emptyList())
            return
        }

        db.collection("habits")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    _uiState.value = HabitUiState.Failure(error.message ?: "Lỗi không xác định")
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val habitList = snapshots.documents.mapNotNull { doc ->
                        doc.toObject<Habit>()?.copy(id = doc.id)
                    }
                    _uiState.value = HabitUiState.Success(habitList)
                }
            }
    }

    fun addHabit(habit: Habit, onComplete: () -> Unit) {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                Log.d(TAG, "--- Bắt đầu thêm thói quen mới ---")
                Log.d(TAG, "Dữ liệu Habit nhận được: $habit")
                Log.d(TAG, "Thời gian nhắc nhở (reminderTime): ${habit.reminderTime}")
                Log.d(TAG, "Ngày lặp lại (repetitionDates): ${habit.repetitionDates}")
                Log.d(TAG, "------------------------------------")

                val documentRef = db.collection("habits").add(habit.copy(userId = currentUser.uid)).await()
                val newHabitWithId = habit.copy(id = documentRef.id)

                alarmScheduler.schedule(newHabitWithId)
                Log.d(TAG, "Đã gửi yêu cầu đặt báo thức cho thói quen: ${newHabitWithId.name}")

                onComplete()
            } catch (e: Exception) {
                Log.e(TAG, "LỖI KHI THÊM THÓI QUEN:", e)
            }
        }
    }

    fun toggleHabitCompletionForDate(habitId: String, date: LocalDate, isCompleted: Boolean) {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        viewModelScope.launch {
            try {
                val habitRef = db.collection("habits").document(habitId)
                val updateValue = if (isCompleted) FieldValue.arrayUnion(dateString) else FieldValue.arrayRemove(dateString)
                habitRef.update("completedDates", updateValue).await()
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi cập nhật trạng thái thói quen:", e)
            }
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            try {
                val habitRef = db.collection("habits").document(habitId)
                val habitToDelete = habitRef.get().await().toObject<Habit>()?.copy(id = habitId)

                if (habitToDelete != null) {
                    alarmScheduler.cancel(habitToDelete)
                    Log.d(TAG, "Đã gửi yêu cầu hủy báo thức cho thói quen: ${habitToDelete.name}")
                }
                habitRef.delete().await()
                Log.d(TAG, "Đã xóa thói quen ID: $habitId khỏi Firestore.")
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi xóa thói quen:", e)
            }
        }
    }

    // <-- THÊM MỚI: Thêm một Factory để có thể khởi tạo ViewModel với Application context -->
    class HabitViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HabitViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}