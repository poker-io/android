package com.pokerio.app.utils

sealed class Screens(val route: String) {
    object Initial : Screens("initial")
    object Home : Screens("home")
    object Lobby : Screens("lobby")
    object Settings : Screens("settings")
    object Game : Screens("game")
}
