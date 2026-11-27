package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.preferences.UserProfile
import com.example.ui.theme.AppThemeId
import com.example.ui.theme.AppThemeSpec
import com.example.ui.theme.AppThemes

@Composable
fun AccountSettingsScreen(
  userProfile: UserProfile,
  selectedTheme: AppThemeId,
  totalVisits: Int,
  totalPhotos: Int,
  onSelectTheme: (AppThemeId) -> Unit,
  onUpdateProfile: (name: String, bio: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val colorScheme = MaterialTheme.colorScheme
  var showEditProfileDialog by remember { mutableStateOf(false) }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        Text(
          text = stringResource(R.string.account_title),
          style = MaterialTheme.typography.headlineMedium,
          color = colorScheme.onBackground,
        )
      }

      // Profile Summary Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
          border = BorderStroke(1.dp, colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Surface(
                shape = CircleShape,
                color = colorScheme.primaryContainer,
                modifier = Modifier.size(54.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                  )
                }
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = userProfile.displayName,
                  style = MaterialTheme.typography.titleLarge,
                  color = colorScheme.onSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = userProfile.bio,
                  style = MaterialTheme.typography.bodySmall,
                  color = colorScheme.onSurfaceVariant,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
              }

              IconButton(
                onClick = { showEditProfileDialog = true },
                modifier = Modifier.testTag("account_edit_profile_button"),
              ) {
                Icon(
                  imageVector = Icons.Filled.Edit,
                  contentDescription = stringResource(R.string.account_edit_profile),
                  tint = colorScheme.primary,
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              StatPill(
                label = stringResource(R.string.account_stats_visits),
                value = totalVisits.toString(),
                modifier = Modifier.weight(1f),
              )
              StatPill(
                label = stringResource(R.string.account_stats_photos),
                value = totalPhotos.toString(),
                modifier = Modifier.weight(1f),
              )
            }
          }
        }
      }

      // Tema Aplikasi section (6 color preview cards)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.Palette,
              contentDescription = null,
              tint = colorScheme.primary,
              modifier = Modifier.size(22.dp),
            )
            Text(
              text = stringResource(R.string.theme_section_title),
              style = MaterialTheme.typography.titleLarge,
              color = colorScheme.onBackground,
            )
          }

          Text(
            text = stringResource(R.string.theme_section_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant,
          )

          val themeRows = AppThemes.all.chunked(2)
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            themeRows.forEach { rowSpecs ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                rowSpecs.forEach { spec ->
                  ThemePreviewCard(
                    spec = spec,
                    isSelected = spec.id == selectedTheme,
                    onClick = { onSelectTheme(spec.id) },
                    modifier = Modifier.weight(1f),
                  )
                }
                if (rowSpecs.size == 1) {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          }
        }
      }

      // Local storage info card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
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
                  imageVector = Icons.Filled.FolderOpen,
                  contentDescription = null,
                  tint = colorScheme.primary,
                  modifier = Modifier.size(20.dp),
                )
              }
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(R.string.storage_info_title),
                style = MaterialTheme.typography.titleSmall,
                color = colorScheme.onSurface,
              )
              Text(
                text = stringResource(R.string.storage_info_body, totalPhotos),
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }

  if (showEditProfileDialog) {
    EditProfileDialog(
      initialName = userProfile.displayName,
      initialBio = userProfile.bio,
      onDismiss = { showEditProfileDialog = false },
      onConfirm = { newName, newBio ->
        onUpdateProfile(newName, newBio)
        showEditProfileDialog = false
      },
    )
  }
}

@Composable
private fun StatPill(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  val colorScheme = MaterialTheme.colorScheme
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = colorScheme.surfaceVariant.copy(alpha = 0.7f),
    modifier = modifier,
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        color = colorScheme.primary,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun ThemePreviewCard(
  spec: AppThemeSpec,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val activeScheme = MaterialTheme.colorScheme
  val borderColor = if (isSelected) activeScheme.primary else activeScheme.outlineVariant
  val borderWidth = if (isSelected) 2.dp else 1.dp

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = activeScheme.surface),
    border = BorderStroke(borderWidth, borderColor),
    modifier =
      modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick)
        .testTag("theme_card_${spec.id.storageKey}"),
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      // Mini theme palette preview box
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(spec.background)
            .padding(8.dp)
      ) {
        // Mini card strip
        Column(
          modifier = Modifier.align(Alignment.CenterStart).fillMaxWidth(0.68f),
          verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(spec.surface)
          )
          Box(
            modifier =
              Modifier.fillMaxWidth(0.75f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(spec.onBackground.copy(alpha = 0.28f))
          )
          Box(
            modifier =
              Modifier.width(34.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(spec.accent)
          )
        }

        // Accent color swatch circle
        Box(
          modifier =
            Modifier.align(Alignment.BottomEnd)
              .size(20.dp)
              .clip(CircleShape)
              .background(spec.accent)
        )

        if (isSelected) {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = stringResource(R.string.cd_theme_selected),
            tint = spec.accent,
            modifier = Modifier.align(Alignment.TopEnd).size(18.dp),
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = spec.displayName,
            style = MaterialTheme.typography.titleSmall,
            color = activeScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text =
              if (spec.isDark) stringResource(R.string.theme_mode_dark)
              else stringResource(R.string.theme_mode_light),
            style = MaterialTheme.typography.labelSmall,
            color = activeScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
private fun EditProfileDialog(
  initialName: String,
  initialBio: String,
  onDismiss: () -> Unit,
  onConfirm: (name: String, bio: String) -> Unit,
) {
  var name by remember(initialName) { mutableStateOf(initialName) }
  var bio by remember(initialBio) { mutableStateOf(initialBio) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.account_edit_profile)) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text(stringResource(R.string.account_name_label)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
          value = bio,
          onValueChange = { bio = it },
          label = { Text(stringResource(R.string.account_bio_label)) },
          minLines = 2,
          maxLines = 3,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    confirmButton = {
      TextButton(onClick = { onConfirm(name, bio) }) {
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
