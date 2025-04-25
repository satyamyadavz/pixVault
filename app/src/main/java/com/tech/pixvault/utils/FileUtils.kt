package com.tech.pixvault.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object FileUtils {

    fun copyFileToInternalStorage(context: Context, uri: Uri, destinationFolder: File): Boolean {
        return try {
            val fileName = getFileName(context, uri)
            val inputStream = context.contentResolver.openInputStream(uri)
            val outFile = File(destinationFolder, fileName)

            inputStream.use { input ->
                outFile.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun openFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val mime = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(file.extension.lowercase()) ?: "*/*"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No app found to open this file.", Toast.LENGTH_SHORT).show()
        }
    }

    fun renameFile(context: Context, file: File, onRenamed: () -> Unit) {
        val editText = EditText(context).apply { setText(file.name) }

        AlertDialog.Builder(context)
            .setTitle("Rename File")
            .setView(editText)
            .setPositiveButton("Rename") { _, _ ->
                val newName = editText.text.toString()
                val newFile = File(file.parent, newName)
                if (file.renameTo(newFile)) {
                    Toast.makeText(context, "Renamed successfully", Toast.LENGTH_SHORT).show()
                    onRenamed()
                } else {
                    Toast.makeText(context, "Rename failed", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = "file_" + System.currentTimeMillis()
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}
