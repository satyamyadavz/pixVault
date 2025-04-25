package com.tech.pixvault.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class FingerprintActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("PixVaultPrefs", MODE_PRIVATE)
        val isAuthenticated = prefs.getBoolean("isAuthenticated", false)

        if (isAuthenticated) {
            // Already logged in once – skip fingerprint
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        showBiometricPrompt()
    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) !=
            BiometricManager.BIOMETRIC_SUCCESS
        ) {
            Toast.makeText(this, "Biometric not available", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("PixVault Login")
            .setSubtitle("Authenticate to access your vault")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    startActivity(Intent(this@FingerprintActivity, LoginActivity::class.java))
                    finish()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@FingerprintActivity, "Authentication cancelled", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }
}
