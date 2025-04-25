package com.tech.pixvault.activities


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BiometricHelper.authenticate(this) { success ->
            if (success) {
                // After fingerprint success → check if logged in
                if (FirebaseAuth.getInstance().currentUser != null) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            } else {
                Toast.makeText(this, "Fingerprint failed", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
