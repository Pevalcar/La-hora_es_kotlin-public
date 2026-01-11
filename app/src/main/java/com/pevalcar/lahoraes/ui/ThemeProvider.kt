package com.pevalcar.lahoraes.ui


import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pevalcar.lahoraes.GlobalViewModel
import com.pevalcar.lahoraes.ui.theme.blue.BluedarkScheme
import com.pevalcar.lahoraes.ui.theme.blue.BluelightScheme
import com.pevalcar.lahoraes.ui.theme.green.GreendarkScheme
import com.pevalcar.lahoraes.ui.theme.green.GreenlightScheme
import com.pevalcar.lahoraes.ui.theme.purple.PurpledarkScheme
import com.pevalcar.lahoraes.ui.theme.purple.PurplelightScheme
import com.pevalcar.lahoraes.ui.theme.red.ReddarkScheme
import com.pevalcar.lahoraes.ui.theme.red.RedlightScheme
import com.pevalcar.lahoraes.utils.Constats.AppTheme
import com.pevalcar.lahoraes.utils.Constats.AppTheme.DARK
import com.pevalcar.lahoraes.utils.Constats.AppTheme.SYSTEM
import com.pevalcar.lahoraes.utils.Constats.AppThemeName
import com.pevalcar.lahoraes.utils.Constats.AppThemeName.BLUE
import com.pevalcar.lahoraes.utils.Constats.AppThemeName.GREEN
import com.pevalcar.lahoraes.utils.Constats.AppThemeName.PURPLE
import com.pevalcar.lahoraes.utils.Constats.AppThemeName.RED

@Composable
fun LaHoraEsTheme(
    viewModel: GlobalViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val darkThemeState by viewModel.darkTheme.collectAsState()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val appNAme by viewModel.appThemeName.collectAsState()
    val dynamicColor by viewModel.dinamicTheme.collectAsState()

    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )
    val colorScheme = getcolorScheme(
        darkThemeState = darkThemeState ?: SYSTEM,
        isSystemInDarkTheme = isSystemInDarkTheme,
        dynamicColor = dynamicColor,
        context = LocalContext.current,
        appNAme = appNAme
    )

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = shapes,
        typography = AppTypography,
        content = content
    )
}


private fun getcolorScheme(
    darkThemeState: AppTheme,
    isSystemInDarkTheme: Boolean,
    dynamicColor: Boolean,
    context: Context,
    appNAme: AppThemeName
): ColorScheme {

    val dynamicColore = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && dynamicColor

    return when (darkThemeState) {
        SYSTEM -> {
            if (dynamicColore) {
                if (isSystemInDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                    context
                )
            } else {
                if (isSystemInDarkTheme) darkColorScheme(appNAme) else lightColorScheme(appNAme)
            }
        }

        DARK -> if (dynamicColore) dynamicDarkColorScheme(context) else darkColorScheme(appNAme)
        else -> {
            if (dynamicColore) dynamicLightColorScheme(context) else lightColorScheme(appNAme)
        }
    }
}

private fun lightColorScheme(appNAme: AppThemeName): ColorScheme = when (appNAme) {
    PURPLE -> PurplelightScheme
    GREEN -> GreenlightScheme
    BLUE -> BluelightScheme
    RED -> RedlightScheme
}

private fun darkColorScheme(appNAme: AppThemeName): ColorScheme = when (appNAme) {
    PURPLE -> PurpledarkScheme
    GREEN -> GreendarkScheme
    BLUE -> BluedarkScheme
    RED -> ReddarkScheme
}


fun getColorTheme(appNAme: AppThemeName, darkMode: Boolean): Array<Color> = when (appNAme) {
    PURPLE -> if (darkMode) arrayOf(
        PurplelightScheme.primary,
        PurplelightScheme.secondary,
        PurplelightScheme.tertiary
    ) else arrayOf(PurpledarkScheme.primary, PurpledarkScheme.secondary, PurpledarkScheme.tertiary)

    GREEN -> if (darkMode) arrayOf(
        GreenlightScheme.primary,
        GreenlightScheme.secondary,
        GreenlightScheme.tertiary
    ) else arrayOf(GreendarkScheme.primary, GreendarkScheme.secondary, GreendarkScheme.tertiary)

    BLUE -> if (darkMode) arrayOf(
        BluelightScheme.primary,
        BluelightScheme.secondary,
        BluelightScheme.tertiary
    ) else arrayOf(BluedarkScheme.primary, BluedarkScheme.secondary, BluedarkScheme.tertiary)

    RED -> if (darkMode) arrayOf(
        RedlightScheme.primary,
        RedlightScheme.secondary,
        RedlightScheme.tertiary
    ) else arrayOf(ReddarkScheme.primary, ReddarkScheme.secondary, ReddarkScheme.tertiary)
}