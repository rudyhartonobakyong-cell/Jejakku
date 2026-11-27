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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.VisitDayWithPhotos
import com.example.ui.components.MemoryPhotoPlaceholder
import com.example.util.DateUtils
import java.io.File

@Composable
fun TodayVisitScreen(
  todayDateKey: String,
  todayMemory: VisitDayWithPhotos?,
  onSaveVisit: (locationTitle: String, description: String) -> Unit,
  onTakePhotoClick: (locationTitle: String, description: String) -> Unit,
  onOpenDateDetail: (dateKey: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var locationInput by rememberSaveable { mutableStateOf("") }
  var descriptionInput by rememberSaveable { mutableStateOf("") }
  var initializedFromDb by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(todayMemory) {
    if (todayMemory != null && !initializedFromDb) {
      locationInput = todayMemory.visit.locationTitle
      descriptionInput = todayMemory.visit.description
      initializedFromDb = true
    }
  }

  val colorScheme = MaterialTheme.colorScheme
  val photos = todayMemory?.photos.orEmpty()
  val formattedDate = DateUtils.formatFullDate(todayDateKey)

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Header
      item {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Surface(
            color = colorScheme.primaryContainer,
            shape = RoundedCornerShape(50),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(14.dp),
              )
              Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onPrimaryContainer,
              )
            }
          }
          Text(
            text = stringResource(R.string.today_title),
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onBackground,
          )
        }
      }

      // Hero preview + quick camera button
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
          border = BorderStroke(1.dp, colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column {
            Box(
              modifier =
                Modifier.fillMaxWidth()
                  .aspectRatio(16f / 9f)
                  .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                  .clickable(enabled = todayMemory != null) {
                    onOpenDateDetail(todayDateKey)
                  }
            ) {
              if (photos.isNotEmpty()) {
                AsyncImage(
                  model = File(photos.first().filePath),
                  contentDescription = stringResource(R.string.cd_memory_cover),
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize(),
                )
              } else {
                MemoryPhotoPlaceholder(
                  modifier = Modifier.fillMaxSize(),
                  locationHint =
                    locationInput.ifBlank { stringResource(R.string.today_default_location) },
                  subtitle = stringResource(R.string.today_camera_subtitle),
                )
              }

              // Photo count pill
              Surface(
                color = colorScheme.surface.copy(alpha = 0.88f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, colorScheme.outlineVariant),
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                  Icon(
                    imageVector = Icons.Filled.PhotoLibrary,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(14.dp),
                  )
                  Text(
                    text = stringResource(R.string.memories_photo_count_format, photos.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurface,
                  )
                }
              }
            }

            // Camera Capture CTA bar
            Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                Text(
                  text = stringResource(R.string.today_take_photo),
                  style = MaterialTheme.typography.titleMedium,
                  color = colorScheme.onSurface,
                )
                Text(
                  text = stringResource(R.string.today_camera_subtitle),
                  style = MaterialTheme.typography.bodySmall,
                  color = colorScheme.onSurfaceVariant,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
              }

              Button(
                onClick = { onTakePhotoClick(locationInput, descriptionInput) },
                shape = RoundedCornerShape(14.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                  ),
                modifier = Modifier.heightIn(min = 48.dp).testTag("today_camera_button"),
              ) {
                Icon(
                  imageVector = Icons.Filled.PhotoCamera,
                  contentDescription = stringResource(R.string.cd_camera_button),
                  modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = stringResource(R.string.today_take_photo),
                  style = MaterialTheme.typography.labelLarge,
                )
              }
            }
          }
        }
      }

      // Location & description form
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
          border = BorderStroke(1.dp, colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.EditNote,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(22.dp),
              )
              Text(
                text = "Detail Kunjungan",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface,
              )
            }

            OutlinedTextField(
              value = locationInput,
              onValueChange = { locationInput = it },
              label = { Text(stringResource(R.string.today_location_label)) },
              placeholder = { Text(stringResource(R.string.today_location_placeholder)) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Filled.LocationOn,
                  contentDescription = null,
                  tint = colorScheme.primary,
                )
              },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = colorScheme.primary,
                  unfocusedBorderColor = colorScheme.outline,
                  focusedLabelColor = colorScheme.primary,
                ),
              modifier = Modifier.fillMaxWidth().testTag("today_location_input"),
            )

            OutlinedTextField(
              value = descriptionInput,
              onValueChange = { descriptionInput = it },
              label = { Text(stringResource(R.string.today_description_label)) },
              placeholder = { Text(stringResource(R.string.today_description_placeholder)) },
              minLines = 3,
              maxLines = 5,
              shape = RoundedCornerShape(14.dp),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = colorScheme.primary,
                  unfocusedBorderColor = colorScheme.outline,
                  focusedLabelColor = colorScheme.primary,
                ),
              modifier = Modifier.fillMaxWidth().testTag("today_description_input"),
            )

            FilledTonalButton(
              onClick = { onSaveVisit(locationInput, descriptionInput) },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("today_save_button"),
            ) {
              Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = stringResource(R.string.today_save_visit),
                style = MaterialTheme.typography.labelLarge,
              )
            }
          }
        }
      }

      // Today's photo gallery strip
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = stringResource(R.string.today_photos_section),
              style = MaterialTheme.typography.titleMedium,
              color = colorScheme.onBackground,
            )
            if (photos.isNotEmpty()) {
              Text(
                text = "Lihat Detail",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.primary,
                modifier =
                  Modifier.clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenDateDetail(todayDateKey) }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
              )
            }
          }

          if (photos.isEmpty()) {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = colorScheme.surface,
              border = BorderStroke(1.dp, colorScheme.outlineVariant),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Surface(
                  shape = CircleShape,
                  color = colorScheme.primaryContainer,
                  modifier = Modifier.size(40.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Filled.PhotoCamera,
                      contentDescription = null,
                      tint = colorScheme.primary,
                      modifier = Modifier.size(20.dp),
                    )
                  }
                }
                Text(
                  text = stringResource(R.string.today_no_photos_yet),
                  style = MaterialTheme.typography.bodyMedium,
                  color = colorScheme.onSurfaceVariant,
                )
              }
            }
          } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              items(photos, key = { it.id }) { photo ->
                Box(
                  modifier =
                    Modifier.size(width = 132.dp, height = 100.dp)
                      .clip(RoundedCornerShape(14.dp))
                      .clickable { onOpenDateDetail(todayDateKey) }
                ) {
                  AsyncImage(
                    model = File(photo.filePath),
                    contentDescription = stringResource(R.string.cd_memory_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                  )
                  Surface(
                    color = colorScheme.surface.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp),
                  ) {
                    Text(
                      text = DateUtils.formatTime(photo.createdAt),
                      style = MaterialTheme.typography.labelSmall,
                      color = colorScheme.onSurface,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                  }
                }
              }
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}
