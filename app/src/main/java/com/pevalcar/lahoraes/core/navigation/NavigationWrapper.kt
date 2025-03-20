package com.pevalcar.lahoraes.core.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pevalcar.lahoraes.presentation.config.ConfigurationScreen
import com.pevalcar.lahoraes.presentation.contacts.ContactsScreen
import com.pevalcar.lahoraes.presentation.home.HomeScreen
import com.pevalcar.lahoraes.presentation.splash.SplashScreen

@Composable
fun NavigationWrapper(
    activity: Activity
) {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
            SplashScreen {
                navController.popBackStack()

                navController.navigate(Home)
            }
        }
        composable<Home> {
            HomeScreen(
            ) {
                navController.navigate(Settings)
            }
        }
        composable<Settings> {
            ConfigurationScreen(
                activity = activity,
                navigatoToContact = {
                    navController.navigate(Contact)
                }) {
                navController.popBackStack()    // Volver a la pantalla de inicio
            }
        }

        composable<Contact> {
            ContactsScreen(navigateBack = {
                navController.popBackStack()
            })
        }
    }
}
    
    
