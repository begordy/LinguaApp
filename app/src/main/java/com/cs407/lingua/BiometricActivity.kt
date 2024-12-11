package com.cs407.lingua

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BiometricActivity : AppCompatActivity() {
    private lateinit var biometricPromptManager: BiometricPromptManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(this@BiometricActivity, "Please Authenticate", Toast.LENGTH_SHORT).show()
            }
        })

        biometricPromptManager = BiometricPromptManager(this)

        val bioButton = findViewById<Button>(R.id.bioButton)
        bioButton.setOnClickListener {
            biometricPromptManager.showBiometricPrompt(
                title = "Biometric Authentication",
                description = "Please authenticate to proceed."
            )
        }

        // Collect prompt results from the flow
        lifecycleScope.launch {
            biometricPromptManager.promptResults.collect { result ->
                when (result) {
                    is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                        finish()
                    }
                    is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                        // Handle error (optional)
                        Toast.makeText(this@BiometricActivity, "Authentication error. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                    is BiometricPromptManager.BiometricResult.AuthenticationCanceled -> {
                        // Handle cancelation (optional)
                        Toast.makeText(this@BiometricActivity, "Authentication canceled. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}