package com.tech.pixvault.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.tech.pixvault.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var darkModeSwitch: MaterialSwitch
    private lateinit var shareButton: MaterialButton
    private lateinit var privacyPolicyText: MaterialTextView
    private lateinit var contactText: MaterialTextView
    private lateinit var logoutButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: MaterialToolbar = findViewById(R.id.settingsTopAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        darkModeSwitch = findViewById(R.id.switchDarkMode)
        shareButton = findViewById(R.id.btnShareApp)
        privacyPolicyText = findViewById(R.id.txtPrivacyPolicy)
        contactText = findViewById(R.id.txtContactUs)
        logoutButton = findViewById(R.id.btnLogout)

        // Ensure the switch state matches the theme
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        darkModeSwitch.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES ||
                (currentNightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && resources.configuration.uiMode and 0x30 == 0x20)

        darkModeSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate()
        }

        // Share App
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "PixVault")
                putExtra(Intent.EXTRA_TEXT, "Check out PixVault app on Play Store!")
            }
            startActivity(Intent.createChooser(shareIntent, "Share PixVault via"))
        }

        // Privacy Policy
        privacyPolicyText.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }

        // Contact Us
        contactText.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:itz.satyamyadav@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "PixVault Support")
            }
            startActivity(Intent.createChooser(emailIntent, "Contact via Email"))
        }

        // Logout
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Only if you're using Firebase

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()

            // Redirect to LoginActivity and clear back stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
