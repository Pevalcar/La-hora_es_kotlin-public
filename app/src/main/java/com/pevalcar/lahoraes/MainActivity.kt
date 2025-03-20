// Clase principal de la actividad que sirve como punto de entrada de la aplicación
package com.pevalcar.lahoraes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.ads.MobileAds
import com.pevalcar.lahoraes.core.navigation.NavigationWrapper
import com.pevalcar.lahoraes.ui.LaHoraEsTheme
import dagger.hilt.android.AndroidEntryPoint

// Actividad principal que hereda de ComponentActivity (base para actividades con Compose)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Configuración del contenido usando Jetpack Compose
        setContent {
            // Aplicación del tema personalizado a toda la UI
            LaHoraEsTheme(
            ) {
                // Componente raíz de la aplicación
                NavigationWrapper(
                    activity = this@MainActivity
                )
            }
            MobileAds.initialize(this) {}
        }
    }
}

