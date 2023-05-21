package com.pokerio.app.screens

import android.app.Activity
import android.content.pm.ActivityInfo
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pokerio.app.components.CardView
import com.pokerio.app.components.PlayerView
import com.pokerio.app.utils.GameState

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
                Text("Winnings pool:")
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
                    Button(onClick = { /*TODO*/ }) {
                        Text("Call")
                    }
                }
                item {
                    Button(onClick = { /*TODO*/ }) {
                        Text("Raise")
                    }
                }
                item {
                    Button(onClick = { /*TODO*/ }) {
                        Text("Check")
                    }
                }
                item {
                    Button(onClick = { /*TODO*/ }) {
                        Text("Fold")
                    }
                }
            }
        }
    }
}

private fun SystemUiController.setSystemUiVisible(value: Boolean) {
    isNavigationBarVisible = value
    isSystemBarsVisible = value
    isStatusBarVisible = value
}
