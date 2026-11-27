package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "visit_days")
data class VisitDayEntity(
  @PrimaryKey val dateKey: String,
  val locationTitle: String,
  val description: String,
  val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(
  tableName = "memory_photos",
  foreignKeys =
    [
      ForeignKey(
        entity = VisitDayEntity::class,
        parentColumns = ["dateKey"],
        childColumns = ["dateKey"],
        onDelete = ForeignKey.CASCADE,
      )
    ],
  indices = [Index(value = ["dateKey"])],
)
data class MemoryPhotoEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val dateKey: String,
  val locationTitle: String,
  val filePath: String,
  val createdAt: Long = System.currentTimeMillis(),
)

data class VisitDayWithPhotos(
  @Embedded val visit: VisitDayEntity,
  @Relation(parentColumn = "dateKey", entityColumn = "dateKey")
  val photos: List<MemoryPhotoEntity>,
)
