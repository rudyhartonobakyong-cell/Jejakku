package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.AppThemeId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jejakku_prefs")

data class UserProfile(
  val displayName: String = "Penjelajah",
  val bio: String = "Menyimpan jejak perjalanan dan foto kenangan harian.",
)

class AppPreferencesRepository(private val context: Context) {

  private object Keys {
    val THEME_ID = stringPreferencesKey("selected_theme_id")
    val DISPLAY_NAME = stringPreferencesKey("user_display_name")
    val BIO = stringPreferencesKey("user_bio")
  }

  val selectedTheme: Flow<AppThemeId> =
    context.dataStore.data.map { prefs -> AppThemeId.fromKey(prefs[Keys.THEME_ID]) }

  val userProfile: Flow<UserProfile> =
    context.dataStore.data.map { prefs ->
      UserProfile(
        displayName = prefs[Keys.DISPLAY_NAME]?.takeIf { it.isNotBlank() } ?: "Penjelajah",
        bio =
          prefs[Keys.BIO]?.takeIf { it.isNotBlank() }
            ?: "Menyimpan jejak perjalanan dan foto kenangan harian.",
      )
    }

  suspend fun setTheme(themeId: AppThemeId) {
    context.dataStore.edit { prefs -> prefs[Keys.THEME_ID] = themeId.storageKey }
  }

  suspend fun updateUserProfile(displayName: String, bio: String) {
    context.dataStore.edit { prefs ->
      prefs[Keys.DISPLAY_NAME] = displayName.trim().ifBlank { "Penjelajah" }
      prefs[Keys.BIO] = bio.trim().ifBlank { "Menyimpan jejak perjalanan dan foto kenangan harian." }
    }
  }
}
