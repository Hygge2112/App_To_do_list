package com.example.to_do_list.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "HabitApp"

@RequiresApi(Build.VERSION_CODES.O)
class HabitViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    init {
        // Chỉ tải thói quen nếu người dùng đã đăng nhập
        if (auth.currentUser != null) {
            loadHabitsForDate(LocalDate.now())
        }
    }

    fun loadHabitsForDate(date: LocalDate) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.d(TAG, "loadHabitsForDate: Người dùng chưa đăng nhập, không tải dữ liệu.")
            return
        }

        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

        db.collection("habits")
            .whereEqualTo("userId", userId)
            .whereArrayContains("repetitionDates", dateString)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e(TAG, "Lỗi khi tải thói quen: ", error)
                    _habits.value = emptyList()
                    return@addSnapshotListener
                }
                if (snapshots == null) {
                    _habits.value = emptyList()
                    return@addSnapshotListener
                }

                val habitList = snapshots.documents.mapNotNull { doc ->
                    doc.toObject<Habit>()?.copy(id = doc.id)
                }
                _habits.value = habitList
                Log.d(TAG, "Đã tải thành công ${habitList.size} thói quen cho ngày $dateString")
            }
    }

    fun addHabit(habit: Habit, onComplete: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "LỖI: Người dùng chưa đăng nhập, không thể thêm thói quen.")
            return
        }

        // --- LOG MỚI ĐỂ GỠ LỖI ---
        Log.d(TAG, "Chuẩn bị lưu thói quen: $habit")
        Log.d(TAG, "UserID hiện tại: ${currentUser.uid}")
        // -------------------------

        viewModelScope.launch {
            try {
                db.collection("habits")
                    .add(habit.copy(userId = currentUser.uid))
                    .await()

                Log.d(TAG, "THÀNH CÔNG: Đã thêm thói quen '${habit.name}' vào Firestore.")
                onComplete()
            } catch (e: Exception) {
                // In ra lỗi chi tiết để gỡ lỗi
                Log.e(TAG, "LỖI KHI THÊM THÓI QUEN: Thao tác Firestore thất bại.", e)
            }
        }
    }

    fun toggleHabitCompleted(habitId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                db.collection("habits").document(habitId)
                    .update("completed", isCompleted)
                    .await()
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi cập nhật trạng thái thói quen: ", e)
            }
        }
    }
}
