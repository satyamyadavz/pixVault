package com.tech.pixvault.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tech.pixvault.R
import com.tech.pixvault.adapters.VaultAdapter
import com.tech.pixvault.utils.FileUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.MaterialToolbar
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var vaultRecyclerView: RecyclerView
    private lateinit var vaultAdapter: VaultAdapter
    private lateinit var vaultFolder: File

    companion object {
        const val IMPORT_FILE_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "PixVault"

        vaultRecyclerView = findViewById(R.id.vaultRecyclerView)
        val importButton: FloatingActionButton = findViewById(R.id.importButton)

        vaultFolder = File(filesDir, "vault_files")
        if (!vaultFolder.exists()) vaultFolder.mkdirs()

        val noMediaFile = File(vaultFolder, ".nomedia")
        if (!noMediaFile.exists()) noMediaFile.createNewFile()

        loadVaultFiles()

        importButton.setOnClickListener {
            pickFileFromDevice()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun pickFileFromDevice() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, IMPORT_FILE_CODE)
    }

    private fun loadVaultFiles() {
        val files = vaultFolder.listFiles()?.toList() ?: emptyList()
        vaultAdapter = VaultAdapter(files, this) { selectedFile ->
            showOptionsDialog(selectedFile)
        }
        vaultRecyclerView.layoutManager = GridLayoutManager(this, 3)
        vaultRecyclerView.adapter = vaultAdapter
    }

    private fun showOptionsDialog(file: File) {
        val options = arrayOf("Open", "Rename", "Delete")
        AlertDialog.Builder(this)
            .setTitle(file.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> FileUtils.openFile(this, file)
                    1 -> FileUtils.renameFile(this, file) { loadVaultFiles() }
                    2 -> {
                        if (file.delete()) {
                            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                            loadVaultFiles()
                        } else {
                            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMPORT_FILE_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let {
                val copied = FileUtils.copyFileToInternalStorage(this, it, vaultFolder)
                if (copied) {
                    Toast.makeText(this, "File added to vault!", Toast.LENGTH_SHORT).show()
                    loadVaultFiles()
                } else {
                    Toast.makeText(this, "Failed to import file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
