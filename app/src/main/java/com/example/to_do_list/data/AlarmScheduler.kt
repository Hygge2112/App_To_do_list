package com.example.to_do_list.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build // Thêm import này
import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class AlarmScheduler(private val context: Context) {

    companion object {
        private const val TAG = "AlarmScheduler"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Lên lịch cho tất cả các ngày nhắc nhở của một thói quen.
     */
    fun schedule(habit: Habit) {
        if (habit.reminderTime == null || habit.repetitionDates.isEmpty()) {
            Log.d(TAG, "Bỏ qua lên lịch cho thói quen '${habit.name}' vì thiếu thông tin.")
            return
        }

        val reminderTime = LocalTime.parse(habit.reminderTime, DateTimeFormatter.ofPattern("HH:mm"))

        habit.repetitionDates.forEach { dateString ->
            val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            val alarmTime = LocalDateTime.of(date, reminderTime)
            val alarmTimeInMillis = alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val currentTime = System.currentTimeMillis()

            if (alarmTimeInMillis > currentTime) {
                val requestCode = (habit.id + dateString).hashCode()
                val intent = Intent(context, HabitBroadcastReceiver::class.java).apply {
                    putExtra("HABIT_ID_EXTRA", habit.id)
                    putExtra("HABIT_NAME_EXTRA", habit.name)
                    putExtra("HABIT_ICON_EXTRA", habit.iconName)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // SỬA LỖI: Thêm khối kiểm tra phiên bản Android
                val canSchedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.canScheduleExactAlarms()
                } else {
                    // Trên các phiên bản cũ hơn, quyền được cấp tại thời điểm cài đặt
                    true
                }

                if (canSchedule) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTimeInMillis,
                        pendingIntent
                    )
                    val minutesUntilAlarm = TimeUnit.MILLISECONDS.toMinutes(alarmTimeInMillis - currentTime)
                    Log.d(TAG, "Đã đặt báo thức cho '${habit.name}' vào $alarmTime (còn $minutesUntilAlarm phút)")
                } else {
                    Log.w(TAG, "Không thể đặt báo thức chính xác. Vui lòng kiểm tra quyền SCHEDULE_EXACT_ALARM.")
                }
            }
        }
    }

    /**
     * Hủy tất cả các báo thức đã lên lịch cho một thói quen.
     */
    fun cancel(habit: Habit) {
        if (habit.reminderTime == null) return

        habit.repetitionDates.forEach { dateString ->
            val requestCode = (habit.id + dateString).hashCode()
            val intent = Intent(context, HabitBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                Log.d(TAG, "Đã hủy báo thức cho thói quen '${habit.name}' vào ngày $dateString")
            }
        }
    }
}