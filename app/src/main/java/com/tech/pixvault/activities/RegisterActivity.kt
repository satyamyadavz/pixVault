package com.tech.pixvault.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tech.pixvault.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordStrengthText: TextView
    private lateinit var registerButton: Button
    private lateinit var goToLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        passwordStrengthText = findViewById(R.id.passwordStrengthText)
        registerButton = findViewById(R.id.registerButton)
        goToLoginButton = findViewById(R.id.goToLoginButton)

        passwordEditText.addTextChangedListener(passwordWatcher)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid
                    val userMap = mapOf("name" to name, "email" to email)
                    uid?.let {
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(uid)
                            .setValue(userMap)
                    }
                    Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        goToLoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private val passwordWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val strength = getPasswordStrength(s.toString())
            passwordStrengthText.text = "Password Strength: $strength"
            passwordStrengthText.setTextColor(getStrengthColor(strength))
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun getPasswordStrength(password: String): String {
        var strength = 0
        if (password.length >= 6) strength++
        if (password.matches(Regex(".*[A-Z].*"))) strength++
        if (password.matches(Regex(".*[a-z].*"))) strength++
        if (password.matches(Regex(".*[0-9].*"))) strength++
        if (password.matches(Regex(".*[!@#\$%^&*()_+=<>?{}\\[\\]~-].*"))) strength++

        return when (strength) {
            0, 1 -> "Very Weak"
            2 -> "Weak"
            3 -> "Moderate"
            4 -> "Strong"
            else -> "Very Strong"
        }
    }

    private fun getStrengthColor(strength: String): Int {
        return when (strength) {
            "Very Weak" -> resources.getColor(R.color.red, null)
            "Weak" -> resources.getColor(R.color.orange, null)
            "Moderate" -> resources.getColor(R.color.yellow, null)
            "Strong" -> resources.getColor(R.color.teal_700, null)
            else -> resources.getColor(R.color.green, null)
        }
    }
}
