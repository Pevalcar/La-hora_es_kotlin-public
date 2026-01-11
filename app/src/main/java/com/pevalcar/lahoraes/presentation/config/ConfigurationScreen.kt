package com.pevalcar.lahoraes.presentation.config

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pevalcar.lahoraes.GlobalViewModel
import com.pevalcar.lahoraes.R
import com.pevalcar.lahoraes.core.common.HelpSection
import com.pevalcar.lahoraes.core.common.QoversionViewModel
import com.pevalcar.lahoraes.ui.getColorTheme
import com.pevalcar.lahoraes.utils.Constats
import com.pevalcar.lahoraes.utils.Constats.AppThemeName

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ConfigurationScreen(
    activity: Activity,
    viewModel: GlobalViewModel = hiltViewModel(),
    viewemodelQo: QoversionViewModel = hiltViewModel(),
    navigatoToContact: () -> Unit,
    navigateToHome: () -> Unit
) {


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.title_activity_config))
                },
                navigationIcon = {
                    IconButton(onClick = navigateToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }) { inisPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inisPadding)
        ) {
            LazyColumn(
                flingBehavior = ScrollableDefaults.flingBehavior(),
                state = rememberLazyListState(),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    StikyTilte(stringResource(R.string.config_theme_title))
                }
                item {
                    ConfigureThemeColor(viewModel)
                }
                item {
                    StikyTilte(stringResource(R.string.config_text_title))
                }
                item {
                    TextConfiguration(viewModel)
                }
                item {
                    StikyTilte(stringResource(R.string.config_battery_title))
                }
                item {
                    BatteryConfiguration() {
                        viewModel.openAppSettings()
                    }
                }
                item {
                    StikyTilte(stringResource(R.string.config_partnertitle))
                }
                item {
                    PartnerConfiguration(viewModel, viewemodelQo, activity)
                }
                item {
                    StikyTilte(stringResource(R.string.config_informacion_title))
                }
                item {
                    InformationConfiguration(viewModel, navigatoToContact)
                }
            }
        }
    }
}

@Composable
fun InformationConfiguration(viewModel: GlobalViewModel, navigateToContact: () -> Unit) {
    val currentVersion = viewModel.currentVersion.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        HelpSection(
            title = stringResource(R.string.contact_us),
            icon = {
                Icon(painterResource(id = R.drawable.conteact), contentDescription = "Contact us")
            }
        ) {
            navigateToContact()
        }
        HelpSection(
            title = stringResource(R.string.privacy_policy),
            icon = {
                Icon(
                    painterResource(id = R.drawable.secure_icon),
                    contentDescription = "Privacy policy"
                )
            }

        )
        {
            viewModel.privacyPolicy()
        }
        //version
        HelpSection(
            title = stringResource(R.string.version),
            suptitle = currentVersion.value,
            icon = {
                Icon(painterResource(id = R.drawable.update), contentDescription = "Version")
            }
        ) {
            viewModel.version()
        }

    }


}

@Composable
fun PartnerConfiguration(
    viewModel: GlobalViewModel,
    viewmodelQo: QoversionViewModel,
    activity: Activity
) {
    val offerings by viewmodelQo.offerings.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {

        HelpSection(title = stringResource(R.string.suscribe_app), icon = {
            Icon(Icons.Filled.Star, contentDescription = "Partner app")
        }) {
            val offering = offerings.firstNotNullOfOrNull { it.value }
            if (offering != null) {
                viewmodelQo.purchase(
                    activity = activity,
                    offering = offering
                )
            } else {
                // Manejar el caso en el que no hay ofertas disponibles
                Log.e("PurchaseError", "No valid offerings found")
            }
        }
        HelpSection(
            title = stringResource(R.string.helpus),
            icon = {
                Icon(
                    painterResource(id = R.drawable.baseline_contact_support_24),
                    contentDescription = "Help us"
                )
            }
        ) {
            viewModel.help()
        }
        HelpSection(
            title = stringResource(id = R.string.shared_app),
            suptitle = stringResource(id = R.string.share_app_suptitle),
            icon = {
                Icon(Icons.Filled.Share, contentDescription = "Share app")
            }
        ) {

            viewModel.shared()

        }
        HelpSection(
            title = stringResource(id = R.string.rate_app),
            suptitle = stringResource(id = R.string.rate_app_suptitle),
            icon = {
                Icon(Icons.Filled.Favorite, contentDescription = "Rate app")
            }
        ) {
            viewModel.reateApp()
        }

    }
}

