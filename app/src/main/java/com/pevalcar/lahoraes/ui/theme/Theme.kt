package com.pevalcar.lahoraes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pevalcar.lahoraes.utils.Constats

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = backgroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = backgroundLight

    /* Other default colors to override
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun LaHoraEsTheme(
    viewModel: ThemedViewModel = hiltViewModel(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkThemeState by viewModel.darkTheme.collectAsState()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )
    val colorScheme = when (darkThemeState) {
        Constats.AppTheme.SYSTEM -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && dynamicColor) {
                val context = LocalContext.current
                if (isSystemInDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                    context
                )
            } else {
                if (isSystemInDarkTheme) DarkColorScheme else LightColorScheme
            }
        }
        Constats.AppTheme.LIGHT -> LightColorScheme
        Constats.AppTheme.DARK -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = shapes,
        typography = Typography,
        content = content
    )
}