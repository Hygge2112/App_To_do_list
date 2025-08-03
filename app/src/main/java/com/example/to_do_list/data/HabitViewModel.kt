package com.example.to_do_list.data

import android.os.Build
import android.util.Log // << THÊM IMPORT
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

private const val TAG = "HabitApp" // << THÊM HẰNG SỐ ĐỂ LỌC LOG

@RequiresApi(Build.VERSION_CODES.O)
class HabitViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    init {
        // Tải danh sách thói quen cho ngày hôm nay khi ViewModel được tạo
        loadHabitsForDate(LocalDate.now())
    }

    // Tải các thói quen cho một ngày cụ thể
    fun loadHabitsForDate(date: LocalDate) {
        val userId = auth.currentUser?.uid ?: return
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE) // "yyyy-MM-dd"

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
            }
    }

    // --- HÀM NÀY ĐÃ ĐƯỢC THAY THẾ HOÀN TOÀN ---
    // Thêm một thói quen mới vào Firestore
    fun addHabit(habit: Habit, onComplete: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "LỖI: Người dùng chưa đăng nhập, không thể thêm thói quen.")
            return
        }

        viewModelScope.launch {
            try {
                db.collection("habits")
                    .add(habit.copy(userId = currentUser.uid))
                    .await()

                Log.d(TAG, "THÀNH CÔNG: Đã thêm thói quen '${habit.name}' vào Firestore.")
                onComplete() // Gọi callback khi thành công
            } catch (e: Exception) {
                // In ra lỗi chi tiết để gỡ lỗi
                Log.e(TAG, "LỖI KHI THÊM THÓI QUEN: ", e)
            }
        }
    }

    // Cập nhật trạng thái hoàn thành của thói quen
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