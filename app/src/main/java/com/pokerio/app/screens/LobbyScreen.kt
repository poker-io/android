package com.pokerio.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.pokerio.app.utils.GameState

@Preview
@Composable
fun LobbyScreen() {
    var numberOfPlayers by remember { mutableStateOf(0) }

    // Sign-up for updates when a new player appears
    val callbackId = GameState.addOnPlayerJoinedCallback { numberOfPlayers = GameState.players.size }
    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            // Unregister callback when we leave the view
            GameState.removeOnPlayerJoinedCallback(callbackId)
        }
    }

    // This is a debugging UI
    Column(modifier = Modifier.testTag("lobby_column")) {
        Text(text = "Game code: ${GameState.gameID}")
        Text(text = "Players ($numberOfPlayers):")
        GameState.players.forEach {
            Text(text = it.nickname)
        }
    }
}
