package com.tech.pixvault.models


import java.io.File

data class VaultFile(
    val file: File,
    val name: String = file.name,
    val path: String = file.absolutePath,
    val size: Long = file.length(),
    val lastModified: Long = file.lastModified()
)
