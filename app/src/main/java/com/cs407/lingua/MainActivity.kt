package com.cs407.lingua

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import android.Manifest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission()
            } else {
                // If permission is already granted, schedule daily notifications
                //scheduleDailyNotification()
            }
        } else {
            // No need to request permission for older versions
            //scheduleDailyNotification()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_NOTIFICATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, schedule daily notifications
                scheduleDailyNotification()
            }
        }
    }

    private fun scheduleDailyNotification() {
        val dailyNotificationRequest = PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            15, TimeUnit.MINUTES // Periodic task, repeat every 15 minutes for demonstration
        )
            .setInitialDelay(1, TimeUnit.SECONDS)   // Delay start (would be set to 1 day)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(dailyNotificationRequest)
    }

    private fun requestNotificationPermission() {
        // Request notification permission
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION)
    }

    init { // for Utils class (requires context within companion object for file reading)
        instance = this
    }
    companion object {
        private const val REQUEST_CODE_NOTIFICATION = 1
        private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}