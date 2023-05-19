package com.pokerio.app.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pokerio.app.components.CardView
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

    Column {
        Text(text = "Player cards:")
        Row {
            CardView(GameState.gameCard1)
            CardView(GameState.gameCard2)
        }
        GameState.players.forEach {
            Column {
                Text(text = it.nickname)
                Text(text = it.funds.toString())
            }
        }
    }
}

private fun SystemUiController.setSystemUiVisible(value: Boolean) {
    isNavigationBarVisible = value
    isSystemBarsVisible = value
    isStatusBarVisible = value
}
