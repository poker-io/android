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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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

@Preview
@Composable
fun GameScreen() {
    val context = LocalContext.current
    val orientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    val systemUiController = rememberSystemUiController()

    DisposableEffect(orientation) {
        // Set orientation
        val activity = context as Activity
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation

        // Hide system UI
        systemUiController.setSystemUiVisible(false)
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            // Restore previous state
            activity.requestedOrientation = originalOrientation

            systemUiController.setSystemUiVisible(true)
            systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
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
                Text(stringResource(R.string.winnings_pool))
                Text(GameState.winningsPool.toString())
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
                    Button(onClick = { onCall(context) }) {
                        Text(stringResource(R.string.call))
                    }
                }
                item {
                    Button(onClick = { onRaise(context) }) {
                        Text(stringResource(R.string.raise))
                    }
                }
                item {
                    Button(onClick = { onCheck(context) }) {
                        Text(stringResource(R.string.check))
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
}

private fun onCall(context: Context) {
    val onSuccess = {
        PokerioLogger.debug("Call action")
    }

    val onError = {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, context.getString(R.string.call_failed), Toast.LENGTH_LONG).show()
        }
    }

    GameState.launchTask {
        GameState.actionCallRequest(onSuccess, onError)
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

private fun onRaise(context: Context) {
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
