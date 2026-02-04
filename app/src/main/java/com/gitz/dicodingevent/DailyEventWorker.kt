package com.gitz.dicodingevent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gitz.dicodingevent.data.remote.retrofit.ApiConfig
import com.gitz.dicodingevent.di.Injection
import com.gitz.dicodingevent.ui.MainActivity
import kotlinx.coroutines.flow.first

class DailyEventWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "daily_event_channel"
        private const val CHANNEL_NAME = "Daily Event Reminder"
        private const val NOTIFICATION_ID = 1
    }

    private fun showNotification(id: Int, title: String, description: String?) {
        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("EVENT_ID", id)
        }

        val pendingIntent = android.app.TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar_month_24)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(applicationContext.getColor(R.color.gold_primary))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager?.createNotificationChannel(channel)
        }

        notificationManager?.notify(NOTIFICATION_ID, notification.build())
    }

    override suspend fun doWork(): Result {
        val pref = Injection.provideSettingPreferences(applicationContext)
        val isReminderActive = pref.getReminderSetting().first()

        if (!isReminderActive) return Result.success()

        return try {
            val apiService = ApiConfig.getApiService(applicationContext)
            val response = apiService.getEvents(active = -1, limit = 1)
            val event = response.listEvents.firstOrNull()

            if (event != null) {
                showNotification(event.id, event.name, "Mulai pada: ${event.beginTime}")
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}