//TODO : agregar mejor explicacion modal con contexto y las instrucciones
@Preview(showBackground = true)
@Composable
fun BatteryConfiguration(onClick: () -> Unit = {}) {
    // Button open App Settings

    Column {
        Text(
            text = stringResource(R.string.battery_configuration),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.bateria_configuration_text),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row {
                //Flecha que apunta a la configuracion
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Open App Settings",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Open App Settings",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun StikyTilte(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(50.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun TextConfiguration(viewModel: GlobalViewModel) {
    val initText by viewModel.initText.collectAsState()
    val finalText by viewModel.finalText.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        RowSection(
            title = stringResource(R.string.config_text_init),
            click = {
            }
        ) {
            TextField(
                value = initText,
                onValueChange = { viewModel.changeInitText(it) },
                Modifier.width(200.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        RowSection(
            title = stringResource(R.string.config_text_final),
            click = {
            }
        ) {
            TextField(
                value = finalText, onValueChange =
                    { viewModel.changeFinalText(it) },
                Modifier.width(200.dp)
            )
        }
    }
}


@Composable
fun ConfigureThemeColor(viewModel: GlobalViewModel) {
    val themeMode by viewModel.appThemeName.collectAsState()
    val darkTheme by viewModel.darkTheme.collectAsState()
    val dinamicTheme by viewModel.dinamicTheme.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Column {
        DarkModeSelector(viewModel)
        Spacer(modifier = Modifier.height(8.dp))
        RowSection(
            title = stringResource(R.string.dinamico_mode),
            click = {}
        ) {
            Checkbox(checked = dinamicTheme, onCheckedChange = { viewModel.toggleDinamicTheme(it) })
        }
        Spacer(modifier = Modifier.height(8.dp))
        RowSection(
            title = stringResource(R.string.color_tema),
            disable = dinamicTheme,
            click = {
                if (!dinamicTheme) {
                    expanded = true
                }
            }
        ) {

            Box(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .wrapContentSize(align = Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {

                ColorsShow(
                    colors = getColorTheme(
                        appNAme = themeMode,
                        darkMode = darkTheme == Constats.AppTheme.DARK
                    ), disable = dinamicTheme,
                    onClick = {
                        if (!dinamicTheme) {
                            expanded = true
                        }
                    }
                )

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    AppThemeName.entries.forEach { theme ->
                        ColorsShow(
                            colors = getColorTheme(
                                appNAme = theme,
                                darkMode = darkTheme == Constats.AppTheme.DARK
                            ),
                            modifier = Modifier.padding(horizontal = 15.dp),
                            onClick = {
                                viewModel.changeTheme(theme)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorsShow(
    colors: Array<Color>,
    modifier: Modifier = Modifier,
    disable: Boolean = false,
    onClick: () -> Unit = {},
) {

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        enabled = !disable
    ) {
        Row {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .width(25.dp)
                        .height(25.dp)
                        .background(color = color.copy(alpha = if (disable) 0.5f else 1f))
                )
            }
        }
    }
}

@Composable
fun DarkModeSelector(viewModel: GlobalViewModel) {
    val thememode by viewModel.darkTheme.collectAsState()
    val expanded by viewModel.isExpanded.collectAsState()

    RowSection(
        title = stringResource(R.string.dark_mode),
        click = {
            viewModel.toggleExpanded()
        }
    ) {
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
}

@Composable
fun RowSection(
    title: String,
    disable: Boolean = false,
    click: () -> Unit?,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clickable {
                click()
            }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (disable) Color.Gray else Color.Unspecified
        )
        Spacer(modifier = Modifier.width(24.dp))
        content()
    }
}