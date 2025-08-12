package com.example.to_do_list.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BootCompletedReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootCompletedReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val userId = Firebase.auth.currentUser?.uid
            if (userId == null) {
                Log.d(TAG, "Người dùng chưa đăng nhập, không khôi phục báo thức.")
                return
            }

            Log.d(TAG, "Thiết bị khởi động xong, đang khôi phục các báo thức...")

            // Chạy tác vụ trên một Coroutine để không chặn luồng chính
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = Firebase.firestore
                    val alarmScheduler = AlarmScheduler(context)

                    // Lấy tất cả thói quen của người dùng từ Firestore
                    val snapshots = db.collection("habits")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()

                    val habits = snapshots.toObjects<Habit>()
                    Log.d(TAG, "Tìm thấy ${habits.size} thói quen để khôi phục.")

                    // Lên lịch lại cho tất cả các thói quen
                    habits.forEach { habit ->
                        alarmScheduler.schedule(habit)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi khi khôi phục báo thức sau khi khởi động:", e)
                }
            }
        }
    }
}