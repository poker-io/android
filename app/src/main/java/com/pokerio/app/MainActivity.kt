package com.pokerio.app

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pokerio.app.screens.Game
import com.pokerio.app.screens.HomeScreen
import com.pokerio.app.screens.InitialSetupScreen
import com.pokerio.app.screens.LobbyScreen
import com.pokerio.app.screens.SettingsScreen
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.ThemeUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Dark theme is disabled for now
        val useDarkTheme = false

        setContent {
            AppTheme(useDarkTheme) {
                MainActivityComposable(useDarkTheme)
            }
        }
    }
}

@Composable
fun MainActivityComposable(
    useDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Transparent,
        darkIcons = !useDarkTheme
    )

    val navController = rememberNavController()
    val context = LocalContext.current

    val navigateToSettings = {
        ContextCompat.getMainExecutor(context).execute {
            navController.navigate("settings")
        }
    }
    val navigateBack = {
        ContextCompat.getMainExecutor(context).execute {
            navController.popBackStack()
        }
    }
    val exitInitialSetup = {
        ContextCompat.getMainExecutor(context).execute {
            navController.popBackStack()
            navController.navigate("home")
        }
    }
    val navigateToLobby = {
        ContextCompat.getMainExecutor(context).execute {
            navController.navigate("lobby")
        }
    }

    GameState.resetGameState()
    GameState.onGameReset = {
        ContextCompat.getMainExecutor(context).execute {
            navController.popBackStack("home", inclusive = false)
        }
    }
    GameState.onGameStart = {
        ContextCompat.getMainExecutor(context).execute {
            navController.popBackStack()
            navController.navigate("game")
        }
    }

    // Check if user had already set a nickname
    val sharedPreferences = LocalContext.current.getSharedPreferences(
        stringResource(id = R.string.shared_preferences_file),
        Context.MODE_PRIVATE
    )
    val nicknameSet = (
        sharedPreferences.getString(
            stringResource(id = R.string.sharedPreferences_nickname),
            ""
        ) ?: ""
        ).isNotBlank()
    val startDestination =
        if (nicknameSet) {
            "home"
        } else {
            "initialSetup"
        }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") {
            HomeScreen(
                navigateToSettings = navigateToSettings,
                navigateToLobby = navigateToLobby
            )
        }

        composable("settings") { SettingsScreen(navigateBack = navigateBack) }
        composable("initialSetup") { InitialSetupScreen(exitInitialSetup = { exitInitialSetup() }) }
        composable("lobby") { LobbyScreen(navigateToSettings = navigateToSettings) }
        composable("game") { Game() }
    }
}

@Composable
private fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ThemeUtils.lightColorScheme,
        content = content
    )
}
