package com.pokerio.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokerio.app.R
import com.pokerio.app.utils.IntUnitProvider
import com.pokerio.app.utils.UnitUnitProvider
import java.lang.Float.max
import java.lang.Float.min

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsScreen(
    @PreviewParameter(UnitUnitProvider::class) navigateBack: () -> Unit
) {
    val sectionTitleFontSize = 24.sp
    val sectionTitleFontWeight = FontWeight.Bold
    val sectionTitleModifier = Modifier.padding(10.dp)
    val spacerModifier = Modifier.padding(10.dp)

    Column {
        TopAppBar(
            title = { Text(stringResource(id = R.string.settings)) },
            navigationIcon = {
                IconButton(onClick = { navigateBack() }) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = stringResource(
                            id = R.string.contentDescription_navigate_back
                        )
                    )
                }
            }
        )
        Column(modifier = Modifier.padding(10.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "test",
                onValueChange = {},
                label = { Text(stringResource(id = R.string.nickname)) }
            )
            Spacer(modifier = spacerModifier)
            Text(
                text = stringResource(id = R.string.starting_funds),
                fontSize = sectionTitleFontSize,
                fontWeight = sectionTitleFontWeight,
                modifier = sectionTitleModifier
            )
            Selector(onValueSelected = {})
            Spacer(modifier = spacerModifier)
            Text(
                text = stringResource(id = R.string.small_blind),
                fontSize = sectionTitleFontSize,
                fontWeight = sectionTitleFontWeight,
                modifier = sectionTitleModifier
            )
            Selector(onValueSelected = {})
        }
    }
}

@Preview
@Composable
private fun Selector(
    minValue: Float = 0f,
    maxValue: Float = 100f,
    initialValue: Float = 50f,
    @PreviewParameter(IntUnitProvider::class) onValueSelected: (value: Int) -> Unit
) {
    var currentValue by remember { mutableStateOf(initialValue) }

    fun updateValue(newValue: Float) {
        currentValue = min(max(newValue, 0f), maxValue)
        onValueSelected(currentValue.toInt())
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.width(IntrinsicSize.Min)) {
                OutlinedButton(onClick = { updateValue(currentValue - 1000) }) {
                    Text(text = "-1000")
                }
                OutlinedButton(onClick = { updateValue(currentValue - 100) }) {
                    Text(text = "-100")
                }
                OutlinedButton(onClick = { updateValue(currentValue - 10) }) {
                    Text(text = "-10")
                }
            }
            Text(
                text = currentValue.toInt().toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Column(modifier = Modifier.width(IntrinsicSize.Min)) {
                OutlinedButton(onClick = { updateValue(currentValue + 1000) }) {
                    Text(text = "+1000")
                }
                OutlinedButton(onClick = { updateValue(currentValue + 100) }) {
                    Text(text = "+100")
                }
                OutlinedButton(onClick = { updateValue(currentValue + 10) }) {
                    Text(text = "+10")
                }
            }
        }
        Slider(
            value = (currentValue - minValue) / (maxValue - minValue),
            onValueChange = { updateValue(minValue + it * (maxValue - minValue)) }
        )
    }
}
