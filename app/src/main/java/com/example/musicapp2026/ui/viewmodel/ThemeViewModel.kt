package com.example.musicapp2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.musicapp2026.ui.theme.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor() : ViewModel() {
    private val _currentTheme = MutableStateFlow(ThemeType.DEEP_MIDNIGHT)
    val currentTheme: StateFlow<ThemeType> = _currentTheme.asStateFlow()

    fun setTheme(themeType: ThemeType) {
        _currentTheme.value = themeType
    }
}
