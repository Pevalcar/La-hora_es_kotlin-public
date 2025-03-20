package com.pevalcar.lahoraes.data

import android.content.Context
import android.os.Build
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccesAppRepo @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    @ApplicationContext private val context: Context

) {
    private val MIN_VERSION = "min_version"

    fun getCurrentVersion(): List<Int> {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName?.split(".")?.map { it.toInt() } ?: listOf(0, 0, 0)

        } catch (e: Exception) {
            listOf(0, 0, 0)
        }
    }

    fun getCurrentVersionName(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

            val versionName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.versionName ?: ("" + "(${packageInfo.longVersionCode})")
            } else {
                packageInfo.versionName
            }
            return versionName ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun getMinAllowedVersion(): List<Int> {
        remoteConfig.fetch(0)
        remoteConfig.activate().await()
        val minVersion = remoteConfig.getString(MIN_VERSION)
        return if (minVersion.isBlank()) listOf(0, 0, 0)
        else minVersion.split(".").map { it.toInt() }


    }
}