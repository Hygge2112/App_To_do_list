package com.example.to_do_list.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class HabitBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "HabitBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Đã nhận được báo thức từ AlarmManager")

        val habitId = intent.getStringExtra("HABIT_ID_EXTRA") ?: return
        val habitName = intent.getStringExtra("HABIT_NAME_EXTRA") ?: "Đến giờ thực hiện thói quen!"
        val iconName = intent.getStringExtra("HABIT_ICON_EXTRA")

        // Sử dụng NotificationHelper để hiển thị thông báo
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(
            id = habitId.hashCode(), // Sử dụng hashCode của ID thói quen để đảm bảo mỗi thông báo là duy nhất
            title = "Đến giờ rồi!",
            message = habitName,
            iconName = iconName
        )
    }
}