package com.pokerio.app

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
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
import com.pokerio.app.screens.GameScreen
import com.pokerio.app.screens.HomeScreen
import com.pokerio.app.screens.InitialSetupScreen
import com.pokerio.app.screens.LobbyScreen
import com.pokerio.app.screens.SettingsScreen
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.ThemeUtils

const val NAV_INITIAL_SETUP = "initialSetup"
const val NAV_HOME = "home"
const val NAV_SETTINGS = "settings"
const val NAV_LOBBY = "lobby"
const val NAV_GAME = "game"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                MainActivityComposable()
            }
        }
    }
}

@Composable
fun MainActivityComposable() {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Transparent,
        darkIcons = true
    )

    val navController = rememberNavController()
    val context = LocalContext.current

    val navigateToSettings = {
        ContextCompat.getMainExecutor(context).execute {
            navController.navigate(NAV_SETTINGS)
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
            navController.navigate(NAV_HOME)
        }
    }
    val navigateToLobby = {
        ContextCompat.getMainExecutor(context).execute {
            navController.navigate(NAV_LOBBY)
        }
    }

    GameState.onGameReset = {
        ContextCompat.getMainExecutor(context).execute {
            navController.popBackStack(NAV_HOME, inclusive = false)
        }
    }
    GameState.onGameStart = {
        ContextCompat.getMainExecutor(context).execute {
            navController.popBackStack()
            navController.navigate(NAV_GAME)
        }
    }

    // Check if user had already set a nickname
    val sharedPreferences = LocalContext.current.getSharedPreferences(
        stringResource(id = R.string.shared_preferences_file),
        Context.MODE_PRIVATE
    )
    val nicknameSet =
        sharedPreferences.getString(stringResource(id = R.string.sharedPreferences_nickname), "")!!.isNotBlank()
    val startDestination = if (nicknameSet) NAV_HOME else NAV_INITIAL_SETUP

    NavHost(navController, startDestination) {
        composable(NAV_HOME) {
            HomeScreen(
                navigateToSettings = navigateToSettings,
                navigateToLobby = navigateToLobby
            )
        }
        composable(NAV_SETTINGS) { SettingsScreen(navigateBack = navigateBack) }
        composable(NAV_INITIAL_SETUP) { InitialSetupScreen(exitInitialSetup = { exitInitialSetup() }) }
        composable(NAV_LOBBY) { LobbyScreen(navigateToSettings = navigateToSettings) }
        composable(NAV_GAME) { GameScreen() }
    }
}

@Composable
private fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ThemeUtils.lightColorScheme,
        content = content
    )
}
