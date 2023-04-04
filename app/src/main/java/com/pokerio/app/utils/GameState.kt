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
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
    const val BASE_URL = "http://192.168.86.30:42069"
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

                // We have to add the creator to the list of players
                players.add(Player(nickname, creatorID, true))

                ContextCompat.getMainExecutor(context).execute(onSuccess)
            } catch (e: Exception) {
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
                println(responseJson)
                val responseObject = Json.parseToJsonElement(responseJson).jsonObject

                this@GameState.gameID = gameID
                startingFunds = responseObject["startingFunds"]!!.jsonPrimitive.content.toInt()
                smallBlind = responseObject["smallBlind"]!!.jsonPrimitive.content.toInt()

                val gameMasterHash = responseObject["gameMasterHash"]!!.jsonPrimitive.content

                // We are included in the player list, so no need to add as separately
                responseObject["players"]!!.jsonArray.forEach {
                    val nickname = it.jsonObject["nickname"]!!.jsonPrimitive.content
                    val playerHash = it.jsonObject["playerHash"]!!.jsonPrimitive.content

                    players.add(
                        Player(nickname, playerHash, playerHash == gameMasterHash)
//                        Player(
//                            playerObject.nickname,
//                            playerObject.playerHash,
//                            playerObject.playerHash == responseObject.gameMaster
//                        )
                    )
                }

                ContextCompat.getMainExecutor(context).execute(onSuccess)
            } catch (e: Exception) {
                e.printStackTrace()
                PokerioLogger.error(e.toString())
                ContextCompat.getMainExecutor(context).execute(onError)
            }
        }
    }
}

@Serializable
data class CreateGameResponse(
    val gameKey: String,
    val startingFunds: Int,
    val smallBlind: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = CreateGameResponse::class)
object CreateGameResponseSerializer

@Serializable
data class JoinGameResponse(
    val startingFunds: Int,
    val smallBlind: Int,
    val gameMasterHash: String,
    val players: ArrayList<JoinGameResponsePlayer> = ArrayList(8)
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = JoinGameResponse::class)
object JoinGameResponseSerializer

@Serializable
data class JoinGameResponsePlayer(
    val nickname: String,
    val playerHash: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = JoinGameResponsePlayer::class)
object JoinGameResponsePlayerSerializer
