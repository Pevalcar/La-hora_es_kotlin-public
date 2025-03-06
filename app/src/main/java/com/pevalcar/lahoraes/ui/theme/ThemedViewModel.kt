package com.pevalcar.lahoraes.ui.theme

import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pevalcar.lahoraes.utils.Constats
import com.pevalcar.lahoraes.utils.Constats.AppTheme
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemedViewModel @Inject constructor(
    private val dataStore : DataStore<Preferences>
) : ViewModel()  {

    private val _darkTheme : MutableStateFlow<AppTheme> = MutableStateFlow(AppTheme.SYSTEM)
    val darkTheme: StateFlow<AppTheme?> = _darkTheme

    private val _isExpanded : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isExpanded: StateFlow<Boolean> = _isExpanded


    fun toggleTheme( mode : AppTheme ) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.APP_THEME] = mode.name
            }
        }
        _isExpanded.value = false
    }

    fun toggleExpanded() {
        _isExpanded.value = !_isExpanded.value
    }
    init {
        loadSavedTheme()
    }

    private fun loadSavedTheme() {
        viewModelScope.launch {
            dataStore.data
                .map { preferences ->
                    preferences[PreferencesKeys.APP_THEME]?.let { themeName ->
                        AppTheme.valueOf(themeName)
                    } ?: AppTheme.SYSTEM
                }
                .collect { theme ->
                    _darkTheme.value = theme
                }
        }
    }

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
    }


}