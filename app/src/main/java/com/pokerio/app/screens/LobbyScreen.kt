package com.pokerio.app.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
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
import com.pokerio.app.R
import com.pokerio.app.components.PlayerListItemView
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.PokerioLogger
import com.pokerio.app.utils.UnitUnitProvider

@Preview
@Composable
fun LobbyScreen(
    @PreviewParameter(UnitUnitProvider::class) navigateToSettings: () -> Unit
) {
    var action by remember { mutableStateOf(0) }
    val numberOfPlayers by remember(key1 = action) { mutableStateOf(GameState.players.size) }
    val funds by remember(key1 = action) { mutableStateOf(GameState.startingFunds) }
    val smallBlind by remember(key1 = action) { mutableStateOf(GameState.smallBlind) }
    val context = LocalContext.current
    val isAdmin by remember(key1 = action) { mutableStateOf(GameState.thisPlayer.isAdmin) }

    DisposableEffect(LocalLifecycleOwner.current) {
        // Sign-up for updates when a new player appears
        val joinedCallbackId = GameState.addOnPlayerJoinedCallback { action++ }
        val removedCallbackId = GameState.addOnPlayerRemovedCallback { action++ }
        val callbackSettingsId = GameState.addOnSettingsChangedCallback { action++ }

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
        Column(modifier = Modifier.weight(1f, false)) {
            TopGameSettings(numberOfPlayers, funds, smallBlind)
            PlayerList(numberOfPlayers)
        }
        BottomButtons(context, isAdmin, numberOfPlayers, navigateToSettings)
    }
}

@Composable
fun TopGameSettings(
    numberOfPlayers: Int,
    funds: Int,
    smallBlind: Int
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        SingleGameSettingView(
            tag = stringResource(id = R.string.game_code),
            value = GameState.gameID
        )
        SingleGameSettingView(
            tag = stringResource(id = R.string.players),
            value = "$numberOfPlayers/8"
        )
        SingleGameSettingView(
            tag = stringResource(id = R.string.funds),
            value = funds,
            modifier = Modifier.testTag("setting_funds")
        )
        SingleGameSettingView(
            tag = stringResource(id = R.string.small_blind),
            value = smallBlind,
            modifier = Modifier.testTag("setting_small_blind")
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayerList(
    numberOfPlayers: Int
) {
    val scrollState = rememberScrollState(0)
    val checkedPlayerCount = minOf(numberOfPlayers, GameState.players.size)

    Column(
        modifier = Modifier
            .verticalScroll(scrollState, true)
            .padding(vertical = 12.dp)
            .testTag("player_list")
    ) {
        for (i in 1..GameState.MAX_PLAYERS) {
            AnimatedVisibility(
                visible = i <= checkedPlayerCount,
                enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy)),
                exit = scaleOut(animationSpec = spring(Spring.DampingRatioMediumBouncy)),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                if (i <= checkedPlayerCount) {
                    PlayerListItemView(player = GameState.players[i - 1])
                }
            }
        }
    }
}

@Composable
fun BottomButtons(
    context: Context,
    isAdmin: Boolean,
    numberOfPlayers: Int,
    navigateToSettings: () -> Unit
) {
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
                    .testTag("start_game"),
                enabled = GameState.MIN_PLAYERS <= numberOfPlayers && numberOfPlayers <= GameState.MAX_PLAYERS
            ) {
                Text(text = stringResource(id = R.string.start_game))
            }
        }
    }
}

@Composable
fun SingleGameSettingView(
    tag: String,
    value: Any,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = tag,
            fontWeight = FontWeight.Light
        )
        Text(text = value.toString())
    }
}

private fun leaveGame(context: Context) {
    val onSuccess = {
        PokerioLogger.debug("Left game")
    }

    val onError = {
        PokerioLogger.displayMessage(context.getString(R.string.failed_leave))
    }

    GameState.launchTask {
        GameState.leaveGameRequest(onSuccess, onError)
    }
}

private fun startGame(context: Context) {
    val onSuccess = {
        PokerioLogger.debug("Started game")
    }

    val onError = {
        PokerioLogger.displayMessage(context.getString(R.string.failed_start))
    }

    GameState.launchTask {
        GameState.startGameRequest(onSuccess, onError)
    }
}
