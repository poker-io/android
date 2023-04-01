package com.pokerio.app.utils

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class FloatUnitProvider : PreviewParameterProvider<(Float) -> Unit> {
    override val values = listOf { _: Float -> }.asSequence()
}
