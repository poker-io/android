package com.pokerio.app.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
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
    var isAdmin by remember { mutableStateOf(GameState.isPlayerAdmin) }

    // Sign-up for updates when a new player appears
    val callbackId =
        GameState.addOnPlayerJoinedCallback {
            numberOfPlayers = GameState.players.size
            isAdmin = GameState.isPlayerAdmin
        }
    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            // Unregister callback when we leave the view
            GameState.removeOnPlayerJoinedCallback(callbackId)
        }
    }

    val scrollState = ScrollState(0)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Game code",
                        fontWeight = FontWeight.Light
                    )
                    Text(text = GameState.gameID)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Players",
                        fontWeight = FontWeight.Light
                    )
                    Text(text = "$numberOfPlayers/8")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Funds",
                        fontWeight = FontWeight.Light
                    )
                    Text(text = GameState.startingFunds.toString())
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Small blind",
                        fontWeight = FontWeight.Light
                    )
                    Text(text = GameState.smallBlind.toString())
                }
            }
        }
        Column(
            modifier = Modifier
                .verticalScroll(scrollState, true)
                .padding(vertical = 12.dp)
                .fillMaxHeight()
        ) {
            (1..8).forEach {
                AnimatedVisibility(
                    visible = it <= numberOfPlayers,
                    enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)),
                    exit = scaleOut(),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    if (it <= numberOfPlayers) {
                        PlayerListItem(player = GameState.players[it - 1])
                    }
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                onClick = { leaveGame(context) },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Leave game")
            }
            if (isAdmin) {
                OutlinedButton(
                    onClick = { updateGameSettings(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Update settings")
                }
                Button(
                    onClick = { startGame(context) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Start game")
                }
            }
        }
    }
}

private fun leaveGame(context: Context) {
    Toast.makeText(context, "TODO: Leave game", Toast.LENGTH_LONG).show()
}

private fun updateGameSettings(context: Context) {
    Toast.makeText(context, "TODO: Update settings", Toast.LENGTH_LONG).show()
}

private fun startGame(context: Context) {
    Toast.makeText(context, "TODO: Start game", Toast.LENGTH_LONG).show()
}
