package com.pevalcar.lahoraes.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeSettingsRepository @Inject constructor() {


    private var currentInterval = 5
    private var use24HourFormat = true
    private var wakeLockEnabled = false
    private var initText = "La hora es"
    private var finalText = ""

    fun getInterval() = currentInterval
    fun getTimeFormat() = use24HourFormat
    fun getWakeLockState() = wakeLockEnabled
    fun getUpsetText() = initText
    fun getFinalText() = finalText

    // Setters
    fun updateInterval(interval: Int) {
        currentInterval = interval
    }

    fun updateTimeFormat(use24: Boolean) {
        use24HourFormat = use24
    }
    fun setWakeLockState(enabled: Boolean) { wakeLockEnabled = enabled }
    fun updateInitText(text: String) {
        initText = text
    }

    fun updateFinalText(text: String) {
        finalText = text
    }
}