package com.example.ui.components

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.File

@Stable
class CameraCaptureController(
  private val startFlow: (dateKey: String, locationTitle: String, description: String) -> Unit
) {
  fun launch(dateKey: String, locationTitle: String, description: String) {
    startFlow(dateKey, locationTitle, description)
  }
}

@Composable
fun rememberCameraCaptureController(
  prepareCapture: (dateKey: String) -> Pair<File, Uri>,
  onPhotoCaptured: (dateKey: String, locationTitle: String, description: String, filePath: String) -> Unit,
  onPhotoCancelled: (filePath: String) -> Unit,
  onPermissionDenied: () -> Unit,
  onCameraUnavailable: () -> Unit,
): CameraCaptureController {
  val context = LocalContext.current

  val currentPrepareCapture by rememberUpdatedState(prepareCapture)
  val currentOnCaptured by rememberUpdatedState(onPhotoCaptured)
  val currentOnCancelled by rememberUpdatedState(onPhotoCancelled)
  val currentOnDenied by rememberUpdatedState(onPermissionDenied)
  val currentOnUnavailable by rememberUpdatedState(onCameraUnavailable)

  var pendingDateKey by rememberSaveable { mutableStateOf("") }
  var pendingLocation by rememberSaveable { mutableStateOf("") }
  var pendingDescription by rememberSaveable { mutableStateOf("") }
  var pendingFilePath by rememberSaveable { mutableStateOf<String?>(null) }

  val takePictureLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
      val path = pendingFilePath
      pendingFilePath = null
      if (path != null) {
        if (success) {
          currentOnCaptured(pendingDateKey, pendingLocation, pendingDescription, path)
        } else {
          currentOnCancelled(path)
        }
      }
    }

  val launchCameraIntent: () -> Unit = {
    try {
      val (file, uri) = currentPrepareCapture(pendingDateKey)
      pendingFilePath = file.absolutePath
      takePictureLauncher.launch(uri)
    } catch (_: ActivityNotFoundException) {
      pendingFilePath?.let { currentOnCancelled(it) }
      pendingFilePath = null
      currentOnUnavailable()
    } catch (_: Exception) {
      pendingFilePath?.let { currentOnCancelled(it) }
      pendingFilePath = null
      currentOnUnavailable()
    }
  }

  val permissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      if (granted) {
        launchCameraIntent()
      } else {
        currentOnDenied()
      }
    }

  return remember(context) {
    CameraCaptureController { dateKey, locationTitle, description ->
      pendingDateKey = dateKey
      pendingLocation = locationTitle
      pendingDescription = description
      val hasPermission =
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
          PackageManager.PERMISSION_GRANTED
      if (hasPermission) {
        launchCameraIntent()
      } else {
        permissionLauncher.launch(Manifest.permission.CAMERA)
      }
    }
  }
}
