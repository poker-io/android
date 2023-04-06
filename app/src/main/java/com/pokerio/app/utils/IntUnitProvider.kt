package com.pokerio.app.utils

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class IntUnitProvider : PreviewParameterProvider<(Int) -> Unit> {
    override val values = listOf { _: Int -> }.asSequence()
}
