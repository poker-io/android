package com.pokerio.app.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pokerio.app.components.CardItem
import com.pokerio.app.utils.GameState

@Preview
@Composable
fun GameScreen() {
    val context = LocalContext.current
    val orientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    val systemUiController = rememberSystemUiController()

    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation

        systemUiController.isNavigationBarVisible = false
        systemUiController.isSystemBarsVisible = false
        systemUiController.isStatusBarVisible = false
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            activity.requestedOrientation = originalOrientation

            systemUiController.isNavigationBarVisible = true
            systemUiController.isSystemBarsVisible = true
            systemUiController.isStatusBarVisible = true
            systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

    Column {
        Text(text = "Player cards:")
        Row {
            CardItem(GameState.gameCard1)
            CardItem(GameState.gameCard2)
        }
        GameState.players.forEach {
            Column {
                Text(text = it.nickname)
                Text(text = it.funds.toString())
            }
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
