package com.example.to_do_list.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
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

private const val TAG = "HabitViewModel"

@RequiresApi(Build.VERSION_CODES.O)
class HabitViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

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
                    _uiState.value = HabitUiState.Error(error.message ?: "Lỗi không xác định")
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
                db.collection("habits").add(habit.copy(userId = currentUser.uid)).await()
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
                db.collection("habits").document(habitId).delete().await()
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi xóa thói quen:", e)
            }
        }
    }
}