package com.pokerio.app

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import com.pokerio.app.utils.PokerioLogger

class PokerioFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        when (message.data["type"]) {
            "playerJoined" -> playerJoined(message.data)
            "playerKicked" -> playerKicked(message.data)
            else -> PokerioLogger.error("Received unknown message type: ${message.data["type"]}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PokerioLogger.debug("New Token: $token")
    }

    companion object {
        fun playerJoined(data: Map<String, String>) {
            PokerioLogger.debug("Received playerJoined FCM message")

            GameState.addPlayer(
                Player(
                    data["nickname"]!!,
                    data["playerHash"]!!
                )
            )
        }

        fun playerKicked(data: Map<String, String>) {
            PokerioLogger.debug("Received playerKicked FCM message")

            GameState.removePlayer(data["playerHash"]!!)
        }
    }
}
