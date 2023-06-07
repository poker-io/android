package com.pokerio.app.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pokerio.app.R
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.PokerioLogger
import com.pokerio.app.utils.UnitUnitProvider

@Composable
fun HomeScreen(
    navigateToSettings: () -> Unit,
    navigateToLobby: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StartGameCard(navigateToLobby = navigateToLobby)
        BottomRow(navigateToSettings = navigateToSettings)
    }
}

@Preview
@Composable
private fun BottomRow(
    modifier: Modifier = Modifier,
    @PreviewParameter(UnitUnitProvider::class) navigateToSettings: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { navigateToSettings() },
            modifier = Modifier.testTag("settings_button")
        ) {
            Icon(
                Icons.Rounded.Settings,
                contentDescription = stringResource(id = R.string.contentDescription_settings_icon)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun StartGameCard(
    modifier: Modifier = Modifier,
    @PreviewParameter(UnitUnitProvider::class) navigateToLobby: () -> Unit
) {
    val context = LocalContext.current
    var gameCode by remember { mutableStateOf("") }

    val onSuccess = {
        navigateToLobby()
    }

    Column {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .testTag("start_game_card")
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                OutlinedTextField(
                    value = gameCode,
                    onValueChange = { gameCode = it },
                    label = { Text(stringResource(R.string.game_code)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Button(
                    onClick = { joinGame(context, gameCode, onSuccess) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription =
                        stringResource(id = R.string.contentDescription_join_game_button)
                    )
                }
            }
        }
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .testTag("create_game_card")
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                OutlinedButton(
                    onClick = { createGame(context, onSuccess) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(stringResource(R.string.new_game))
                }
            }
        }
    }
}

private fun joinGame(context: Context, gameCode: String, onSuccess: () -> Unit) {
    if (gameCode.isBlank()) {
        PokerioLogger.displayMessage(context.getString(R.string.error_game_code_empty))
        return
    }

    val onSuccessWrapper = {
        ContextCompat.getMainExecutor(context).execute(onSuccess)
    }

    val onError = {
        PokerioLogger.displayMessage(context.getString(R.string.failed_join))
    }

    GameState.launchTask {
        GameState.joinGameRequest(gameCode, context, onSuccessWrapper, onError)
    }
}

private fun createGame(context: Context, onSuccess: () -> Unit) {
    val onError = {
        PokerioLogger.displayMessage(context.getString(R.string.failed_create))
    }

    GameState.launchTask {
        GameState.createGameRequest(context, onSuccess, onError)
    }
}
