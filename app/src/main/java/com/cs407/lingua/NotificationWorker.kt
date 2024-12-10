package com.cs407.lingua

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters


class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.lingua_logo)

    override fun doWork(): Result {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "study_notification_channel",
                "Study Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(applicationContext, "study_notification_channel")
            .setSmallIcon(R.drawable.notification_logo)
            .setLargeIcon(largeIcon)
            .setContentTitle("Study Reminder")
            .setContentText("Reminder to study Linguistics!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)

        return Result.success()
    }
}