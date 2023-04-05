package com.pokerio.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import com.pokerio.app.utils.GameState

@Preview
@Composable
fun LobbyScreen() {
    var numberOfPlayers by remember { mutableStateOf(0) }

    val callbackId = GameState.addOnPlayerJoinedCallback { numberOfPlayers = GameState.players.size }
    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            GameState.removeOnPlayerJoinedCallback(callbackId)
        }
    }

    Column {
        Text(text = "Game code: ${GameState.gameID}")
        Text(text = "Players ($numberOfPlayers):")
        GameState.players.forEach {
            Text(text = it.nickname)
        }
    }
}
