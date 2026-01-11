package com.pevalcar.lahoraes

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pevalcar.lahoraes.data.AccesAppRepo
import com.pevalcar.lahoraes.data.ConfigsData
import com.pevalcar.lahoraes.data.TimeSettingsRepository
import com.pevalcar.lahoraes.domain.CanAccesToApp
import com.pevalcar.lahoraes.usecase.TimeServiceUseCase
import com.pevalcar.lahoraes.utils.Constats.AppTheme
import com.pevalcar.lahoraes.utils.Constats.AppThemeName
import com.pevalcar.lahoraes.utils.Constats.EMAIL_CONTACT
import com.pevalcar.lahoraes.utils.PreferencesKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val timeSettingsRepository: TimeSettingsRepository,
    private val canAccesToApp: AccesAppRepo,
    private val canStopApp: CanAccesToApp,
    @ApplicationContext private val context: Context,
    private val timeServiceUseCase: TimeServiceUseCase
) : ViewModel() {


    private val _initText = MutableStateFlow("La hora es")
    val initText: StateFlow<String> = _initText
    private val _finalText = MutableStateFlow("")
    val finalText: StateFlow<String> = _finalText


    private val _wakeLockEnabled = MutableStateFlow(false)
    val wakeLockEnabled: StateFlow<Boolean> = _wakeLockEnabled

    private val _serviceRunning = MutableStateFlow(false)
    val serviceRunning: StateFlow<Boolean> = _serviceRunning
    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime
    val availableIntervals = listOf(1, 5, 10, 15, 30, 60)

    private val _selectedInterval = MutableStateFlow(timeSettingsRepository.getInterval())
    val selectedInterval: StateFlow<Int> = _selectedInterval
    private val _use24HourFormat = MutableStateFlow(timeSettingsRepository.getTimeFormat())
    val use24HourFormat: StateFlow<Boolean> = _use24HourFormat

    private val _blockVersion: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val blockVersion: StateFlow<Boolean> = _blockVersion

    private val _currentVersion: MutableStateFlow<String> = MutableStateFlow("")
    val currentVersion: StateFlow<String> = _currentVersion

    private val _darkTheme: MutableStateFlow<AppTheme> = MutableStateFlow(AppTheme.SYSTEM)
    val darkTheme: StateFlow<AppTheme?> = _darkTheme

    private val _dinamicTheme: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dinamicTheme: StateFlow<Boolean> = _dinamicTheme

    private val _isExpanded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isExpanded: StateFlow<Boolean> = _isExpanded

    private val _appThemeName: MutableStateFlow<AppThemeName> =
        MutableStateFlow(AppThemeName.PURPLE)
    val appThemeName: StateFlow<AppThemeName> = _appThemeName


    fun updateSelectedInterval(interval: Int) {
        require(interval in availableIntervals) { "Intervalo no válido" }
        _selectedInterval.value = interval
        timeSettingsRepository.updateInterval(interval)
    }

    fun toggleTimeFormat() {
        stopService()
        _use24HourFormat.value = !_use24HourFormat.value
        timeSettingsRepository.updateTimeFormat(!_use24HourFormat.value)
    }

    init {
        startTimeUpdates()
        checkUserVersion()
        loadConfiguration()
    }

    private fun checkUserVersion() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                canStopApp.invoke()
            }
            _blockVersion.value = !result
        }
    }

    private fun startTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                val pattern = if (!_use24HourFormat.value) "HH:mm" else "hh:mm a"
                _currentTime.value = SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
                delay(1000) // Actualizar cada segundo
            }
        }
    }


    fun toggleService() {
        if (serviceRunning.value) {
            timeServiceUseCase.stopService()
            Toast.makeText(context, R.string.servicio_turno_off, Toast.LENGTH_SHORT).show()
        } else {
            viewModelScope.launch {
                getConfig().collect {
                    _initText.value = it.initText
                    _finalText.value = it.finalText
                }
            }
            timeServiceUseCase.startService(
                interval = selectedInterval.value,
                use24Format = use24HourFormat.value,
                wakeLockEnabled = wakeLockEnabled.value,
                iniText = initText.value,
                finalText = finalText.value
            )
            Toast.makeText(context, R.string.servicio_turno_on, Toast.LENGTH_SHORT).show()
        }
        _serviceRunning.value = !serviceRunning.value
    }

    private fun stopService() {
        if (serviceRunning.value) {
            timeServiceUseCase.stopService()
            _serviceRunning.value = !serviceRunning.value
            Toast.makeText(context, R.string.servicio_turno_off, Toast.LENGTH_SHORT).show()
        }
    }

    fun updateWakeLock(enabled: Boolean) {
        stopService()
        timeSettingsRepository.setWakeLockState(enabled)
        _wakeLockEnabled.value = enabled
    }

    private fun getConfig() = dataStore.data.map { preferences ->
        ConfigsData(
            initText = preferences[PreferencesKeys.INIT_TEXT] ?: "La hora es",
            finalText = preferences[PreferencesKeys.FINAL_TEXT] ?: "",
            appTheme = preferences[PreferencesKeys.APP_THEME_MODE]?.let { themeName ->
                AppTheme.valueOf(themeName)
            } ?: AppTheme.SYSTEM,
            appThemeName = preferences[PreferencesKeys.APP_THEME_NAME]?.let { themeName ->
                AppThemeName.valueOf(themeName)
            } ?: AppThemeName.PURPLE,
            dinamicTheme = preferences[PreferencesKeys.DINAMIC_THEME]?.toBoolean() ?: false
        )
    }
    //Config screen

    fun toggleTheme(mode: AppTheme) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.APP_THEME_MODE] = mode.name
            }
        }
        _isExpanded.value = false
    }

    fun toggleExpanded() {
        _isExpanded.value = !_isExpanded.value
    }

    fun toggleDinamicTheme(mode: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.DINAMIC_THEME] = mode.toString()
            }
        }
    }


    fun changeTheme(theme: AppThemeName) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.APP_THEME_NAME] = theme.name
            }
        }
        _appThemeName.value = theme
    }

    private fun loadConfiguration() {
        viewModelScope.launch {
            getConfig().collect {
                _initText.value = it.initText
                _darkTheme.value = it.appTheme
                _appThemeName.value = it.appThemeName
                _dinamicTheme.value = it.dinamicTheme
                _finalText.value = it.finalText
            }
        }

        _currentVersion.value = canAccesToApp.getCurrentVersionName()
    }


    fun changeInitText(text: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.INIT_TEXT] = text
            }
        }
        _initText.value = text
    }

    fun changeFinalText(text: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.FINAL_TEXT] = text
            }
        }
        _finalText.value = text
    }


    fun openAppSettings() {
        val flag = FLAG_ACTIVITY_NEW_TASK
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        intent.flags = flag
        context.startActivity(intent)

    }

    fun help() {
        // EmailContact
        val flag = FLAG_ACTIVITY_NEW_TASK

        val helpAsunto = context.getString(R.string.help_asunto) + " " +
                context.getString(R.string.app_name) + " " +
                canAccesToApp.getCurrentVersionName() + " - " +
                "Android " + Build.VERSION.RELEASE

        val mailUri = ("mailto:$EMAIL_CONTACT" +
                "?subject=" + Uri.encode(helpAsunto)).toUri()

        try {
            val intent = Intent(Intent.ACTION_SENDTO, mailUri).apply {
                flags = flag
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Manejar caso donde no hay cliente de email instalado
            Toast.makeText(context, "No se encontró una aplicación de email", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun shared() {
        //compartir la app
        val appName = context.getString(R.string.app_name)
        val packageName = context.packageName
        val playStoreLink = "https://play.google.com/store/apps/details?id=$packageName"

        val shareMessage = "¡Mira esta aplicación $appName!\n$playStoreLink"

        val sendIntent = Intent(
            Intent.ACTION_SEND
        ).apply {
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        // Solución: Agregar el flag al Intent del chooser
        val shareIntent = Intent.createChooser(sendIntent, null).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
        }

        // Usar el contexto adecuado (preferiblemente Activity context)
        context.startActivity(shareIntent)
    }

    fun reateApp() {
        val packageName = context.packageName

        try {
            // Intent para abrir directamente en Play Store
            val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "market://details?id=$packageName".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Verificamos si existe Play Store instalado
            if (context.packageManager.resolveActivity(marketIntent, 0) != null) {
                context.startActivity(marketIntent)
            } else {
                // Fallback a versión web si no hay Play Store
                val webIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            }
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        }
    }

    fun version() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                canStopApp.invoke()
            }
            if (result) Toast.makeText(
                context,
                R.string.actualizacion_no_es_necesaria,
                Toast.LENGTH_SHORT
            ).show() else Toast.makeText(
                context,
                R.string.actualizacion_necesaria,
                Toast.LENGTH_SHORT
            ).show()

            Toast.makeText(context, R.string.actualizacion_no_es_necesaria, Toast.LENGTH_SHORT)
                .show()

        }
    }

    fun privacyPolicy() {
        val urlPrivacyPolicy = "https://www.termsfeed.com/live/8c89b8ea-33cf-47e2-9c2d-3cd9aab36589"
        val intentPrivacyPolicy = Intent(Intent.ACTION_VIEW, urlPrivacyPolicy.toUri())
            .apply {
                flags = FLAG_ACTIVITY_NEW_TASK
            }
        context.startActivity(intentPrivacyPolicy)
        Toast.makeText(context, R.string.privacy_policy, Toast.LENGTH_SHORT).show()

    }
}