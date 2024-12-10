package com.cs407.lingua

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.compose.ui.window.application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class SettingsViewModel(application: Application): AndroidViewModel(application){
    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)

    val primaryColor = MutableLiveData<Int>()
    val secondaryColor = MutableLiveData<Int>()
    val notificationSelection = MutableLiveData<Int>()
    val vibAllowed = MutableLiveData<Boolean>()
    val simple = MutableLiveData<Boolean>()
    val dataLoader = DataLoader
    init {
        primaryColor.value = sharedPreferences.getInt("primaryColor", Color.parseColor("#673AB7"))
        secondaryColor.value = sharedPreferences.getInt("secondaryColor", Color.WHITE)
        notificationSelection.value = sharedPreferences.getInt("notificationSelection", 1)  //Value is number of days frequency
        vibAllowed.value = sharedPreferences.getBoolean("vibAllowed", true)
    }

    fun savePrimaryColor(color: Int){
        sharedPreferences.edit().putInt("primaryColor", color).apply()
        primaryColor.value = color
    }
    fun saveSecondaryColor(color: Int){
        sharedPreferences.edit().putInt("secondaryColor", color).apply()
        secondaryColor.value = color
    }

    fun saveVibSelection(boolean: Boolean) {
        sharedPreferences.edit().putBoolean("vibAllowed", boolean).apply()
        vibAllowed.value = boolean
    }

    fun saveNotificationSelection(selection: Int) {
        sharedPreferences.edit().putInt("notificationSelection", selection).apply()
        notificationSelection.value = selection
    }

}











