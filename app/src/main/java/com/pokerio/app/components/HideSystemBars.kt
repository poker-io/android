package com.pokerio.app.components

import android.app.Activity
import android.view.View
import androidx.core.view.WindowCompat

fun setDefaultBarsBehaviour(view: View) {
    val window = (view.context as Activity).window
    val insetsController = WindowCompat.getInsetsController(window, view)

    // Allow app to draw behind system bars
    WindowCompat.setDecorFitsSystemWindows(window, false)
    insetsController.isAppearanceLightStatusBars = true
    insetsController.isAppearanceLightNavigationBars = true
}
