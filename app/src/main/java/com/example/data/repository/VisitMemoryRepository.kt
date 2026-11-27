package com.example.data.repository

import com.example.data.local.MemoryPhotoEntity
import com.example.data.local.VisitDao
import com.example.data.local.VisitDayEntity
import com.example.data.local.VisitDayWithPhotos
import com.example.data.storage.PhotoStorageManager
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class VisitMemoryRepository(
  private val visitDao: VisitDao,
  private val photoStorageManager: PhotoStorageManager,
) {

  val allMemories: Flow<List<VisitDayWithPhotos>> =
    visitDao.observeAllVisitsWithPhotos().map { list ->
      list.map { item ->
        item.copy(photos = item.photos.sortedWith(compareBy({ it.createdAt }, { it.id })))
      }
    }

  fun observeMemoryByDate(dateKey: String): Flow<VisitDayWithPhotos?> =
    visitDao.observeVisitWithPhotos(dateKey).map { item ->
      item?.copy(photos = item.photos.sortedWith(compareBy({ it.createdAt }, { it.id })))
    }

  suspend fun saveVisitDetails(dateKey: String, locationTitle: String, description: String) {
    val cleanLocation = locationTitle.trim().ifBlank { "Kunjungan Hari Ini" }
    val cleanDescription = description.trim()
    visitDao.upsertVisit(
      VisitDayEntity(
        dateKey = dateKey,
        locationTitle = cleanLocation,
        description = cleanDescription,
        updatedAt = System.currentTimeMillis(),
      )
    )
    visitDao.updatePhotosLocationForDate(dateKey, cleanLocation)
  }

  suspend fun saveCapturedPhoto(
    dateKey: String,
    locationTitle: String,
    description: String,
    filePath: String,
  ) {
    val existingVisit = visitDao.getVisitByDate(dateKey)
    val resolvedLocation =
      locationTitle.trim().ifBlank {
        existingVisit?.locationTitle?.takeIf { it.isNotBlank() } ?: "Kunjungan Hari Ini"
      }
    val resolvedDescription =
      description.trim().ifBlank { existingVisit?.description.orEmpty() }

    visitDao.upsertVisit(
      VisitDayEntity(
        dateKey = dateKey,
        locationTitle = resolvedLocation,
        description = resolvedDescription,
        updatedAt = System.currentTimeMillis(),
      )
    )

    visitDao.insertPhoto(
      MemoryPhotoEntity(
        dateKey = dateKey,
        locationTitle = resolvedLocation,
        filePath = filePath,
        createdAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun deletePhoto(photo: MemoryPhotoEntity) {
    visitDao.deletePhotoById(photo.id)
    withContext(Dispatchers.IO) { photoStorageManager.deleteFileIfExists(photo.filePath) }
  }

  fun preparePhotoFile(dateKey: String): File = photoStorageManager.createPendingPhotoFile(dateKey)

  fun getShareableUri(file: File) = photoStorageManager.getContentUri(file)

  suspend fun cleanupUnusedFile(filePath: String) {
    withContext(Dispatchers.IO) { photoStorageManager.deleteFileIfExists(filePath) }
  }
}
