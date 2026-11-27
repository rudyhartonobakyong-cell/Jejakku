package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.VisitDayWithPhotos
import com.example.ui.components.MemoryPhotoPlaceholder
import com.example.util.DateUtils
import java.io.File

@Composable
fun MemoriesScreen(
  memories: List<VisitDayWithPhotos>,
  onOpenDateDetail: (dateKey: String) -> Unit,
  onGoToToday: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colorScheme = MaterialTheme.colorScheme

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = stringResource(R.string.memories_title),
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onBackground,
          )
          Text(
            text = stringResource(R.string.memories_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
          )
        }
      }

      if (memories.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            border = BorderStroke(1.dp, colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              MemoryPhotoPlaceholder(
                modifier =
                  Modifier.fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(14.dp))
              )
              Text(
                text = stringResource(R.string.memories_empty_title),
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center,
              )
              Text(
                text = stringResource(R.string.memories_empty_message),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
              )
              Button(
                onClick = onGoToToday,
                shape = RoundedCornerShape(14.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                  ),
                modifier = Modifier.heightIn(min = 48.dp).testTag("memories_go_today_button"),
              ) {
                Text(
                  text = stringResource(R.string.memories_go_today),
                  style = MaterialTheme.typography.labelLarge,
                )
              }
            }
          }
        }
      } else {
        items(memories, key = { it.visit.dateKey }) { item ->
          YouTubeStyleMemoryCard(
            memory = item,
            onClick = { onOpenDateDetail(item.visit.dateKey) },
          )
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}

@Composable
private fun YouTubeStyleMemoryCard(
  memory: VisitDayWithPhotos,
  onClick: () -> Unit,
) {
  val colorScheme = MaterialTheme.colorScheme
  val photos = memory.photos
  val coverPhoto = photos.firstOrNull()
  val formattedDate = DateUtils.formatShortDate(memory.visit.dateKey)
  val photoCountLabel = stringResource(R.string.memories_photo_count_format, photos.size)
  val descriptionSnippet =
    memory.visit.description.ifBlank { stringResource(R.string.memories_no_description) }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
    border = BorderStroke(1.dp, colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onClick)
        .testTag("memory_card_${memory.visit.dateKey}"),
  ) {
    Column {
      // YouTube-style 16:9 large cover image
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
      ) {
        if (coverPhoto != null) {
          AsyncImage(
            model = File(coverPhoto.filePath),
            contentDescription = stringResource(R.string.cd_memory_cover),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
        } else {
          MemoryPhotoPlaceholder(modifier = Modifier.fillMaxSize())
        }

        // Bottom-right photo count pill (YouTube duration chip style)
        Surface(
          color = colorScheme.background.copy(alpha = 0.84f),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(0.5.dp, colorScheme.outline),
          modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.PhotoLibrary,
              contentDescription = null,
              tint = colorScheme.primary,
              modifier = Modifier.size(13.dp),
            )
            Text(
              text = photoCountLabel,
              style = MaterialTheme.typography.labelSmall,
              color = colorScheme.onBackground,
            )
          }
        }
      }

      // YouTube-style metadata row below thumbnail
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
      ) {
        Surface(
          shape = CircleShape,
          color = colorScheme.primaryContainer,
          modifier = Modifier.size(40.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Filled.LocationOn,
              contentDescription = stringResource(R.string.cd_location_pin),
              tint = colorScheme.primary,
              modifier = Modifier.size(22.dp),
            )
          }
        }

        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
          Text(
            text = memory.visit.locationTitle,
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = "$formattedDate • $photoCountLabel",
            style = MaterialTheme.typography.labelMedium,
            color = colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = descriptionSnippet,
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }
    }
  }
}
