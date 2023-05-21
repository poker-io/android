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
            PLAYER_JOINED -> playerJoined(message.data)
            SETTINGS_UPDATED -> settingsUpdated(message.data)
            PLAYER_KICKED -> playerKicked(message.data)
            PLAYER_LEFT -> playerLeft(message.data)
            START_GAME -> startGame(message.data)
            ACTION_FOLD -> actionFold(message.data)
            ACTION_RAISE -> actionRaise(message.data)
            ACTION_CHECK -> actionCheck(message.data)
            ACTION_CALL -> actionCall(message.data)
            ACTION_WON -> actionWon(message.data)
            else -> PokerioLogger.error("Received unknown message type: ${message.data["type"]}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PokerioLogger.debug("New Token: $token")
    }

    companion object {
        const val PLAYER_JOINED = "playerJoined"
        const val SETTINGS_UPDATED = "settingsUpdated"
        const val PLAYER_KICKED = "playerKicked"
        const val PLAYER_LEFT = "playerLeft"
        const val START_GAME = "startGame"
        const val ACTION_FOLD = "fold"
        const val ACTION_RAISE = "raise"
        const val ACTION_CHECK = "check"
        const val ACTION_CALL = "call"
        const val ACTION_WON = "won"

        fun playerJoined(data: Map<String, String>) {
            PokerioLogger.debug("Received playerJoined FCM message")

            GameState.addPlayer(
                Player(
                    data["nickname"]!!,
                    data["playerHash"]!!
                )
            )
        }

        fun settingsUpdated(data: Map<String, String>) {
            PokerioLogger.debug("Received updatedSettings FCM message")

            GameState.changeGameSettings(
                data["startingFunds"]!!.toInt(),
                data["smallBlind"]!!.toInt()
            )
        }

        fun playerKicked(data: Map<String, String>) {
            PokerioLogger.debug("Received playerKicked FCM message")
            GameState.removePlayer(data["playerHash"]!!)
        }

        fun playerLeft(data: Map<String, String>) {
            PokerioLogger.debug("Received playerLeft FCM message")
            GameState.removePlayer(data["playerHash"]!!, data["gameMaster"]!!)
        }

        fun startGame(data: Map<String, String>) {
            PokerioLogger.debug("Received startGame FCM message")
            GameState.startGame(
                data["card1"]!!,
                data["card2"]!!,
                data["players"]!!
            )
        }

        fun actionFold(data: Map<String, String>) {
            PokerioLogger.debug("Received fold FCM message")
            GameState.handleActionFold(data["player"]!!)
        }

        fun actionRaise(data: Map<String, String>) {
            PokerioLogger.debug("Received raise FCM message")
            GameState.handleActionRaise(data["player"]!!, data["actionPayload"]!!.toInt())
        }

        fun actionCheck(data: Map<String, String>) {
            PokerioLogger.debug("Received check FCM message")
            GameState.handleActionCheck(data["player"]!!)
        }

        fun actionCall(data: Map<String, String>) {
            PokerioLogger.debug("Received call FCM message")
            GameState.handleActionCall(data["player"]!!)
        }

        fun actionWon(data: Map<String, String>) {
            PokerioLogger.debug("Received won FCM message")
            GameState.handleActionWon(data["player"]!!)
        }
    }
}
