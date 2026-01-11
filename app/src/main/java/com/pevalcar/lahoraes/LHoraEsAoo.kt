package com.pevalcar.lahoraes

import android.app.Application
import com.pevalcar.lahoraes.utils.Constats.QOPROYECTKEY
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.QonversionConfig
import com.qonversion.android.sdk.dto.QLaunchMode
import com.qonversion.android.sdk.dto.entitlements.QEntitlementsCacheLifetime
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LHoraEsAoo : Application() {

    override fun onCreate() {
        super.onCreate()
        val qonversionConfig = QonversionConfig.Builder(
            this,
            QOPROYECTKEY,
            QLaunchMode.SubscriptionManagement
        )
            .setEntitlementsCacheLifetime(QEntitlementsCacheLifetime.Year)
            .build()
        Qonversion.initialize(qonversionConfig)
    }
}