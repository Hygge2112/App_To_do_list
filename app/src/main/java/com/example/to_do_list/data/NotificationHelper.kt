package com.example.to_do_list.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.to_do_list.MainActivity
import com.example.to_do_list.R // Đảm bảo bạn đã có icon trong drawable
import com.example.to_do_list.ui.theme.create_habit.HabitIconProvider

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "habit_reminder_channel"
        const val CHANNEL_NAME = "Nhắc nhở Thói quen"
        const val CHANNEL_DESCRIPTION = "Thông báo cho các thói quen đã được lên lịch"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(id: Int, title: String, message: String, iconName: String?) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Lưu ý: Notification không thể hiển thị trực tiếp VectorDrawable.
        // Chúng ta sẽ dùng icon mặc định của ứng dụng.
        // Để hiển thị icon tùy chỉnh, cần chuyển đổi Vector thành Bitmap.
        // Đây là một kỹ thuật nâng cao hơn, hiện tại chúng ta sẽ dùng icon mặc định.
        val smallIconRes = R.drawable.ic_launcher_foreground // Thay bằng icon của bạn

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Sử dụng âm thanh, rung mặc định

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
    }
}