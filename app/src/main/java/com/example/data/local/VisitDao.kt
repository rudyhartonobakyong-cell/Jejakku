package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {

  @Transaction
  @Query("SELECT * FROM visit_days ORDER BY dateKey DESC")
  fun observeAllVisitsWithPhotos(): Flow<List<VisitDayWithPhotos>>

  @Transaction
  @Query("SELECT * FROM visit_days WHERE dateKey = :dateKey LIMIT 1")
  fun observeVisitWithPhotos(dateKey: String): Flow<VisitDayWithPhotos?>

  @Query("SELECT * FROM visit_days WHERE dateKey = :dateKey LIMIT 1")
  suspend fun getVisitByDate(dateKey: String): VisitDayEntity?

  @Upsert
  suspend fun upsertVisit(visit: VisitDayEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPhoto(photo: MemoryPhotoEntity): Long

  @Query("DELETE FROM memory_photos WHERE id = :photoId")
  suspend fun deletePhotoById(photoId: Long)

  @Query("UPDATE memory_photos SET locationTitle = :locationTitle WHERE dateKey = :dateKey")
  suspend fun updatePhotosLocationForDate(dateKey: String, locationTitle: String)
}
