package com.example

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.preferences.AppPreferencesRepository
import com.example.data.repository.VisitMemoryRepository
import com.example.data.storage.PhotoStorageManager
import com.example.ui.theme.AppThemeId
import com.example.ui.theme.AppThemes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class JejakKuFeatureTest {

  private lateinit var context: Context
  private lateinit var database: AppDatabase
  private lateinit var repository: VisitMemoryRepository

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    database =
      Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    repository = VisitMemoryRepository(database.visitDao(), PhotoStorageManager(context))
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `six app themes have exact colors and dark-light flags`() {
    assertEquals(6, AppThemes.all.size)

    val samudra = AppThemes.specFor(AppThemeId.SAMUDRA)
    assertTrue(samudra.isDark)
    assertEquals(Color(0xFF0A1018), samudra.background)
    assertEquals(Color(0xFF4FC3F7), samudra.accent)

    val pinkManis = AppThemes.specFor(AppThemeId.PINK_MANIS)
    assertFalse(pinkManis.isDark)
    assertEquals(Color(0xFFFFF0F5), pinkManis.background)
    assertEquals(Color(0xFFEC4899), pinkManis.accent)

    val hitamElegan = AppThemes.specFor(AppThemeId.HITAM_ELEGAN)
    assertTrue(hitamElegan.isDark)
    assertEquals(Color(0xFF000000), hitamElegan.background)
    assertEquals(Color(0xFFD4AF37), hitamElegan.accent)

    val hijauHutan = AppThemes.specFor(AppThemeId.HIJAU_HUTAN)
    assertTrue(hijauHutan.isDark)
    assertEquals(Color(0xFF0F1F17), hijauHutan.background)
    assertEquals(Color(0xFF4ADE80), hijauHutan.accent)

    val unguSenja = AppThemes.specFor(AppThemeId.UNGU_SENJA)
    assertTrue(unguSenja.isDark)
    assertEquals(Color(0xFF1A1033), unguSenja.background)
    assertEquals(Color(0xFFA78BFA), unguSenja.accent)

    val oranyePantai = AppThemes.specFor(AppThemeId.ORANYE_PANTAI)
    assertFalse(oranyePantai.isDark)
    assertEquals(Color(0xFFFFF7ED), oranyePantai.background)
    assertEquals(Color(0xFFF97316), oranyePantai.accent)
  }

  @Test
  fun `captured photos link to date and location and support deletion`() = runTest {
    val file1 = repository.preparePhotoFile("2026-11-26")
    file1.writeText("photo1")
    repository.saveCapturedPhoto(
      dateKey = "2026-11-26",
      locationTitle = "Kawah Putih",
      description = "Pagi berkabut yang sejuk.",
      filePath = file1.absolutePath,
    )

    val file2 = repository.preparePhotoFile("2026-11-26")
    file2.writeText("photo2")
    repository.saveCapturedPhoto(
      dateKey = "2026-11-26",
      locationTitle = "Kawah Putih",
      description = "",
      filePath = file2.absolutePath,
    )

    val memory = repository.observeMemoryByDate("2026-11-26").first()!!
    assertEquals("Kawah Putih", memory.visit.locationTitle)
    assertEquals("Pagi berkabut yang sejuk.", memory.visit.description)
    assertEquals(2, memory.photos.size)
    assertEquals(file1.absolutePath, memory.photos.first().filePath)

    // Delete first photo and verify file and DB row are removed
    repository.deletePhoto(memory.photos.first())
    val updated = repository.observeMemoryByDate("2026-11-26").first()!!
    assertEquals(1, updated.photos.size)
    assertFalse(file1.exists())
    file2.delete()
  }

  @Test
  fun `theme selection persists in datastore`() = runTest {
    val prefs = AppPreferencesRepository(context)
    assertEquals(AppThemeId.SAMUDRA, prefs.selectedTheme.first())

    prefs.setTheme(AppThemeId.UNGU_SENJA)
    assertEquals(AppThemeId.UNGU_SENJA, prefs.selectedTheme.first())

    prefs.setTheme(AppThemeId.ORANYE_PANTAI)
    assertEquals(AppThemeId.ORANYE_PANTAI, prefs.selectedTheme.first())
  }
}
