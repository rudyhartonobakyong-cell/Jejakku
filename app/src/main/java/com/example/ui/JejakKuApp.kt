package com.example.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.ui.components.rememberCameraCaptureController
import com.example.ui.screens.AccountSettingsScreen
import com.example.ui.screens.DateDetailScreen
import com.example.ui.screens.MemoriesScreen
import com.example.ui.screens.TodayVisitScreen
import com.example.ui.theme.JejakKuTheme
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

private object Routes {
  const val TODAY = "today"
  const val MEMORIES = "memories"
  const val ACCOUNT = "account"
  const val DETAIL = "detail/{dateKey}"

  fun detail(dateKey: String): String = "detail/$dateKey"
}

private data class TopLevelDestination(
  val route: String,
  val labelRes: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

private val topLevelDestinations =
  listOf(
    TopLevelDestination(
      route = Routes.TODAY,
      labelRes = R.string.nav_today,
      selectedIcon = Icons.Filled.Today,
      unselectedIcon = Icons.Outlined.Today,
      testTag = "nav_today",
    ),
    TopLevelDestination(
      route = Routes.MEMORIES,
      labelRes = R.string.nav_memories,
      selectedIcon = Icons.Filled.PhotoLibrary,
      unselectedIcon = Icons.Outlined.PhotoLibrary,
      testTag = "nav_memories",
    ),
    TopLevelDestination(
      route = Routes.ACCOUNT,
      labelRes = R.string.nav_account,
      selectedIcon = Icons.Filled.AccountCircle,
      unselectedIcon = Icons.Outlined.AccountCircle,
      testTag = "nav_account",
    ),
  )

@Composable
fun JejakKuApp(viewModel: MainViewModel) {
  val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()
  val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
  val allMemories by viewModel.allMemories.collectAsStateWithLifecycle()
  val todayMemory by viewModel.todayMemory.collectAsStateWithLifecycle()

  JejakKuTheme(themeId = selectedTheme) {
    val colorScheme = MaterialTheme.colorScheme
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isTopLevelRoute =
      topLevelDestinations.any { dest ->
        currentDestination?.hierarchy?.any { it.route == dest.route } == true
      }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    fun showMessage(resId: Int) {
      coroutineScope.launch { snackbarHostState.showSnackbar(context.getString(resId)) }
    }

    val cameraController =
      rememberCameraCaptureController(
        prepareCapture = { dateKey -> viewModel.preparePhotoCapture(dateKey) },
        onPhotoCaptured = { dateKey, locationTitle, description, filePath ->
          viewModel.onPhotoCaptured(dateKey, locationTitle, description, filePath)
          showMessage(R.string.today_photo_saved_message)
        },
        onPhotoCancelled = { filePath ->
          viewModel.onPhotoCaptureCancelled(filePath)
          showMessage(R.string.camera_capture_cancelled)
        },
        onPermissionDenied = { showMessage(R.string.camera_permission_denied) },
        onCameraUnavailable = { showMessage(R.string.camera_unavailable) },
      )

    fun navigateToTopLevel(route: String) {
      navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
      }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val useNavRail = maxWidth >= 600.dp

      Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
          if (isTopLevelRoute && !useNavRail) {
            ColumnBottomBar(
              currentDestination = currentDestination,
              onSelectRoute = ::navigateToTopLevel,
            )
          }
        },
      ) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          if (isTopLevelRoute && useNavRail) {
            NavigationRail(
              containerColor = colorScheme.surface,
              contentColor = colorScheme.onSurface,
            ) {
              topLevelDestinations.forEach { dest ->
                val selected =
                  currentDestination?.hierarchy?.any { it.route == dest.route } == true
                NavigationRailItem(
                  selected = selected,
                  onClick = { navigateToTopLevel(dest.route) },
                  icon = {
                    Icon(
                      imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                      contentDescription = stringResource(dest.labelRes),
                    )
                  },
                  label = {
                    Text(
                      text = stringResource(dest.labelRes),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  },
                  colors =
                    NavigationRailItemDefaults.colors(
                      selectedIconColor = colorScheme.primary,
                      selectedTextColor = colorScheme.primary,
                      indicatorColor = colorScheme.primaryContainer,
                      unselectedIconColor = colorScheme.onSurfaceVariant,
                      unselectedTextColor = colorScheme.onSurfaceVariant,
                    ),
                  modifier = Modifier.testTag(dest.testTag),
                )
              }
            }
          }

          NavHost(
            navController = navController,
            startDestination = Routes.TODAY,
            modifier = Modifier.weight(1f).fillMaxSize(),
          ) {
            composable(Routes.TODAY) {
              TodayVisitScreen(
                todayDateKey = viewModel.todayDateKey,
                todayMemory = todayMemory,
                onSaveVisit = { locationTitle, description ->
                  viewModel.saveVisitDetails(
                    dateKey = viewModel.todayDateKey,
                    locationTitle = locationTitle,
                    description = description,
                  )
                  showMessage(R.string.today_saved_message)
                },
                onTakePhotoClick = { locationTitle, description ->
                  cameraController.launch(
                    dateKey = viewModel.todayDateKey,
                    locationTitle = locationTitle,
                    description = description,
                  )
                },
                onOpenDateDetail = { dateKey -> navController.navigate(Routes.detail(dateKey)) },
              )
            }

            composable(Routes.MEMORIES) {
              MemoriesScreen(
                memories = allMemories,
                onOpenDateDetail = { dateKey -> navController.navigate(Routes.detail(dateKey)) },
                onGoToToday = { navigateToTopLevel(Routes.TODAY) },
              )
            }

            composable(Routes.ACCOUNT) {
              val totalPhotos = allMemories.sumOf { it.photos.size }
              AccountSettingsScreen(
                userProfile = userProfile,
                selectedTheme = selectedTheme,
                totalVisits = allMemories.size,
                totalPhotos = totalPhotos,
                onSelectTheme = viewModel::selectTheme,
                onUpdateProfile = viewModel::updateUserProfile,
              )
            }

            composable(
              route = Routes.DETAIL,
              arguments = listOf(navArgument("dateKey") { type = NavType.StringType }),
            ) { backStackEntry ->
              val dateKey = backStackEntry.arguments?.getString("dateKey") ?: viewModel.todayDateKey
              val detailMemory by
                remember(dateKey) { viewModel.observeMemoryByDate(dateKey) }
                  .collectAsStateWithLifecycle(initialValue = null)

              DateDetailScreen(
                dateKey = dateKey,
                memory = detailMemory,
                onBack = { navController.popBackStack() },
                onAddPhoto = { locationTitle, description ->
                  cameraController.launch(
                    dateKey = dateKey,
                    locationTitle = locationTitle,
                    description = description,
                  )
                },
                onUpdateVisit = { locationTitle, description ->
                  viewModel.saveVisitDetails(
                    dateKey = dateKey,
                    locationTitle = locationTitle,
                    description = description,
                  )
                  showMessage(R.string.today_saved_message)
                },
                onDeletePhoto = viewModel::deletePhoto,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ColumnBottomBar(
  currentDestination: androidx.navigation.NavDestination?,
  onSelectRoute: (String) -> Unit,
) {
  val colorScheme = MaterialTheme.colorScheme
  androidx.compose.foundation.layout.Column {
    HorizontalDivider(thickness = 1.dp, color = colorScheme.outlineVariant)
    NavigationBar(
      containerColor = colorScheme.surface,
      contentColor = colorScheme.onSurface,
      tonalElevation = 0.dp,
    ) {
      topLevelDestinations.forEach { dest ->
        val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
        NavigationBarItem(
          selected = selected,
          onClick = { onSelectRoute(dest.route) },
          icon = {
            Icon(
              imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
              contentDescription = stringResource(dest.labelRes),
            )
          },
          label = {
            Text(
              text = stringResource(dest.labelRes),
              style = MaterialTheme.typography.labelSmall,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          },
          colors =
            NavigationBarItemDefaults.colors(
              selectedIconColor = colorScheme.primary,
              selectedTextColor = colorScheme.primary,
              indicatorColor = colorScheme.primaryContainer,
              unselectedIconColor = colorScheme.onSurfaceVariant,
              unselectedTextColor = colorScheme.onSurfaceVariant,
            ),
          modifier = Modifier.testTag(dest.testTag),
        )
      }
    }
  }
}
