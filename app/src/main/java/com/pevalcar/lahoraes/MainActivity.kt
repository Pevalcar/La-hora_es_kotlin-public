// Clase principal de la actividad que sirve como punto de entrada de la aplicación
package com.pevalcar.lahoraes

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.pevalcar.lahoraes.ui.theme.LaHoraEsTheme
import com.pevalcar.lahoraes.ui.theme.ThemedViewModel
import com.pevalcar.lahoraes.utils.Constats
import com.pevalcar.lahoraes.utils.Constats.ADMOB_BANNER_ID
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                TimeAnnouncerApp()
            }
            MobileAds.initialize(this) {}
        }
    }
}

// Componente principal de la interfaz de usuario con Compose
@Composable
fun TimeAnnouncerApp(
    viewModel: TimeAnnouncerViewModel = hiltViewModel()
) {
    // Obtiene el contexto actual para operaciones con Android
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Manejar el caso en que el usuario denegó el permiso
            // Puedes mostrar un mensaje al usuario o realizar otras acciones
            // Por ejemplo, mostar una alert recomendando activar el permiso y al aceptarlo enviar a la configuracionde la app

            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )
            )
        }
    }
    val blockVersion by viewModel.blockVersion.collectAsState()

    if (blockVersion) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.nueva_actualizacion),
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.por_favor_actualiza_la_app),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {
                        naviteToMarket(context)
                    }) {
                        Text(text = stringResource(R.string.actualizar))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }


    // Solicitar permiso cuando sea necesario
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        LaunchedEffect(Unit) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    // Diseño principal en columna vertical
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector de formato horario

            TimeFormatSelector(viewModel)

            // Reloj grande
            DigitalClockDisplay(viewModel, context)

            // Selector de intervalos
            IntervalSelector(viewModel)

            // Controles de servicio
            ServiceControls(viewModel, context)

            // Admob BAnner
            Spacer(modifier = Modifier.weight(1f))
            AdMobBanner()
        }
    }
}

@Composable
fun DarkModeSelector(viewModel: ThemedViewModel = hiltViewModel()) {
    val thememode by viewModel.darkTheme.collectAsState()
    val expanded by viewModel.isExpanded.collectAsState()

    Box(
        modifier = Modifier
            .padding(start = 15.dp)
            .wrapContentSize(align = Alignment.TopStart),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                viewModel.toggleExpanded()
            }
        ) {
            Icon(
                painter = painterResource(
                    when (thememode) {
                        Constats.AppTheme.SYSTEM -> R.drawable.phone_mode
                        Constats.AppTheme.LIGHT -> R.drawable.ligth_mode
                        Constats.AppTheme.DARK -> R.drawable.dark_mode
                        else -> R.drawable.phone_mode
                    }
                ),
                contentDescription = "Open Menu"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { viewModel.toggleExpanded() },
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.sistema))
                },
                onClick = {
                    viewModel.toggleTheme(Constats.AppTheme.SYSTEM)
                },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.phone_mode),
                        contentDescription = "Phone",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.dark))
                },
                onClick = {
                    viewModel.toggleTheme(Constats.AppTheme.DARK)
                },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.dark_mode),
                        contentDescription = "Dark",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.ligth))
                },
                onClick = {
                    viewModel.toggleTheme(Constats.AppTheme.LIGHT)
                },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ligth_mode),
                        contentDescription = "Light",
                        tint = Color.Yellow
                    )


                }
            )
        }
    }
}

@Composable
fun AdMobBanner() {
    val addRequest = AdRequest.Builder().build()
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = {
            AdView(it).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = ADMOB_BANNER_ID
                loadAd(addRequest)
            }

        },
        update = {
            it.loadAd(addRequest)
        })

}

@Composable
private fun IntervalSelector(viewModel: TimeAnnouncerViewModel) {
    val selectedInterval by viewModel.selectedInterval.collectAsState()

    Column {
        Text(
            stringResource(R.string.intervalo_de_anuncio),
            style = MaterialTheme.typography.titleSmall
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.availableIntervals) { interval ->
                IntervalButton(
                    interval = interval,
                    isSelected = interval == selectedInterval,
                    onSelect = { viewModel.updateSelectedInterval(interval) }
                )
            }
        }
    }
}

@Composable
private fun IntervalButton(interval: Int, isSelected: Boolean, onSelect: () -> Unit) {
    Button(
        onClick = onSelect,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.height(50.dp)
    ) {
        Text(
            text = when (interval) {
                1 -> stringResource(R.string._1_minuto)
                60 -> stringResource(R.string._1_hora)
                else -> ("$interval${stringResource(R.string.minutos)}")
            },
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DigitalClockDisplay(viewModel: TimeAnnouncerViewModel, context: Context) {
    val currentTime by viewModel.currentTime.collectAsState()
    val use24hFormat by viewModel.use24HourFormat.collectAsState()
    var tts: TextToSpeech? = null
    val pattern = if (!use24hFormat) "HH:mm" else "hh:mm a"
    val formattedTime = remember(currentTime) {
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
    }

    // envolvermos el text en un censor de clic para activar el anuncio


    Surface(
        onClick = {
            val currentTime = SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.getDefault()
                    tts?.speak(
                        (context.getString(R.string.son_las) + currentTime),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
            }

        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp
    ) {


        Text(

            text = formattedTime,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TimeFormatSelector(viewModel: TimeAnnouncerViewModel) {
    val use24hFormat by viewModel.use24HourFormat.collectAsState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        DarkModeSelector()
        Spacer(modifier = Modifier.weight(1f))
        Text(stringResource(R.string._24_horas), style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = use24hFormat,
            onCheckedChange = { viewModel.toggleTimeFormat() }
        )
        Text(stringResource(R.string._12_horas), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ServiceControls(
    viewModel: TimeAnnouncerViewModel,
    context: Context
) {
    val serviceRunning by viewModel.serviceRunning.collectAsState()
    val wakeLockEnabled by viewModel.wakeLockEnabled.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Botón principal de control del servicio
        Button(
            onClick = {
                viewModel.toggleService()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (serviceRunning) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (serviceRunning) stringResource(R.string.detener_servicio)
                else stringResource(R.string.iniciar_servicio),
                style = MaterialTheme.typography.bodyLarge

            )
        }

        // Control de WakeLock
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.mantener_dispositivo_activo),
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = wakeLockEnabled,
                onCheckedChange = {
                    viewModel.updateWakeLock(it)

                }
            )
        }
    }
}

fun naviteToMarket(context: Context) {
    val appPackage = context.packageName
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$appPackage")
            )
        )
    } catch (e: ActivityNotFoundException) {
        Log.e("Pevalcar-naviteToMarket", "${e.message}")
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackage")
            )
        )
    }
}