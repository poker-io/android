package com.pokerio.app

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pokerio.app.screens.HomeScreen


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme(useDarkTheme = false) {
                MainActivityComposable()
            }
        }
    }

    @Composable
    fun MainActivityComposable() {
        val navController = rememberNavController()

        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen() }
            }
        }
    }

    @Composable
    fun AppTheme(
        useDarkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        val colors = when {
            dynamicColor && useDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
            dynamicColor && !useDarkTheme -> dynamicLightColorScheme(LocalContext.current)
            useDarkTheme -> darkColorScheme()
            else -> lightColorScheme()
        }

        MaterialTheme(
            colorScheme = colors,
            content = content
        )
    }
}
