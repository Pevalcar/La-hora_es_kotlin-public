package com.pevalcar.lahoraes.core.common

import com.qonversion.android.sdk.dto.offerings.QOffering
import javax.inject.Inject


class QoRepo @Inject constructor() {

    private var premium = false
    private var offer: List<QOffering> = listOf()
    fun isPremium(): Boolean {
        return premium
    }

    fun setPremium(value: Boolean) {
        premium = value
    }

    fun getOffer(): List<QOffering> {
        return offer
    }

    fun setOffer(value: List<QOffering>) {
        offer = value
    }

}