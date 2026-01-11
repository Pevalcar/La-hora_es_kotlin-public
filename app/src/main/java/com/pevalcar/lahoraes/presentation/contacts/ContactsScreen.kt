package com.pevalcar.lahoraes.presentation.contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pevalcar.lahoraes.R
import com.pevalcar.lahoraes.core.common.HelpSection
import com.pevalcar.lahoraes.utils.Constats.EMAIL_CONTACT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.contactos))
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { inisPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inisPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            HelpSection(
                "Store", "Pagina de la aplicacion",
                icon = {
                    Icon(painterResource(R.drawable.store), contentDescription = "Store")
                }
            ) {
                viewModel.store()
            }
            HelpSection(
                "E-mail", "Contacto con el desarrollador: $EMAIL_CONTACT",
                //Email icon
                icon = {
                    Icon(Icons.Filled.Email, contentDescription = "Contact us")
                }
            )
            {
                viewModel.email()
            }
            HelpSection(
                "X", "Contar con el desarrollador: @pevalcar",
                //Twitter icon
                icon = {
                    Icon(painterResource(R.drawable.twitter_x), contentDescription = "Twitter")
                }
            )
            {
                viewModel.twitter()
            }

            HelpSection(
                "Github", "Contar con el desarrollador: @pevalcar",
                //Twitter icon
                icon = {
                    Icon(painterResource(R.drawable.github), contentDescription = "Github")
                }
            ) {
                viewModel.github()
            }


        }
    }

}
