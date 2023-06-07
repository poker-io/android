package com.pokerio.app.utils

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PokerioLogger {
    const val LOG_TAG = "Pokerio"
    var snackbarHostState: SnackbarHostState? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun error(message: String) {
        Log.e(LOG_TAG, message)
    }

    fun debug(message: String) {
        Log.d(LOG_TAG, message)
    }

    fun displayMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState?.showSnackbar(message)
                ?: error("snackbarHostState is null. Did you forget to set it?")
        }
    }
}
