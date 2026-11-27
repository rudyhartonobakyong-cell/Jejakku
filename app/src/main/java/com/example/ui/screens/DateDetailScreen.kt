package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.MemoryPhotoEntity
import com.example.data.local.VisitDayWithPhotos
import com.example.ui.components.MemoryPhotoPlaceholder
import com.example.util.DateUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDetailScreen(
  dateKey: String,
  memory: VisitDayWithPhotos?,
  onBack: () -> Unit,
  onAddPhoto: (locationTitle: String, description: String) -> Unit,
  onUpdateVisit: (locationTitle: String, description: String) -> Unit,
  onDeletePhoto: (MemoryPhotoEntity) -> Unit,
  modifier: Modifier = Modifier,
) {
  val colorScheme = MaterialTheme.colorScheme
  val photos = memory?.photos.orEmpty()
  val locationTitle = memory?.visit?.locationTitle ?: stringResource(R.string.today_default_location)
  val description = memory?.visit?.description.orEmpty()
  val fullDate = DateUtils.formatFullDate(dateKey)

  var showEditDialog by remember { mutableStateOf(false) }
  var photoToDelete by remember { mutableStateOf<MemoryPhotoEntity?>(null) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = colorScheme.background,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = stringResource(R.string.detail_title),
              style = MaterialTheme.typography.titleMedium,
              color = colorScheme.onBackground,
            )
            Text(
              text = fullDate,
              style = MaterialTheme.typography.bodySmall,
              color = colorScheme.onSurfaceVariant,
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("detail_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.detail_back),
              tint = colorScheme.onBackground,
            )
          }
        },
        actions = {
          IconButton(
            onClick = { showEditDialog = true },
            modifier = Modifier.testTag("detail_edit_button"),
          ) {
            Icon(
              imageVector = Icons.Filled.Edit,
              contentDescription = stringResource(R.string.detail_edit_visit),
              tint = colorScheme.primary,
            )
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.background,
            titleContentColor = colorScheme.onBackground,
          ),
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        // Horizontal Photo Pager + Dot Indicators
        item {
          if (photos.isEmpty()) {
            Card(
              shape = RoundedCornerShape(20.dp),
              border = BorderStroke(1.dp, colorScheme.outlineVariant),
              colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
              modifier = Modifier.fillMaxWidth(),
            ) {
              MemoryPhotoPlaceholder(
                modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f),
                locationHint = locationTitle,
                subtitle = "Belum ada foto pada tanggal ini",
              )
            }
          } else {
            val pagerState = rememberPagerState(pageCount = { photos.size })
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Box(
                modifier =
                  Modifier.fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("detail_photo_pager")
              ) {
                HorizontalPager(
                  state = pagerState,
                  modifier = Modifier.fillMaxSize(),
                ) { page ->
                  val photo = photos[page]
                  Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                      model = File(photo.filePath),
                      contentDescription = stringResource(R.string.cd_memory_photo),
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize(),
                    )
                    Surface(
                      color = colorScheme.background.copy(alpha = 0.78f),
                      shape = RoundedCornerShape(50),
                      modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    ) {
                      Text(
                        text = DateUtils.formatTime(photo.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                      )
                    }
                  }
                }

                // Counter badge
                Surface(
                  color = colorScheme.background.copy(alpha = 0.82f),
                  shape = RoundedCornerShape(50),
                  border = BorderStroke(0.5.dp, colorScheme.outline),
                  modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                ) {
                  Text(
                    text = "${pagerState.currentPage + 1} / ${photos.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  )
                }

                // Delete current photo button
                val currentPhoto = photos.getOrNull(pagerState.currentPage)
                if (currentPhoto != null) {
                  Surface(
                    color = colorScheme.background.copy(alpha = 0.82f),
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                  ) {
                    IconButton(
                      onClick = { photoToDelete = currentPhoto },
                      modifier = Modifier.size(48.dp).testTag("detail_delete_photo_button"),
                    ) {
                      Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = stringResource(R.string.detail_delete_photo),
                        tint = colorScheme.error,
                      )
                    }
                  }
                }
              }

              // Dot indicator row
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("detail_pager_dots"),
              ) {
                repeat(photos.size) { index ->
                  val isSelected = pagerState.currentPage == index
                  Box(
                    modifier =
                      Modifier.height(8.dp)
                        .width(if (isSelected) 22.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                          if (isSelected) colorScheme.primary
                          else colorScheme.outline.copy(alpha = 0.7f)
                        )
                  )
                }
              }
            }
          }
        }

        // Location & travel description card
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            border = BorderStroke(1.dp, colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Surface(
                  shape = CircleShape,
                  color = colorScheme.primaryContainer,
                  modifier = Modifier.size(38.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Filled.LocationOn,
                      contentDescription = stringResource(R.string.cd_location_pin),
                      tint = colorScheme.primary,
                      modifier = Modifier.size(20.dp),
                    )
                  }
                }
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = locationTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.onSurface,
                  )
                  Text(
                    text =
                      "$fullDate • ${stringResource(R.string.memories_photo_count_format, photos.size)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.primary,
                  )
                }
              }

              Text(
                text = stringResource(R.string.detail_description_title),
                style = MaterialTheme.typography.titleSmall,
                color = colorScheme.onSurfaceVariant,
              )
              Text(
                text =
                  description.ifBlank { stringResource(R.string.memories_no_description) },
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurface,
              )
            }
          }
        }

        // Add Photo button
        item {
          Button(
            onClick = { onAddPhoto(locationTitle, description) },
            shape = RoundedCornerShape(16.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
              ),
            modifier =
              Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("detail_add_photo_button"),
          ) {
            Icon(
              imageVector = Icons.Filled.AddAPhoto,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = stringResource(R.string.detail_add_photo),
              style = MaterialTheme.typography.labelLarge,
            )
          }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
      }
    }
  }

  if (showEditDialog) {
    EditVisitDialog(
      initialLocation = locationTitle,
      initialDescription = description,
      onDismiss = { showEditDialog = false },
      onConfirm = { newLocation, newDescription ->
        onUpdateVisit(newLocation, newDescription)
        showEditDialog = false
      },
    )
  }

  photoToDelete?.let { target ->
    AlertDialog(
      onDismissRequest = { photoToDelete = null },
      title = { Text(stringResource(R.string.detail_delete_confirm_title)) },
      text = { Text(stringResource(R.string.detail_delete_confirm_body)) },
      confirmButton = {
        TextButton(
          onClick = {
            onDeletePhoto(target)
            photoToDelete = null
          }
        ) {
          Text(
            text = stringResource(R.string.action_delete),
            color = colorScheme.error,
          )
        }
      },
      dismissButton = {
        TextButton(onClick = { photoToDelete = null }) {
          Text(stringResource(R.string.action_cancel))
        }
      },
    )
  }
}

@Composable
private fun EditVisitDialog(
  initialLocation: String,
  initialDescription: String,
  onDismiss: () -> Unit,
  onConfirm: (locationTitle: String, description: String) -> Unit,
) {
  var location by remember(initialLocation) { mutableStateOf(initialLocation) }
  var description by remember(initialDescription) { mutableStateOf(initialDescription) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.detail_edit_visit)) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = location,
          onValueChange = { location = it },
          label = { Text(stringResource(R.string.today_location_label)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text(stringResource(R.string.today_description_label)) },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    confirmButton = {
      TextButton(onClick = { onConfirm(location, description) }) {
        Text(stringResource(R.string.action_save))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.action_cancel))
      }
    },
  )
}
