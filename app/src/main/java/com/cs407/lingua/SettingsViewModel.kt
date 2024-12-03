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
    val notificationAllowed = MutableLiveData<Boolean>()
    val toastAllowed = MutableLiveData<Boolean>()
    val easy = MutableLiveData<Boolean>()
    val medium = MutableLiveData<Boolean>()
    val hard = MutableLiveData<Boolean>()
    val simple = MutableLiveData<Boolean>()
    val complex = MutableLiveData<Boolean>()
    val compound = MutableLiveData<Boolean>()
    init {
        primaryColor.value = sharedPreferences.getInt("primaryColor", Color.parseColor("#673AB7"))
        secondaryColor.value = sharedPreferences.getInt("secondaryColor", Color.WHITE)
        notificationAllowed.value = sharedPreferences.getBoolean("notificationAllowed", false)
        toastAllowed.value = sharedPreferences.getBoolean("toastAllowed", true)
        easy.value = sharedPreferences.getBoolean("easy", true)
        medium.value = sharedPreferences.getBoolean("medium", true)
        hard.value = sharedPreferences.getBoolean("hard", true)
        simple.value = sharedPreferences.getBoolean("simple", true)
        complex.value = sharedPreferences.getBoolean("complex", true)
        compound.value = sharedPreferences.getBoolean("compound", true)
    }

    fun savePrimaryColor(color: Int){
        sharedPreferences.edit().putInt("primaryColor", color).apply()
        primaryColor.value = color
    }
    fun saveSecondaryColor(color: Int){
        sharedPreferences.edit().putInt("secondaryColor", color).apply()
        secondaryColor.value = color
    }


}











