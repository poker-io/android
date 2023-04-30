package com.pokerio.app.utils

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class PlayerProvider : PreviewParameterProvider<Player> {
    override val values = listOf(
        Player(
            nickname = "Test player",
            playerID = "933fd63d73154afff094c115013fb7de168c451f6bd306bd1573f47f5ca43dda"
        )
    ).asSequence()
}
