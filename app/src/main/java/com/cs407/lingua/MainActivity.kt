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
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

//ZoomLayout (used in SyntaxAdvancedQuestion.kt) from https://github.com/natario1/ZoomLayout
//Material Components Android (used in QuizResult.kt and QuestionResult.kt) from https://github.com/material-components/material-components-android
// Dataloader sources...
//   Phonetics raw data from WikiPron: https://github.com/CUNY-CL/wikipron/blob/master/data/scrape/tsv/eng_latn_us_broad_filtered.tsv
//   1000 most common English words from: https://gist.github.com/SivilTaram/9597125e4134cc81648027b1c6f6395f
//   Syntax raw data from GUM Treebank: https://github.com/amir-zeldes/gum/tree/master/const

class MainActivity : AppCompatActivity() {

    private var windowInsetsController: WindowInsetsControllerCompat? = null
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission()
            } else {
                scheduleDailyNotification()
            }
        } else {
            scheduleDailyNotification()
        }
        // Hide both the status bar and the navigation bar.
        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onResume() {
        super.onResume()
        // Hide both the status bar and the navigation bar.
        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_NOTIFICATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, schedule daily notifications
                scheduleDailyNotification()
            } else {
                cancelDailyNotification()
            }
        }
    }

    private fun cancelDailyNotification() {
        WorkManager.getInstance(applicationContext).cancelUniqueWork("studyReminder")
    }

    private fun scheduleDailyNotification() {
        val dailyNotificationRequest = settingsViewModel.notificationSelection.value?.let {
            PeriodicWorkRequest.Builder(
                NotificationWorker::class.java,
                it.toLong(), TimeUnit.DAYS // Periodic task, repeat each day
            )
                .setInitialDelay(1, TimeUnit.SECONDS)   // Delay start (would be set to 1 day)
                .build()
        }
        if (dailyNotificationRequest != null) {
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("studyReminder", ExistingPeriodicWorkPolicy.KEEP,dailyNotificationRequest)
        }
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