package com.pokerio.app.utils

import android.util.Log

class PokerioLogger {

    companion object {
        const val LOG_TAG = "Pokerio"

        fun error(message: String) {
            Log.e(LOG_TAG, message)
        }

        fun debug(message: String) {
            Log.d(LOG_TAG, message)
        }
    }
}
