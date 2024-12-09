package com.cs407.lingua

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var email: TextInputEditText
    lateinit var password: TextInputEditText
    lateinit var regButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_page)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        regButton = findViewById(R.id.registerButton)

        regButton.setOnClickListener{

            val emailInput = email.text.toString()
            val passwordInput = password.text.toString()

            // checking if email
            if (TextUtils.isEmpty(emailInput)) {
                Toast.makeText(this,"Please Type in Email", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(passwordInput)) {
                Toast.makeText(this,"Please Type in Password", Toast.LENGTH_SHORT).show()
            }

            auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }


        }

    }
}