package com.example.musicapp2026.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.musicapp2026.ui.screens.*
import com.example.musicapp2026.ui.theme.MusicAppTheme
import com.example.musicapp2026.ui.viewmodel.MusicViewModel
import com.example.musicapp2026.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        checkAndRequestPermissions()

        setContent {
            val currentTheme by themeViewModel.currentTheme.collectAsState()
            
            MusicAppTheme(themeType = currentTheme) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var currentMainScreen by remember { mutableStateOf<MainScreenType>(MainScreenType.List) }

                MusicAppDrawer(
                    drawerState = drawerState,
                    onSettingsClick = {
                        currentMainScreen = MainScreenType.Settings
                        scope.launch { drawerState.close() }
                    },
                    onInfoClick = { /* TODO */ }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        var showDetail by remember { mutableStateOf(false) }

                        BackHandler(enabled = showDetail || currentMainScreen != MainScreenType.List) {
                            if (showDetail) {
                                showDetail = false
                            } else {
                                currentMainScreen = MainScreenType.List
                            }
                        }

                        if (showDetail) {
                            SongDetailScreen(
                                viewModel = viewModel,
                                onBack = { showDetail = false },
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        } else {
                            when (currentMainScreen) {
                                MainScreenType.List -> {
                                    MainScreen(
                                        viewModel = viewModel,
                                        onBack = { finish() },
                                        onMenuClick = { scope.launch { drawerState.open() } },
                                        onOpenPlayer = { showDetail = true }
                                    )
                                }
                                MainScreenType.Settings -> {
                                    SettingsScreen(
                                        themeViewModel = themeViewModel,
                                        onBack = { currentMainScreen = MainScreenType.List },
                                        onMenuClick = { scope.launch { drawerState.open() } }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        permissionLauncher.launch(permission)
    }
}

enum class MainScreenType {
    List, Settings
}
