package com.pokerio.app.screens

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pokerio.app.R
import com.pokerio.app.components.CardView
import com.pokerio.app.components.PlayerView
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.PokerioLogger
import java.lang.NumberFormatException

@Preview
@Composable
fun GameScreen() {
    val context = LocalContext.current
    val orientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    val systemUiController = rememberSystemUiController()
    var raiseDialogOpen by remember { mutableStateOf(false) }
    var debugNumberOfActions by remember { mutableStateOf(0) }

    DisposableEffect(orientation) {
        // Set orientation
        val activity = context as Activity
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation

        // Hide system UI
        systemUiController.setSystemUiVisible(false)
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Setup callbacks
        val callbackId = GameState.addOnNewActionCallback {
            debugNumberOfActions++
        }

        onDispose {
            // Restore previous state
            activity.requestedOrientation = originalOrientation

            systemUiController.setSystemUiVisible(true)
            systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT

            GameState.removeOnNewActionCallback(callbackId)
        }
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GameState.players.forEach {
                PlayerView(it)
            }
            Column {
                Text(stringResource(R.string.winnings_pool) + ": ${GameState.winningsPool}")
                Text("DEBUG: $debugNumberOfActions")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            GameState.cards.forEach { card ->
                CardView(card)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                PlayerView(GameState.thisPlayer)
                CardView(GameState.gameCard1)
                CardView(GameState.gameCard2)
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                item {
                    Button(onClick = { raiseDialogOpen = true }) {
                        Text(stringResource(R.string.raise))
                    }
                }
                item {
                    Button(onClick = { onCheck(context) }) {
                        Text(stringResource(R.string.check) + " (${GameState.getMaxBet()})")
                    }
                }
                item {
                    Button(onClick = { onFold(context) }) {
                        Text(stringResource(R.string.fold))
                    }
                }
            }
        }
    }
    if (raiseDialogOpen) {
        RaiseDialog {
            raiseDialogOpen = false
        }
    }
}

private fun onCheck(context: Context) {
    val onSuccess = {
        PokerioLogger.debug("Check action")
    }

    val onError = {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, context.getString(R.string.check_failed), Toast.LENGTH_LONG).show()
        }
    }

    GameState.launchTask {
        GameState.actionCheckRequest(onSuccess, onError)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseDialog(
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val prevAmount = GameState.thisPlayer.bet
    val minAmount = GameState.getMaxBet() + 1
    var newAmount by remember { mutableStateOf(minAmount.toString()) }
    var amountCorrect by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = { onClose() },
        confirmButton = {
            TextButton(
                onClick = {
                    onRaise(context, newAmount.toInt())
                    onClose()
                },
                enabled = amountCorrect
            ) {
                Text(stringResource(R.string.confirm_raise))
            }
        },
        dismissButton = {
            TextButton(onClick = { onClose() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(stringResource(R.string.raise_amount)) },
        text = {
            Column {
                Text("Your previous bet: $prevAmount")
                Text("Minimum bet: $minAmount")
                OutlinedTextField(
                    value = newAmount,
                    onValueChange = {
                        try {
                            newAmount = it
                            val value = it.toInt()
                            amountCorrect = value >= minAmount
                        } catch (e: NumberFormatException) {
                            PokerioLogger.error(e.toString())
                            amountCorrect = false
                        }
                    },
                    isError = !amountCorrect,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}

private fun onRaise(context: Context, newAmount: Int) {
    val onSuccess = {
        PokerioLogger.debug("Raise action")
    }

    val onError = {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, context.getString(R.string.raise_failed), Toast.LENGTH_LONG).show()
        }
    }

    GameState.launchTask {
        GameState.actionRaiseRequest(newAmount, onSuccess, onError)
    }
}

private fun onFold(context: Context) {
    val onSuccess = {
        PokerioLogger.debug("Fold action")
    }

    val onError = {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, context.getString(R.string.fold_failed), Toast.LENGTH_LONG).show()
        }
    }

    GameState.launchTask {
        GameState.actionFoldRequest(onSuccess, onError)
    }
}

private fun SystemUiController.setSystemUiVisible(value: Boolean) {
    isNavigationBarVisible = value
    isSystemBarsVisible = value
    isStatusBarVisible = value
}
