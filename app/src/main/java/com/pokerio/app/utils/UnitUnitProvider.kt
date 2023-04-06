package com.pokerio.app.utils

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class UnitUnitProvider : PreviewParameterProvider<() -> Unit> {
    override val values = listOf {}.asSequence()
}
