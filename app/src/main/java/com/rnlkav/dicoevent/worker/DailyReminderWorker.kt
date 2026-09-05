package com.rnlkav.dicoevent.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rnlkav.dicoevent.R
import com.rnlkav.dicoevent.data.retrofit.ApiConfig
import java.text.SimpleDateFormat
import java.util.Locale

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val response = ApiConfig.getApiService().getEvents(active = -1, limit = 1)
            val event = response.listEvents.firstOrNull()
            if (event != null) {
                val formattedDate = formatEventTime(event.beginTime)
                showNotification(event.name, formattedDate)
            }
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private fun formatEventTime(time: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy HH:mm 'WIB'", Locale.forLanguageTag("id-ID"))
            val date = inputFormat.parse(time)
            date?.let { outputFormat.format(it) } ?: time
        } catch (_: Exception) {
            time
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_upcoming)
            .setContentTitle("Upcoming Event: $title")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    companion object {
        const val WORK_NAME = "daily_reminder_work"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding channel"
    }
}
