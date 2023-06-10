package com.pokerio.app

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pokerio.app.components.setDefaultBarsBehaviour
import com.pokerio.app.screens.GameScreen
import com.pokerio.app.screens.HomeScreen
import com.pokerio.app.screens.InitialSetupScreen
import com.pokerio.app.screens.LobbyScreen
import com.pokerio.app.screens.SettingsScreen
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Screens
import com.pokerio.app.utils.ThemeUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MainActivityComposable()
            }
        }
    }
}

@Composable
fun MainActivityComposable() {
    val navController = rememberNavController()
    val context = LocalContext.current

    setDefaultBarsBehaviour(LocalView.current)

    val navigateToSettings = {
        ContextCompat.getMainExecutor(context).execute {
            navController.navigate(Screens.Settings.route)
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
            navController.navigate(Screens.Home.route)
        }
    }
    val navigateToLobby = {
        ContextCompat.getMainExecutor(context).execute {
            navController.navigate(Screens.Lobby.route)
        }
    }

    GameState.onGameReset = {
        ContextCompat.getMainExecutor(context).execute {
            navController.popBackStack(Screens.Home.route, inclusive = false)
        }
    }
    GameState.onGameStart = {
        ContextCompat.getMainExecutor(context).execute {
            navController.popBackStack()
            navController.navigate(Screens.Game.route)
        }
    }

    // Check if user had already set a nickname
    val sharedPreferences = LocalContext.current.getSharedPreferences(
        stringResource(id = R.string.shared_preferences_file),
        Context.MODE_PRIVATE
    )
    val nicknameSet =
        sharedPreferences.getString(stringResource(id = R.string.sharedPreferences_nickname), "")!!.isNotBlank()
    val startDestination = if (nicknameSet) Screens.Home.route else Screens.Initial.route

    NavHost(navController, startDestination) {
        composable(Screens.Home.route) {
            HomeScreen(
                navigateToSettings = navigateToSettings,
                navigateToLobby = navigateToLobby
            )
        }
        composable(Screens.Settings.route) { SettingsScreen(navigateBack = navigateBack) }
        composable(Screens.Initial.route) { InitialSetupScreen(exitInitialSetup = { exitInitialSetup() }) }
        composable(Screens.Lobby.route) { LobbyScreen(navigateToSettings = navigateToSettings) }
        composable(Screens.Game.route) { GameScreen() }
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
