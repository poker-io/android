package com.pokerio.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.pokerio.app.R
import com.pokerio.app.utils.FloatUnitProvider
import com.pokerio.app.utils.UnitUnitProvider

@Preview
@Composable
fun SettingsScreen(
    @PreviewParameter(UnitUnitProvider::class) navigateBack: () -> Unit
) {
    Column {
        TopBar(navigateBack = navigateBack)
        Text(text = stringResource(id = R.string.starting_funds))
        Text(text = stringResource(id = R.string.small_blind))
    }
}

@Preview
@Composable
private fun TopBar(
    @PreviewParameter(UnitUnitProvider::class) navigateBack: () -> Unit
) {
    Row {
        IconButton(onClick = { navigateBack() }) {
            Icon(
                Icons.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.contentDescription_navigate_back)
            )
        }
        Text(text = stringResource(id = R.string.settings))
    }
}

@Preview
@Composable
private fun Selector(
    min: Float = 0f,
    max: Float = 100f,
    initialValue: Float = 50f,
    @PreviewParameter(FloatUnitProvider::class) onValueSelected: (value: Float) -> Unit
) {
    var currentValue by remember { mutableStateOf(initialValue) }

    Column {
        Row {
            Column {
            }
            Text(text = "")
            Column() {
            }
        }
        Slider(
            value = currentValue,
            onValueChange = {
                currentValue = it
                onValueSelected(it)
            }
        )
    }
}
