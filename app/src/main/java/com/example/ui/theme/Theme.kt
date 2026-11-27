package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalActiveThemeSpec = staticCompositionLocalOf { AppThemes.specFor(AppThemeId.SAMUDRA) }

@Composable
fun JejakKuTheme(
  themeId: AppThemeId = AppThemeId.SAMUDRA,
  content: @Composable () -> Unit,
) {
  val spec = AppThemes.specFor(themeId)
  val colorScheme = spec.toColorScheme()
  val view = LocalView.current

  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window ?: return@SideEffect
      val controller = WindowCompat.getInsetsController(window, view)
      controller.isAppearanceLightStatusBars = !spec.isDark
      controller.isAppearanceLightNavigationBars = !spec.isDark
    }
  }

  CompositionLocalProvider(LocalActiveThemeSpec provides spec) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = AppTypography,
      content = content,
    )
  }
}
