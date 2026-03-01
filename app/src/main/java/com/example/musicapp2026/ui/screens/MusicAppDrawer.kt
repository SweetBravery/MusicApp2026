package com.example.musicapp2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.system.exitProcess

@Composable
fun MusicAppDrawer(
    drawerState: DrawerState,
    onSettingsClick: () -> Unit,
    onInfoClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Music App 2026",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Configuración") },
                    selected = false,
                    onClick = onSettingsClick,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Información") },
                    selected = false,
                    onClick = onInfoClick,
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Salir de la app") },
                    selected = false,
                    onClick = { exitProcess(0) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) }
                )
            }
        },
        content = content
    )
}
