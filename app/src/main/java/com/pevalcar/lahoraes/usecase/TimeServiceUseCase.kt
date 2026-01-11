package com.pevalcar.lahoraes.usecase

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.pevalcar.lahoraes.utils.TimeService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeServiceUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun startService(
        interval: Int,
        use24Format: Boolean,
        wakeLockEnabled: Boolean,
        iniText: String,
        finalText: String
    ) {

        val serviceIntent = Intent(context, TimeService::class.java).apply {
            putExtra("interval", interval)
            putExtra("use24Format", use24Format)
            putExtra("wakeLock", wakeLockEnabled)
            putExtra("initText", iniText)
            putExtra("finalText", finalText)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    fun stopService() {
        context.stopService(Intent(context, TimeService::class.java))
    }
}