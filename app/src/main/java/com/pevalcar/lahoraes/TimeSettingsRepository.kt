package com.pevalcar.lahoraes

import javax.inject.Inject

class TimeSettingsRepository @Inject constructor() {
    private var currentInterval = 5
    private var use24HourFormat = true
    private var wakeLockEnabled = false

    fun getInterval() = currentInterval
    fun getTimeFormat() = use24HourFormat

    fun updateInterval(interval: Int) {
        currentInterval = interval
    }

    fun updateTimeFormat(use24: Boolean) {
        use24HourFormat = use24
    }
    fun getWakeLockState() = wakeLockEnabled
    fun setWakeLockState(enabled: Boolean) { wakeLockEnabled = enabled }
}