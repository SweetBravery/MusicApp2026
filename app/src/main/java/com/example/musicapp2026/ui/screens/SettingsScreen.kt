package com.example.musicapp2026.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musicapp2026.ui.theme.AppThemePresets
import com.example.musicapp2026.ui.theme.ThemeType
import com.example.musicapp2026.ui.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel,
    onBack: () -> Unit,
    onMenuClick: () -> Unit
) {
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Row {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { SettingsHeader("Interfaz y Personalización") }
            
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tema de la App", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Claros", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    ThemeSelectorRow(
                        themes = listOf(
                            ThemeType.CLASSIC_GOLD, ThemeType.OCEAN_BREEZE, 
                            ThemeType.SAKURA, ThemeType.FOREST_LIGHT, ThemeType.ROYAL_PURPLE
                        ),
                        selectedTheme = currentTheme,
                        onThemeSelect = { themeViewModel.setTheme(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Oscuros", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    ThemeSelectorRow(
                        themes = listOf(
                            ThemeType.DEEP_MIDNIGHT, ThemeType.NEON_CYBER, 
                            ThemeType.NORDIC_ICE, ThemeType.BLOOD_MOON, ThemeType.OBSIDIAN_MINT
                        ),
                        selectedTheme = currentTheme,
                        onThemeSelect = { themeViewModel.setTheme(it) }
                    )
                }
            }

            item { SettingItem(Icons.Default.ColorLens, "Colores Dinámicos", "Material You") }
            item { SettingItem(Icons.Default.Style, "Estilo del Reproductor", "Elegir diseño") }
            item { SettingItem(Icons.Default.GridView, "Vista por Defecto", "Listado / Cuadrícula") }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            item { SettingsHeader("Audio y Reproducción") }
            item { SettingItem(Icons.Default.GraphicEq, "Ecualizador", "Ajustar bandas de audio") }
            item { SettingItem(Icons.AutoMirrored.Filled.CompareArrows, "Fundido Cruzado (Crossfade)", "0 segundos") }
            item { SettingItem(Icons.AutoMirrored.Filled.VolumeUp, "Normalización de Volumen", "Mantener nivel constante") }
            item { SettingItem(Icons.Default.Bluetooth, "Reproducción Automática", "Al conectar auriculares") }
            item { SettingItem(Icons.Default.Snooze, "Temporizador de Sueño", "Desactivado") }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            item { SettingsHeader("Gestión de la Biblioteca") }
            item { SettingItem(Icons.Default.Folder, "Escaneo de Carpetas", "Seleccionar carpetas") }
            item { SettingItem(Icons.Default.Timer, "Filtro de Duración Mínima", "30 segundos") }
            item { SettingItem(Icons.Default.Sync, "Sincronización de Metadatos", "API theaudioDB") }
            item { SettingItem(Icons.Default.DeleteSweep, "Limpiar Caché", "Liberar espacio") }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            item { SettingsHeader("Notificaciones y Controles") }
            item { SettingItem(Icons.Default.Lock, "Controles en Pantalla de Bloqueo", "Activar / Desactivar") }
            item { SettingItem(Icons.Default.Vibration, "Gesto de Sacudir", "Cambiar de canción") }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun ThemeSelectorRow(
    themes: List<ThemeType>,
    selectedTheme: ThemeType,
    onThemeSelect: (ThemeType) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(themes) { theme ->
            val colorScheme = AppThemePresets.getColorScheme(theme)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorScheme.background)
                    .border(
                        width = if (selectedTheme == theme) 3.dp else 1.dp,
                        color = if (selectedTheme == theme) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable { onThemeSelect(theme) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SettingItem(icon: ImageVector, title: String, subtitle: String) {
    Surface(
        onClick = { /* TODO */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
