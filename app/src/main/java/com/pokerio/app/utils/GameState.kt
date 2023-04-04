package com.pokerio.app.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.pokerio.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.net.URL

// This is an object - a static object if you will that will exist in the global context. There
// will always be one and only one instance of this object
object GameState {
    // Class fields
    var gameID = ""
    var players = mutableListOf<Player>()
    var startingFunds: Int = -1
    var smallBlind: Int = -1

    // Constants
    const val BASE_URL = "http://10.20.7.83:42069"
    val netowrkCoroutine = CoroutineScope(Dispatchers.IO)

    // Methods

    // This method makes a request to create a game and sets the field of the GameState object on
    // success and returns true. Returns false if something goes wrong.
    fun createGame(context: Context, onSuccess: () -> Unit, onError: () -> Unit) {
        // Get all values
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )

        val nickname = sharedPreferences.getString(
            context.getString(R.string.sharedPreferences_nickname),
            "Player"
        )!!
        // TODO: Load settings
//        val startingFunds = sharedPreferences.getInt(
//            context.getString(R.string.sharedPreferences_starting_funds), -1
//        )
//        val smallBlind = sharedPreferences.getInt(
//            context.getString(R.string.sharedPreferences_small_blind), -1
//        )

        // Make request
        netowrkCoroutine.launch {
            try {
                val creatorID = FirebaseMessaging.getInstance().token.await()

                // Prepare url
                val urlString = "/createGame?creatorToken=$creatorID&nickname=$nickname"
                val url = URL(BASE_URL + urlString)

                val responseJson = url.readText()
                val responseObject =
                    Json.decodeFromString(CreateGameResponseSerializer, responseJson)

                gameID = responseObject.gameKey
                startingFunds = responseObject.startingFunds
                smallBlind = responseObject.smallBlind

                ContextCompat.getMainExecutor(context).execute(onSuccess)
            } catch (e: FileNotFoundException) {
                PokerioLogger.error(e.toString())
                ContextCompat.getMainExecutor(context).execute(onError)
            }
        }
    }

    fun joinGame(gameID: String, context: Context, onSuccess: () -> Unit, onError: () -> Unit) {
        // Get all values
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )

        val nickname = sharedPreferences.getString(
            context.getString(R.string.sharedPreferences_nickname),
            "Player"
        ) ?: "Player"

        // Make request
        netowrkCoroutine.launch {
            try {
                val playerID = FirebaseMessaging.getInstance().token.await()

                // Prepare url
                val urlString = "/joinGame?nickname=$nickname&playerToken=$playerID&gameId=$gameID"
                val url = URL(BASE_URL + urlString)

                val responseJson = url.readText()
                val responseObject =
                    Json.decodeFromString(JoinGameResponseSerializer, responseJson)

                this@GameState.gameID = gameID
                startingFunds = responseObject.startingFunds
                smallBlind = responseObject.smallBlind

                // We are included in the player list, so no need to add as separately
                responseObject.players.forEach {
                    players.add(
                        Player(
                            it.nickname,
                            it.playerHash,
                            it.playerHash == responseObject.gameMaster
                        )
                    )
                }

                ContextCompat.getMainExecutor(context).execute(onSuccess)
            } catch (e: FileNotFoundException) {
                PokerioLogger.error(e.toString())
                ContextCompat.getMainExecutor(context).execute(onError)
            }
        }
    }
}

data class CreateGameResponse(
    val gameKey: String,
    val startingFunds: Int,
    val smallBlind: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = CreateGameResponse::class)
object CreateGameResponseSerializer

data class JoinGameResponse(
    val startingFunds: Int,
    val smallBlind: Int,
    val gameMaster: String,
    val players: Array<JoinGameResponsePlayer>
)

data class JoinGameResponsePlayer(
    val nickname: String,
    val playerHash: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = JoinGameResponse::class)
object JoinGameResponseSerializer
