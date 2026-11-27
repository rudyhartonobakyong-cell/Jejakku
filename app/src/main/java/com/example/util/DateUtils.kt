package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
  private val indonesianLocale = Locale("id", "ID")
  private const val ISO_PATTERN = "yyyy-MM-dd"

  fun todayKey(): String {
    val formatter = SimpleDateFormat(ISO_PATTERN, Locale.US)
    return formatter.format(Date())
  }

  fun formatFullDate(dateKey: String): String {
    val parsed = parseIsoDate(dateKey) ?: return dateKey
    val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", indonesianLocale)
    return formatter.format(parsed).replaceFirstChar { it.titlecase(indonesianLocale) }
  }

  fun formatShortDate(dateKey: String): String {
    val parsed = parseIsoDate(dateKey) ?: return dateKey
    val formatter = SimpleDateFormat("d MMM yyyy", indonesianLocale)
    return formatter.format(parsed)
  }

  fun formatTime(timestampMillis: Long): String {
    val formatter = SimpleDateFormat("HH:mm", indonesianLocale)
    return formatter.format(Date(timestampMillis))
  }

  private fun parseIsoDate(dateKey: String): Date? =
    try {
      SimpleDateFormat(ISO_PATTERN, Locale.US).apply { isLenient = false }.parse(dateKey)
    } catch (_: Exception) {
      null
    }
}
