package com.pokerio.app.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pokerio.app.R
import com.pokerio.app.components.PlayerListItem
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.UnitUnitProvider

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun LobbyScreen(
    @PreviewParameter(UnitUnitProvider::class) navigateToSettings: () -> Unit
) {
    var numberOfPlayers by remember { mutableStateOf(GameState.players.size) }
    var funds by remember { mutableStateOf(GameState.startingFunds) }
    var smallBlind by remember { mutableStateOf(GameState.smallBlind) }
    val context = LocalContext.current
    var isAdmin by remember { mutableStateOf(GameState.isPlayerAdmin) }
    val scrollState = ScrollState(0)

    DisposableEffect(LocalLifecycleOwner.current) {
        // Sign-up for updates when a new player appears
        val joinedCallbackId =
            GameState.addOnPlayerJoinedCallback {
                numberOfPlayers = GameState.players.size
                isAdmin = GameState.isPlayerAdmin
            }
        val removedCallbackId =
            GameState.addOnPlayerRemovedCallback {
                numberOfPlayers = GameState.players.size
                isAdmin = GameState.isPlayerAdmin
            }
        val callbackSettingsId =
            GameState.addOnSettingsChangedCallback {
                funds = GameState.startingFunds
                smallBlind = GameState.smallBlind
            }
        onDispose {
            // Unregister callback when we leave the view
            GameState.removeOnSettingsChangedCallback(callbackSettingsId)
            GameState.removeOnPlayerJoinedCallback(joinedCallbackId)
            GameState.removeOnPlayerRemovedCallback(removedCallbackId)
        }
    }
    BackHandler {
        // Leave the game if we're navigating back
        leaveGame(context)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
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
                        text = stringResource(id = R.string.label_game_code),
                        fontWeight = FontWeight.Light
                    )
                    Text(text = GameState.gameID)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.players),
                        fontWeight = FontWeight.Light
                    )
                    Text(text = "$numberOfPlayers/8")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.funds),
                        fontWeight = FontWeight.Light
                    )
                    Text(text = "$funds", modifier = Modifier.testTag("funds"))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.small_blind),
                        fontWeight = FontWeight.Light
                    )
                    Text(text = "$smallBlind", modifier = Modifier.testTag("small_blind"))
                }
            }
        }
        Column(
            modifier = Modifier
                .verticalScroll(scrollState, true)
                .padding(vertical = 12.dp)
                .fillMaxHeight()
                .testTag("player_list")
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
                    .testTag("leave_game")
            ) {
                Text(text = stringResource(id = R.string.leave_game))
            }
            if (isAdmin) {
                OutlinedButton(
                    onClick = { navigateToSettings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("update_settings")
                ) {
                    Text(text = stringResource(id = R.string.update_settings))
                }
                Button(
                    onClick = { startGame(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("start_game")
                ) {
                    Text(text = stringResource(id = R.string.start_game))
                }
            }
        }
    }
}

private fun leaveGame(context: Context) {
    val onSuccess = {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, "Left game", Toast.LENGTH_LONG).show()
        }
    }

    val onError = {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, "Failed to leave game", Toast.LENGTH_LONG).show()
        }
    }

    GameState.launchTask {
        GameState.leaveGameRequest(onSuccess, onError)
    }
}

private fun startGame(context: Context) {
    val onSuccess = {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, "Started Game", Toast.LENGTH_LONG).show()
        }
    }

    val onError = {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, "Failed to start game", Toast.LENGTH_LONG).show()
        }
    }

    GameState.launchTask {
        GameState.startGameRequest(onSuccess, onError)
    }
}
