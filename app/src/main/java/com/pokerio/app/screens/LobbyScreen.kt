package com.pokerio.app.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokerio.app.components.PlayerListItem
import com.pokerio.app.utils.GameState

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun LobbyScreen() {
    var numberOfPlayers by remember { mutableStateOf(GameState.players.size) }
    val context = LocalContext.current

    // Sign-up for updates when a new player appears
    val callbackId =
        GameState.addOnPlayerJoinedCallback { numberOfPlayers = GameState.players.size }
    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            // Unregister callback when we leave the view
            GameState.removeOnPlayerJoinedCallback(callbackId)
        }
    }

    val scrollState = ScrollState(0)

    // This is a debugging UI
    Column {
        Text(text = "Game code: ${GameState.gameID}")
        Text(text = "Players: $numberOfPlayers/8")
        Column(
            modifier = Modifier
                .testTag("lobby_column")
                .verticalScroll(scrollState, true)
                .padding(horizontal = 12.dp)
        ) {
            GameState.players.forEach {
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn()
                ) {
                    PlayerListItem(player = it)
                }
            }
        }
        Button(onClick = { updateGameSettings(context) }) {
            Text(text = "Update game settings")
        }
        Button(onClick = { startGame(context) }) {
            Text(text = "Start game")
        }
    }
}

private fun updateGameSettings(context: Context) {
    Toast.makeText(context, "TODO: Update settings", Toast.LENGTH_LONG).show()
}

private fun startGame(context: Context) {
    Toast.makeText(context, "TODO: Start game", Toast.LENGTH_LONG).show()
}
