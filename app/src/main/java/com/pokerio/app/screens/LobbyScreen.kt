package com.pokerio.app.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.UnitUnitProvider

@Preview
@Composable
fun LobbyScreen(
    @PreviewParameter(UnitUnitProvider::class) navigateToSettings: () -> Unit
) {
    var numberOfPlayers by remember { mutableStateOf(0) }
    val context = LocalContext.current

    DisposableEffect(LocalLifecycleOwner.current) {
        // Sign-up for updates when a new player appears
        val callbackId = GameState.addOnPlayerJoinedCallback { numberOfPlayers = GameState.players.size }
        onDispose {
            // Unregister callback when we leave the view
            GameState.removeOnPlayerJoinedCallback(callbackId)
        }
    }

    // This is a debugging UI
    Column() {
        Column(modifier = Modifier.testTag("lobby_column")) {
            Text(text = "Game code: ${GameState.gameID}")
            Text(text = "Players ($numberOfPlayers):")
            GameState.players.forEach {
                Text(text = it.nickname)
            }
        }
        Button(onClick = { navigateToSettings() }) {
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
