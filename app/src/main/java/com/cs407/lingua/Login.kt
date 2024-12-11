package com.cs407.lingua

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import android.provider.Settings
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.newSingleThreadContext

class Login : AppCompatActivity() {

    var authenticated = false
    private lateinit var googleAuth: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 20

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        if (!authenticated) {
            authenticated = true
            phoneAuth()
        }

        auth = FirebaseAuth.getInstance()

        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

            return
        }


        val login = findViewById<Button>(R.id.loginButton)

        login.setOnClickListener{
            val email = findViewById<TextInputEditText>(R.id.usernameInput).text.toString().trim()
            val password = findViewById<TextInputEditText>(R.id.passwdInput).text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                //TODO: remove this later cuz this is just so I can bypass the login page and actually get in
                if(email.compareTo("admin") == 0 && password.compareTo("admin") == 0){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Please use a valid username or password",
                                Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }



        googleAuth = findViewById(R.id.googleButton)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleAuth.setOnClickListener {
            googleSignIn()
        }


        val registerButton = findViewById<Button>(R.id.register)

        registerButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    private fun googleSignIn() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuth(account.idToken)
            } catch (e: ApiException) {
                e.printStackTrace()
                Toast.makeText(this, "Sign-In Failed: ${e.statusCode} - ${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuth(idToken: String?) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val exception = task.exception
                    Toast.makeText(this, "Error with logging in: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun phoneAuth() {
        val intent = Intent(this, BiometricActivity::class.java)
        startActivity(intent)

//        val biometricManager = BiometricManager.from(this)
//
//        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
//            BiometricManager.BIOMETRIC_SUCCESS -> {
//                // Delay prompt slightly to ensure activity transition completes
//                promptManager.showBiometricPrompt(
//                    title = "Biometric Authentication",
//                    description = "Authenticate using biometrics or device credentials",
//                )
//
//            }
//            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
//                Toast.makeText(this, "No biometric hardware available", Toast.LENGTH_SHORT).show()
//            }
//            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
//                Toast.makeText(this, "Biometric hardware is currently unavailable", Toast.LENGTH_SHORT).show()
//            }
//            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11 or higher
//                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
//                        putExtra(
//                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
//                        )
//                    }
//                    startActivity(enrollIntent)
//                } else {
//                    Toast.makeText(this, "No biometric credentials enrolled", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }
}
