package com.pokerio.app.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ViewOnlySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier
) {
    val sliderColors = SliderDefaults.colors(
        disabledThumbColor = MaterialTheme.colorScheme.primary,
        disabledActiveTrackColor = MaterialTheme.colorScheme.primary
    )

    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = false,
        colors = sliderColors
    )
}
