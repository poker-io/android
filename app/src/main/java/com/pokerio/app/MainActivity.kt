package com.pokerio.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pokerio.app.screens.HomeScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { MainActivityComposable() }
    }

    @Composable
    fun MainActivityComposable() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen() }
        }
    }
}
