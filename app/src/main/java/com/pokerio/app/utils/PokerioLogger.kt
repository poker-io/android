package com.pokerio.app.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

object PokerioLogger {
    const val LOG_TAG = "Pokerio"

    fun error(message: String) {
        Log.e(LOG_TAG, message)
    }

    fun debug(message: String) {
        Log.d(LOG_TAG, message)
    }

    fun displayMessage(context: Context, message: String) {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}
