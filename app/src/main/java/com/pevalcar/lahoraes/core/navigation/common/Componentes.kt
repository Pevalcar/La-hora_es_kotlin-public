package com.pevalcar.lahoraes.core.navigation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HelpSection(
    title: String,
    suptitle: String = "",
    icon: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ListItem(
        leadingContent = {
            icon()
        },
        headlineContent = {
            Text(text = title)
        }, supportingContent = {
            if (suptitle.isNotEmpty()) {
                Text(text = suptitle)
            }
        }, modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            })
}


@Preview(showBackground = true)
@Composable
fun HelpSectionPreview() {
    Column {
        HelpSection(title = "Help", onClick = {})
        HelpSection(title = "Help", suptitle = "Help", onClick = {})

    }
}