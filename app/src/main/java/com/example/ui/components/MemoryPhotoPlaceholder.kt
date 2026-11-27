package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun MemoryPhotoPlaceholder(
  modifier: Modifier = Modifier,
  locationHint: String? = null,
  subtitle: String? = null,
) {
  val colorScheme = MaterialTheme.colorScheme
  val gradientBrush =
    Brush.linearGradient(
      colors =
        listOf(
          colorScheme.surfaceVariant,
          colorScheme.primaryContainer.copy(alpha = 0.72f),
          colorScheme.primary.copy(alpha = 0.28f),
        )
    )

  Box(
    modifier = modifier.background(brush = gradientBrush),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val radiusLarge = size.minDimension * 0.44f
      val radiusSmall = size.minDimension * 0.24f
      drawCircle(
        color = colorScheme.primary.copy(alpha = 0.12f),
        radius = radiusLarge,
        center = Offset(size.width * 0.18f, size.height * 0.22f),
      )
      drawCircle(
        color = colorScheme.primary.copy(alpha = 0.10f),
        radius = radiusSmall,
        center = Offset(size.width * 0.85f, size.height * 0.78f),
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(16.dp),
    ) {
      Surface(
        shape = CircleShape,
        color = colorScheme.surface.copy(alpha = 0.88f),
        tonalElevation = 4.dp,
        modifier = Modifier.size(56.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = stringResource(R.string.cd_location_pin),
            tint = colorScheme.primary,
            modifier = Modifier.size(30.dp),
          )
        }
      }

      if (!locationHint.isNullOrBlank()) {
        Text(
          text = locationHint,
          style = MaterialTheme.typography.titleSmall,
          color = colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      if (!subtitle.isNullOrBlank()) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}
