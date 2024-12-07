package com.cs407.lingua

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var notificationPermissionHelper: NotificationPermissionHelper
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        notificationPermissionHelper = NotificationPermissionHelper { isGranted ->
            if (isGranted) {
                //do nothing
            } else {
                //do nothing
            }
        }

        notificationPermissionLauncher =
            notificationPermissionHelper.registerLauncher(this)

        if (!notificationPermissionHelper.isNotificationPermissionGranted(this)) {
            notificationPermissionHelper.requestNotificationPermission(notificationPermissionLauncher)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}