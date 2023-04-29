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
        private set
    var startingFunds: Int = -1
    var smallBlind: Int = -1

    // Callbacks
    private var playerJoinedCallbacks = HashMap<Int, (Player) -> Unit>()
    private var playerRemovedCallbacks = HashMap<Int, (Player) -> Unit>()
    private var nextId = 0

    // Constants
    const val BASE_URL = "http://158.101.160.143:42069"
    val netowrkCoroutine = CoroutineScope(Dispatchers.IO)

    // Methods

    // This method makes a request to create a game and sets the field of the GameState object on
    // success and returns true. Returns false if something goes wrong.
    fun createGame(
        context: Context,
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL
    ) {
        // Get all values
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )

        val nickname = sharedPreferences.getString(
            context.getString(R.string.sharedPreferences_nickname),
            "Player"
        ) ?: "Player"
        // TODO: Load settings

        // Make request
        netowrkCoroutine.launch {
            try {
                val creatorID = FirebaseMessaging.getInstance().token.await()
                // Prepare url
                val urlString = "/createGame?creatorToken=$creatorID&nickname=$nickname"
                val url = URL(baseUrl + urlString)

                val responseJson = url.readText()
                val responseObject =
                    Json.decodeFromString(CreateGameResponseSerializer, responseJson)

                gameID = responseObject.gameKey
                startingFunds = responseObject.startingFunds
                smallBlind = responseObject.smallBlind

                // We have to add the creator to the list of players
                addPlayer(Player(nickname, creatorID, true))

                ContextCompat.getMainExecutor(context).execute(onSuccess)
            } catch (e: Exception) {
                PokerioLogger.error(e.toString())
                ContextCompat.getMainExecutor(context).execute(onError)
            }
        }
    }

    fun joinGame(
        gameID: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL
    ) {
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
                val url = URL(baseUrl + urlString)

                val responseJson = url.readText()
                println(responseJson)
                val responseObject = Json.parseToJsonElement(responseJson).jsonObject

                this@GameState.gameID = gameID
                // TODO: This has to be parsed better
                startingFunds = responseObject["startingFunds"]!!.jsonPrimitive.content.toInt()
                smallBlind = responseObject["smallBlind"]!!.jsonPrimitive.content.toInt()

                val gameMasterHash = responseObject["gameMasterHash"]!!.jsonPrimitive.content

                // We are included in the player list, so no need to add as separately
                responseObject["players"]!!.jsonArray.forEach {
                    val nickname = it.jsonObject["nickname"]!!.jsonPrimitive.content
                    val playerHash = it.jsonObject["playerHash"]!!.jsonPrimitive.content

                    addPlayer(Player(nickname, playerHash, playerHash == gameMasterHash))
                }

                ContextCompat.getMainExecutor(context).execute(onSuccess)
            } catch (e: Exception) {
                e.printStackTrace()
                PokerioLogger.error(e.toString())
                ContextCompat.getMainExecutor(context).execute(onError)
            }
        }
    }

    fun exitSettings(
        context: Context,
        onError: () -> Unit,
        baseUrl: String = BASE_URL
    ) {
        if (isInGame()) {
            val sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_file),
                Context.MODE_PRIVATE
            )

            val smallBlind = sharedPreferences.getInt(
                context.getString(R.string.sharedPreferences_small_blind),
                1000
            )

            val startingFunds = sharedPreferences.getInt(
                context.getString(R.string.sharedPreferences_starting_funds),
                100
            )

            netowrkCoroutine.launch {
                try {
                    val playerID = FirebaseMessaging.getInstance().token.await()

                    // Prepare url
                    val urlString = "/modifyGame?creatorToken=$playerID&smallBlind=$smallBlind&startingFunds=$startingFunds"
                    val url = URL(baseUrl + urlString)
                    url.readText()
                } catch (e: Exception) {
                    e.printStackTrace()
                    PokerioLogger.error(e.toString())
                    ContextCompat.getMainExecutor(context).execute(onError)
                }
            }
        }
    }

    fun addOnPlayerJoinedCallback(callback: (Player) -> Unit): Int {
        playerJoinedCallbacks.put(nextId, callback)
        return nextId++
    }

    fun removeOnPlayerJoinedCallback(id: Int) {
        playerJoinedCallbacks.remove(id)
    }

    fun addOnPlayerRemovedCallback(callback: (Player) -> Unit): Int {
        playerRemovedCallbacks.put(nextId, callback)
        return nextId++
    }

    fun removeOnPlayerRemovedCallback(id: Int) {
        playerRemovedCallbacks.remove(id)
    }

    fun addPlayer(player: Player) {
        players.add(player)
        playerJoinedCallbacks.forEach { it.value(player) }
    }

    fun removePlayer(playerHash: String) {
        val player = players.find { it.playerID == playerHash } ?: return

        players.removeIf { it.playerID == playerHash }
        playerRemovedCallbacks.forEach { it.value(player) }
    }

    fun changeGameSettings(newStartingFunds: Int, newSmallBlind: Int){
        startingFunds = newStartingFunds
        smallBlind = newSmallBlind
    }
    fun isInGame(): Boolean {
        return gameID != ""
    }
}

data class CreateGameResponse(
    val gameKey: String,
    val startingFunds: Int,
    val smallBlind: Int
)

@Generated
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = CreateGameResponse::class)
object CreateGameResponseSerializer
