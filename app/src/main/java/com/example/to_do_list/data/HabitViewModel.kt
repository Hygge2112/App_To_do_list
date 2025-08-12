package com.example.to_do_list.data

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
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

private const val TAG = "HabitViewModel_DEBUG" // Đổi tag để dễ lọc log

@RequiresApi(Build.VERSION_CODES.O)
// SỬA ĐỔI 1: Chuyển sang AndroidViewModel để lấy context cho AlarmScheduler
class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    // SỬA ĐỔI 2: Khởi tạo AlarmScheduler
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
                    // Sử dụng Failure cho nhất quán
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

    // SỬA ĐỔI 3: Cập nhật hàm addHabit để đặt báo thức và thêm log
    fun addHabit(habit: Habit, onComplete: () -> Unit) {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                // Thêm logs để kiểm tra dữ liệu
                Log.d(TAG, "--- Bắt đầu thêm thói quen mới ---")
                Log.d(TAG, "Dữ liệu Habit nhận được: $habit")
                Log.d(TAG, "Thời gian nhắc nhở (reminderTime): ${habit.reminderTime}")
                Log.d(TAG, "Ngày lặp lại (repetitionDates): ${habit.repetitionDates}")
                Log.d(TAG, "------------------------------------")

                val documentRef = db.collection("habits").add(habit.copy(userId = currentUser.uid)).await()
                // Tạo một đối tượng mới với ID từ Firestore để gửi đến scheduler
                val newHabitWithId = habit.copy(id = documentRef.id)

                // Gọi đến AlarmScheduler để đặt báo thức
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

    // SỬA ĐỔI 4: Cập nhật hàm deleteHabit để hủy báo thức trước khi xóa
    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            try {
                val habitRef = db.collection("habits").document(habitId)

                // Lấy thông tin thói quen trước khi xóa để hủy báo thức
                val habitToDelete = habitRef.get().await().toObject<Habit>()?.copy(id = habitId)

                if (habitToDelete != null) {
                    alarmScheduler.cancel(habitToDelete)
                    Log.d(TAG, "Đã gửi yêu cầu hủy báo thức cho thói quen: ${habitToDelete.name}")
                }

                // Xóa thói quen khỏi Firestore
                habitRef.delete().await()
                Log.d(TAG, "Đã xóa thói quen ID: $habitId khỏi Firestore.")

            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi xóa thói quen:", e)
            }
        }
    }
}