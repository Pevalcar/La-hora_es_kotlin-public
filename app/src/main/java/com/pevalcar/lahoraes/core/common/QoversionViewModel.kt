package com.pevalcar.lahoraes.core.common

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.pevalcar.lahoraes.utils.Constats.QOPREMIUM
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.dto.QonversionError
import com.qonversion.android.sdk.dto.entitlements.QEntitlement
import com.qonversion.android.sdk.dto.products.QProduct
import com.qonversion.android.sdk.listeners.QonversionEntitlementsCallback
import com.qonversion.android.sdk.listeners.QonversionProductsCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class QoversionViewModel @Inject constructor(
    private val qoRepo: QoRepo,
    private val context: Context,
) : ViewModel() {

    private val _offerings = MutableStateFlow<Map<String, QProduct>>(emptyMap())
    val offerings: StateFlow<Map<String, QProduct>> = _offerings

    private val _hasPremiumPermission = MutableStateFlow(false)
    val hasPremiumPermission: StateFlow<Boolean> = _hasPremiumPermission

    init {
        loadOfferings()
        updatePermission()
    }

    private fun loadOfferings() {
        Qonversion.shared.products(object : QonversionProductsCallback {
            override fun onError(error: QonversionError) {
                Log.e("QOVER", error.toString())
            }

            override fun onSuccess(products: Map<String, QProduct>) {
                _offerings.value = products
            }


        })
//        Qonversion.shared.userInfo(object : QonversionUserCallback {
//            override fun onSuccess(user: QUser) {
//                Log.e("QOVER_user", "user $user")
//            }
//
//            override fun onError(error: QonversionError) {
//                // handle error here
//            }
//        })
    }

    fun updatePermission() {
        Qonversion.shared.checkEntitlements(object : QonversionEntitlementsCallback {
            override fun onError(error: QonversionError) {
                Log.e("QOVER suscription", error.toString())
            }

            override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                val premiumEntitlement = entitlements[QOPREMIUM]
                Log.e(
                    "QOVER suscription",
                    "premiumEntitlement $premiumEntitlement , entitlements ${entitlements}"
                )
                _hasPremiumPermission.value =
                    premiumEntitlement != null && premiumEntitlement.isActive
            }
        })
    }

    //TODO : agregar configuraciones al tener premium
    fun purchase(activity: Activity, offering: QProduct) {

        Qonversion.shared.purchase(
            activity, offering,
            callback = object : QonversionEntitlementsCallback {
                override fun onError(error: QonversionError) {
                    Log.e("onError", "onError: Purshace error $error")
                    Toast.makeText(context, "Error : ${error.description}", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                    Toast.makeText(context, "Has premium", Toast.LENGTH_SHORT).show()
                    updatePermission()
                }
            },
        )
    }
}