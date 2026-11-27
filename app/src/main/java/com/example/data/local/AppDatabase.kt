package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [VisitDayEntity::class, MemoryPhotoEntity::class],
  version = 1,
  exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun visitDao(): VisitDao

  companion object {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase =
      INSTANCE
        ?: synchronized(this) {
          INSTANCE
            ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "jejakku_database.db",
              )
              .build()
              .also { INSTANCE = it }
        }
  }
}
