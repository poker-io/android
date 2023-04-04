package com.pokerio.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pokerio.app.utils.GameState

@Preview
@Composable
fun LobbyScreen() {
    // TODO
    Column {
        Text(text = "Game code: ${GameState.gameID}")
        Text(text = "Players:")
        GameState.players.forEach {
            Text(text = it.nickname)
        }
    }
}
