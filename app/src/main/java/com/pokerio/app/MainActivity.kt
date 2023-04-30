package com.pokerio.app

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pokerio.app.screens.Game
import com.pokerio.app.screens.HomeScreen
import com.pokerio.app.screens.InitialSetupScreen
import com.pokerio.app.screens.LobbyScreen
import com.pokerio.app.screens.SettingsScreen
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.PokerioLogger
import com.pokerio.app.utils.discard

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme(useDarkTheme = false) {
                MainActivityComposable()
            }
        }
    }
}

@Composable
fun MainActivityComposable() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val navigateToSettings = {
        navController.navigate("settings")
    }
    val navigateBack = {
        navController.navigateUp().discard()
    }
    val exitInitialSetup = {
        navController.navigateUp()
        navController.navigate("home")
    }
    val navigateToLobby = {
        navController.navigate("lobby")
    }

    GameState.resetGameState()
    GameState.onGameReset = {
        ContextCompat.getMainExecutor(context).execute {
            navController.popBackStack("home", inclusive = false)
        }
    }
    GameState.onGameStart = {
        PokerioLogger.debug("Called onGameStart")
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
    val colors: ColorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (useDarkTheme) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            dynamicLightColorScheme(LocalContext.current)
        }
    } else {
        if (useDarkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
