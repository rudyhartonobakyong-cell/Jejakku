package com.example.data.storage

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoStorageManager(private val context: Context) {

  private fun memoriesDir(): File {
    val dir = File(context.filesDir, "memories")
    if (!dir.exists()) {
      dir.mkdirs()
    }
    return dir
  }

  fun createPendingPhotoFile(dateKey: String): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Date())
    val safeDate = dateKey.replace(Regex("[^0-9-]"), "")
    return File(memoriesDir(), "kenangan_${safeDate}_$timestamp.jpg")
  }

  fun getContentUri(file: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

  fun deleteFileIfExists(filePath: String) {
    try {
      val file = File(filePath)
      if (file.exists()) {
        file.delete()
      }
    } catch (_: SecurityException) {
      // Ignore cleanup errors for unreachable paths
    }
  }
}
