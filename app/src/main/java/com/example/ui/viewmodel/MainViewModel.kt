package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MemoryPhotoEntity
import com.example.data.local.VisitDayWithPhotos
import com.example.data.preferences.AppPreferencesRepository
import com.example.data.preferences.UserProfile
import com.example.data.repository.VisitMemoryRepository
import com.example.data.storage.PhotoStorageManager
import com.example.ui.theme.AppThemeId
import com.example.util.DateUtils
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
  application: Application,
  private val preferencesRepository: AppPreferencesRepository,
  private val visitRepository: VisitMemoryRepository,
) : AndroidViewModel(application) {

  val todayDateKey: String = DateUtils.todayKey()

  val selectedTheme: StateFlow<AppThemeId> =
    preferencesRepository.selectedTheme.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = AppThemeId.SAMUDRA,
    )

  val userProfile: StateFlow<UserProfile> =
    preferencesRepository.userProfile.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = UserProfile(),
    )

  val allMemories: StateFlow<List<VisitDayWithPhotos>> =
    visitRepository.allMemories.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = emptyList(),
    )

  val todayMemory: StateFlow<VisitDayWithPhotos?> =
    visitRepository.observeMemoryByDate(todayDateKey).stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = null,
    )

  fun observeMemoryByDate(dateKey: String): Flow<VisitDayWithPhotos?> =
    visitRepository.observeMemoryByDate(dateKey)

  fun selectTheme(themeId: AppThemeId) {
    viewModelScope.launch { preferencesRepository.setTheme(themeId) }
  }

  fun updateUserProfile(displayName: String, bio: String) {
    viewModelScope.launch { preferencesRepository.updateUserProfile(displayName, bio) }
  }

  fun saveVisitDetails(dateKey: String, locationTitle: String, description: String) {
    viewModelScope.launch {
      visitRepository.saveVisitDetails(
        dateKey = dateKey,
        locationTitle = locationTitle,
        description = description,
      )
    }
  }

  fun preparePhotoCapture(dateKey: String): Pair<File, Uri> {
    val file = visitRepository.preparePhotoFile(dateKey)
    val uri = visitRepository.getShareableUri(file)
    return file to uri
  }

  fun onPhotoCaptured(
    dateKey: String,
    locationTitle: String,
    description: String,
    filePath: String,
  ) {
    viewModelScope.launch {
      visitRepository.saveCapturedPhoto(
        dateKey = dateKey,
        locationTitle = locationTitle,
        description = description,
        filePath = filePath,
      )
    }
  }

  fun onPhotoCaptureCancelled(filePath: String) {
    viewModelScope.launch { visitRepository.cleanupUnusedFile(filePath) }
  }

  fun deletePhoto(photo: MemoryPhotoEntity) {
    viewModelScope.launch { visitRepository.deletePhoto(photo) }
  }

  class Factory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val db = AppDatabase.getInstance(application)
      val prefs = AppPreferencesRepository(application)
      val storage = PhotoStorageManager(application)
      val repo = VisitMemoryRepository(db.visitDao(), storage)
      return MainViewModel(application, prefs, repo) as T
    }
  }
}
