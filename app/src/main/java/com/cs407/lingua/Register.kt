package com.cs407.lingua
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

        val backButton = findViewById<FloatingActionButton>(R.id.backButton)

        backButton.setOnClickListener{
            finish()
        }

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        regButton = findViewById(R.id.registerButton)

        regButton.setOnClickListener{

            val emailInput = email.text.toString().trim()
            val passwordInput = password.text.toString().trim()

            // checking if email
            if (TextUtils.isEmpty(emailInput)) {
                Toast.makeText(this,"Please Type in Email", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(passwordInput)) {
                Toast.makeText(this,"Please Type in Password", Toast.LENGTH_SHORT).show()
            }

            val database = Firebase.database.getReference("users")

            auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid
                        val newUser = database.child(uid!!)
                        val userInfo = mapOf("Authentication" to true, "Favorites" to emptyList<String>())

                        newUser.setValue(userInfo).addOnCompleteListener{
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Error with Account", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


        }

    }
}