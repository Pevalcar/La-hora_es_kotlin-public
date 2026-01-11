package com.pevalcar.lahoraes.utils

import androidx.datastore.preferences.core.stringPreferencesKey

object Constats {


    const val ADMOB_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val EMAIL_CONTACT = "geomemsoluciones@gmail.com"
    const val POLITY_URL = "https://www.termsfeed.com/live/8c89b8ea-33cf-47e2-9c2d-3cd9aab36589"
    const val QOPREMIUM = "premium"
    const val QOPROYECTKEY = "D33k2-NPorfnC5aZ6SdbHCwS_DzAiElj"
    enum class AppTheme {
        SYSTEM,
        LIGHT,
        DARK
    }
    enum class AppThemeName {
        PURPLE,
        GREEN,
        BLUE,
        RED
    }
}

object PreferencesKeys {
    val DINAMIC_THEME = stringPreferencesKey("dinamic_theme")
    val APP_THEME_MODE = stringPreferencesKey("app_theme")
    val INIT_TEXT = stringPreferencesKey("init_text")
    val FINAL_TEXT = stringPreferencesKey("final_text")
    val APP_THEME_NAME = stringPreferencesKey("app_theme_name")

}