package com.pokerio.app

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import com.pokerio.app.utils.PokerioLogger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class PokerioFirebaseMessagingService : FirebaseMessagingService() {

    @Suppress("complexity.CyclomaticComplexMethod")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val type = MessageType.parse(message.data["type"]!!)
        try {
            when (type) {
                MessageType.PlayerJoined -> playerJoined(message.data)
                MessageType.SettingsUpdated -> settingsUpdated(message.data)
                MessageType.PlayerKicked -> playerKicked(message.data)
                MessageType.PlayerLeft -> playerLeft(message.data)
                MessageType.StartGame -> startGame(message.data)
                MessageType.ActionFold -> actionFold(message.data)
                MessageType.ActionRaise -> actionRaise(message.data)
                MessageType.ActionCheck -> actionCheck(message.data)
                MessageType.ActionCall -> actionCall(message.data)
                MessageType.NewCards -> newCards(message.data)
                MessageType.EndGame -> endGame(message.data)
                MessageType.UnknownMessage ->
                    PokerioLogger.error("Received unknown message type: ${message.data["type"]!!}")
            }
        } catch (e: Exception) {
            PokerioLogger.error("Exception occurred while processing '$type' firebase message")
            e.message?.let { PokerioLogger.error(it) }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PokerioLogger.debug("New Token: $token")
    }

    companion object {
        enum class MessageType(val typeName: String) {
            PlayerJoined("playerJoined"),
            SettingsUpdated("settingsUpdated"),
            PlayerKicked("playerKicked"),
            PlayerLeft("playerLeft"),
            StartGame("startGame"),
            ActionFold("fold"),
            ActionRaise("raise"),
            ActionCheck("check"),
            ActionCall("call"),
            NewCards("newCards"),
            EndGame("gameEnd"),

            UnknownMessage("");

            companion object {
                fun parse(type: String): MessageType {
                    return MessageType.values().find { it.typeName == type } ?: UnknownMessage
                }
            }
        }

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

        @OptIn(ExperimentalSerializationApi::class)
        fun newCards(data: Map<String, String>) {
            PokerioLogger.debug("Received newCards FCM message")
            GameState.newCards(Json.decodeFromString(data["cards"]!!))
        }

        @OptIn(ExperimentalSerializationApi::class)
        fun endGame(data: Map<String, String>) {
            PokerioLogger.debug("Received gameEnd FCM message")
            println(data["winners"])
            GameState.handleActionWon(
                Json.decodeFromString(data["winners"]!!),
                data["amount"]!!.toInt()
            )
        }
    }
}
