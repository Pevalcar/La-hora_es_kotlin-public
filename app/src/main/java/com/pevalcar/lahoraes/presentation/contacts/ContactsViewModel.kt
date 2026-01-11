package com.pevalcar.lahoraes.presentation.contacts

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.pevalcar.lahoraes.R
import com.pevalcar.lahoraes.data.AccesAppRepo
import com.pevalcar.lahoraes.utils.Constats.EMAIL_CONTACT
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val canAccesToApp: AccesAppRepo,
) : ViewModel() {


    fun store() {
        val playStoreLink = "https://play.google.com/store/apps/details?id=${context.packageName}"
        val intent = Intent(Intent.ACTION_VIEW, playStoreLink.toUri())
            .apply {
                flags = FLAG_ACTIVITY_NEW_TASK
            }

        context.startActivity(intent)
    }

    fun email() {
        val flag = FLAG_ACTIVITY_NEW_TASK
        val helpAsunto = context.getString(R.string.app_name) + " " +
                canAccesToApp.getCurrentVersionName() + " - " +
                "Android " + Build.VERSION.RELEASE
        val mailUri = ("mailto:${EMAIL_CONTACT}" +
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

    fun twitter() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = "https://twitter.com/pevalcar".toUri()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun github() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = "https://github.com/pevalcar".toUri()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

}
