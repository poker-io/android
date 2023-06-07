package com.pokerio.app.utils

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.messaging.FirebaseMessaging
import com.pokerio.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import java.net.URL
import java.security.MessageDigest
import kotlin.jvm.Throws

// This is an object - a static object if you will that will exist in the global context. There
// will always be one and only one instance of this object
object GameState {
    // Constants
    const val BASE_URL = "http://158.101.160.143:42069"
    const val STARTING_FUNDS_DEFAULT = 1000
    const val SMALL_BLIND_DEFAULT = 100
    const val MAX_PLAYERS = 8
    const val MIN_PLAYERS = 2
    const val CARDS_ON_TABLE = 5

    // Class fields
    var gameID = ""
    val players = mutableStateListOf<Player>()

//    val _players: List<Player> = players
    var startingFunds: Int = -1
    var smallBlind: Int = -1
    var thisPlayer: Player = Player("", "")
    var currentPlayer: Player = Player("", "")
    var gameCard1: GameCard = GameCard.none()
    var gameCard2: GameCard = GameCard.none()
    val cards = Array(CARDS_ON_TABLE) { GameCard.none() }
    var winningsPool = 0

    // Callbacks
    var onGameReset = {}
    var onGameStart = {}
    var onWon: (Player) -> Unit = {}
    private val playerJoinedCallbacks = HashMap<Int, (Player) -> Unit>()
    private val playerRemovedCallbacks = HashMap<Int, (Player) -> Unit>()
    private val settingsChangedCallbacks = HashMap<Int, () -> Unit>()
    private val newActionCallbacks = HashMap<Int, (Player?) -> Unit>()
    private var nextId = 0

    // Methods
    fun launchTask(task: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            task()
        }
    }

    suspend fun createGameRequest(
        context: Context,
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
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

        val preferredSmallBlind = sharedPreferences.getInt(
            context.getString(R.string.sharedPreferences_small_blind),
            SMALL_BLIND_DEFAULT
        )

        val preferredStartingFunds = sharedPreferences.getInt(
            context.getString(R.string.sharedPreferences_starting_funds),
            STARTING_FUNDS_DEFAULT
        )

        // Make request
        try {
            val creatorID = firebaseId ?: FirebaseMessaging.getInstance().token.await()
            // Prepare url
            val urlString =
                "/createGame?creatorToken=$creatorID&nickname=$nickname" +
                    "&smallBlind=$preferredSmallBlind&startingFunds=$preferredStartingFunds"
            val url = URL(baseUrl + urlString)

            val responseJson = url.readText()
            val responseObject =
                Json.decodeFromString(CreateGameResponseSerializer, responseJson)

            gameID = responseObject.gameId.toString()
            startingFunds = responseObject.startingFunds
            smallBlind = responseObject.smallBlind

            // We have to add the creator to the list of players
            players.clear()
            thisPlayer = Player(nickname, creatorID, true)
            addPlayer(thisPlayer)

            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Failed to create game, reason: $e")
            onError()
        }
    }

    suspend fun joinGameRequest(
        gameID: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
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
        try {
            val playerID = firebaseId ?: FirebaseMessaging.getInstance().token.await()
            // Prepare url
            val urlString = "/joinGame?nickname=$nickname&playerToken=$playerID&gameId=$gameID"
            val url = URL(baseUrl + urlString)

            val responseJson = url.readText()

            this.gameID = gameID

            val joinGameResponse = Json.decodeFromString(JoinGameResponseSerializer, responseJson)
            startingFunds = joinGameResponse.startingFunds
            smallBlind = joinGameResponse.smallBlind
            val gameMasterHash = joinGameResponse.gameMasterHash

            joinGameResponse.players.forEach { jsonElement ->
                val playerResponse = Json.decodeFromString(PlayerResponseSerializer, jsonElement.toString())
                val newPlayer = Player(
                    playerResponse.nickname,
                    playerResponse.playerHash,
                    playerResponse.playerHash == gameMasterHash
                )

                addPlayer(newPlayer)
            }

            // This player is not included in the player list, so we need to add them separately
            thisPlayer = Player(nickname, playerID)
            addPlayer(thisPlayer)

            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Failed to join game, reason: $e")
            onError()
        }
    }

    suspend fun modifyGameRequest(
        smallBlind: Int,
        startingFunds: Int,
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
    ) {
        try {
            val playerID = firebaseId ?: FirebaseMessaging.getInstance().token.await()

            // Prepare url
            val urlString =
                "/modifyGame?creatorToken=$playerID&smallBlind=$smallBlind&startingFunds=$startingFunds"
            val url = URL(baseUrl + urlString)
            url.readText()

            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Failed to modify game, reason: $e")
            onError()
        }
    }

    suspend fun kickPlayerRequest(
        playerID: String,
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
    ) {
        try {
            val myID = firebaseId ?: FirebaseMessaging.getInstance().token.await()

            // Prepare url
            val urlString = "/kickPlayer?creatorToken=$myID&playerToken=$playerID"
            val url = URL(baseUrl + urlString)

            url.readText()

            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Failed to kick player, reason: $e")
            onError()
        }
    }

    suspend fun leaveGameRequest(
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
    ) {
        try {
            val myID = firebaseId ?: FirebaseMessaging.getInstance().token.await()

            // Prepare url
            val urlString = "/leaveGame?playerToken=$myID"
            val url = URL(baseUrl + urlString)

            url.readText()

            resetGameState()
            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Failed to leave game, reason: $e")
            onError()
        }
    }

    suspend fun startGameRequest(
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
    ) {
        try {
            val myID = firebaseId ?: FirebaseMessaging.getInstance().token.await()

            // Prepare url
            val urlString = "/startGame?creatorToken=$myID"
            val url = URL(baseUrl + urlString)

            url.readText()

            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Failed to start game, reason: $e")
            onError()
        }
    }

    suspend fun actionCallRequest(
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
    ) {
        try {
            val myID = firebaseId ?: FirebaseMessaging.getInstance().token.await()

            // Prepare url
            val urlString = "/actionCall?playerToken=$myID&gameId=$gameID"
            val url = URL(baseUrl + urlString)

            url.readText()

            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Action call during gameplay failed, reason: $e")
            onError()
        }
    }

    fun handleActionCall(playerHash: String) {
        val isThisPlayer = sha256(thisPlayer.playerID) == playerHash

        val player = if (isThisPlayer) thisPlayer else players.find { it.playerID == playerHash }
        require(player != null)

        val newBet = getMaxBet()

        player.funds -= (newBet - player.bet)
        player.bet = newBet

        nextCurrentPlayer()
        newActionCallbacks.forEach { it.value(player) }
    }

    suspend fun actionCheckRequest(
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
    ) {
        try {
            val myID = firebaseId ?: FirebaseMessaging.getInstance().token.await()

            // Prepare url
            val urlString = "/actionCheck?playerToken=$myID&gameId=$gameID"
            val url = URL(baseUrl + urlString)

            url.readText()

            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Action check during gameplay failed, reason: $e")
            onError()
        }
    }

    fun handleActionCheck(playerHash: String) {
        val isThisPlayer = sha256(thisPlayer.playerID) == playerHash

        val player = if (isThisPlayer) thisPlayer else players.find { it.playerID == playerHash }
        require(player != null)

        nextCurrentPlayer()
        newActionCallbacks.forEach { it.value(player) }
    }

    suspend fun actionRaiseRequest(
        newAmount: Int,
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
    ) {
        try {
            val myID = firebaseId ?: FirebaseMessaging.getInstance().token.await()

            // Prepare url
            val urlString = "/actionRaise?playerToken=$myID&gameId=$gameID&amount=$newAmount"
            val url = URL(baseUrl + urlString)

            url.readText()

            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Action raise during gameplay failed, reason: $e")
            onError()
        }
    }

    fun handleActionRaise(playerHash: String, newAmount: Int) {
        val isThisPlayer = sha256(thisPlayer.playerID) == playerHash

        val player = if (isThisPlayer) thisPlayer else players.find { it.playerID == playerHash }
        require(player != null)

        player.funds -= (newAmount - player.bet)
        player.bet = newAmount

        nextCurrentPlayer()
        newActionCallbacks.forEach { it.value(player) }
    }

    suspend fun actionFoldRequest(
        onSuccess: () -> Unit,
        onError: () -> Unit,
        baseUrl: String = BASE_URL,
        firebaseId: String? = null
    ) {
        try {
            val myID = firebaseId ?: FirebaseMessaging.getInstance().token.await()

            // Prepare url
            val urlString = "/actionFold?playerToken=$myID&gameId=$gameID"
            val url = URL(baseUrl + urlString)

            url.readText()

            onSuccess()
        } catch (e: Exception) {
            PokerioLogger.error("Action fold during gameplay failed, reason: $e")
            onError()
        }
    }

    fun handleActionFold(playerHash: String) {
        val isThisPlayer = sha256(thisPlayer.playerID) == playerHash

        val player = if (isThisPlayer) thisPlayer else players.find { it.playerID == playerHash }
        require(player != null)

        player.folded = true

        nextCurrentPlayer()
        newActionCallbacks.forEach { it.value(player) }
    }

    fun handleActionWon(playerHash: String) {
        val isThisPlayer = sha256(thisPlayer.playerID) == playerHash

        val player = if (isThisPlayer) thisPlayer else players.find { it.playerID == playerHash }
        require(player != null)

        onWon(player)
    }

    fun resetGameState() {
        // Class fields
        gameID = ""
        players.clear()
        startingFunds = -1
        smallBlind = -1
        thisPlayer = Player("", "")
        gameCard1 = GameCard.none()
        gameCard2 = GameCard.none()
        // Callbacks
        playerJoinedCallbacks.clear()
        playerRemovedCallbacks.clear()
        settingsChangedCallbacks.clear()
        newActionCallbacks.clear()
        // Not resetting nextId, because someone might be holding on to an old one and we don't
        // want then to remove new callbacks by mistake
        cards.fill(GameCard.none())

        onGameReset()
    }

    fun addOnPlayerJoinedCallback(callback: (Player) -> Unit): Int {
        playerJoinedCallbacks[nextId] = callback
        return nextId++
    }

    fun removeOnPlayerJoinedCallback(id: Int) {
        playerJoinedCallbacks.remove(id)
    }

    fun addOnPlayerRemovedCallback(callback: (Player) -> Unit): Int {
        playerRemovedCallbacks[nextId] = callback
        return nextId++
    }

    fun removeOnPlayerRemovedCallback(id: Int) {
        playerRemovedCallbacks.remove(id)
    }

    fun addOnSettingsChangedCallback(callback: () -> Unit): Int {
        settingsChangedCallbacks[nextId] = callback
        return nextId++
    }

    fun removeOnNewActionCallback(id: Int) {
        newActionCallbacks.remove(id)
    }

    fun addOnNewActionCallback(callback: (Player?) -> Unit): Int {
        newActionCallbacks[nextId] = callback
        return nextId++
    }

    fun removeOnSettingsChangedCallback(id: Int) {
        settingsChangedCallbacks.remove(id)
    }

    fun addPlayer(player: Player) {
        players.add(player)
        playerJoinedCallbacks.forEach { it.value(player) }
    }

    @Throws(IllegalArgumentException::class)
    fun removePlayer(playerHash: String, newAdmin: String? = null) {
        if (!isInGame()) {
            return
        }

        val isThisPlayerRemoved = (playerHash == sha256(thisPlayer.playerID))
        if (isThisPlayerRemoved) {
            return resetGameState()
        }

        val removedPlayer = players.find { it.playerID == playerHash }
        require(removedPlayer != null)

        if (removedPlayer.isAdmin) {
            require(newAdmin != null)
            val isThisPlayerNewAdmin = (newAdmin == sha256(thisPlayer.playerID))

            if (isThisPlayerNewAdmin) {
                thisPlayer.isAdmin = true
            } else {
                val newAdminPlayer = players.find { it.playerID == newAdmin }
                newAdminPlayer!!.isAdmin = true
            }
        }

        players.remove(removedPlayer)
        playerRemovedCallbacks.forEach { it.value(removedPlayer) }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun startGame(card1: String, card2: String, playersString: String) {
        gameCard1 = GameCard.fromString(card1)
        gameCard2 = GameCard.fromString(card2)
        players.forEach { it.funds = startingFunds }

        val playersJsonArray = Json.decodeFromString<JsonArray>(playersString)
        playersJsonArray.forEach { playerWithTurnJson ->
            println(playerWithTurnJson.toString())
            val playerWithTurn = Json.decodeFromString(PlayerWithTurnResponseSerializer, playerWithTurnJson.toString())

            if (sha256(thisPlayer.playerID) == playerWithTurn.playerHash) {
                thisPlayer.turn = playerWithTurn.turn
            } else {
                val player = players.find { it.playerID == playerWithTurn.playerHash }!!
                player.turn = playerWithTurn.turn
            }
        }

        players.sortBy { it.turn }
        currentPlayer = players[0]
        val smallBlindIndex: Int = players.lastIndex - 1
        val bigBlindIndex: Int = players.lastIndex
        players[smallBlindIndex].bet = smallBlind
        players[smallBlindIndex].funds -= smallBlind
        players[bigBlindIndex].bet = smallBlind * 2
        players[bigBlindIndex].funds -= smallBlind * 2

        onGameStart()
    }

    fun newCards(newCards: List<String>) {
        var index = cards.indexOfFirst { it.isNone() }

        newCards.forEach {
            cards[index] = GameCard.fromString(it)
            index++
        }

        players.forEach() {
            winningsPool += it.bet
            it.bet = 0
        }

        nextRoundPlayer()
        newActionCallbacks.forEach { it.value(null) }
    }

    fun sha256(string: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(string.toByteArray())
            .fold("") { str, byte -> str + "%02x".format(byte) }
    }

    fun changeGameSettings(newStartingFunds: Int, newSmallBlind: Int) {
        startingFunds = newStartingFunds
        smallBlind = newSmallBlind
        settingsChangedCallbacks.forEach { it.value() }
    }

    fun isInGame(): Boolean {
        return gameID.isNotBlank()
    }

    @Throws(NoSuchElementException::class)
    fun getMaxBet(): Int {
        return players.maxOf { it.bet }
    }

    fun getSmallBlindPlayer(): Player? {
        if (players.size < 2) {
            return null
        }
        return players[players.size - 2]
    }

    fun getBigBlindPlayer(): Player? {
        if (players.size < 2) {
            return null
        }
        return players[players.size - 1]
    }

    fun nextCurrentPlayer() {
        var index = players.indexOfFirst { player: Player -> player == currentPlayer }
        if (index != -1) {
            index = (index + 1) % players.size
            while (players[index] != currentPlayer) {
                if (!players[index].folded) {
                    currentPlayer = players[index]
                    break
                }
                index = (index + 1) % players.size
            }
        }
    }

    fun nextRoundPlayer() {
        val smallBlind = getSmallBlindPlayer()
        if (smallBlind != null){
            currentPlayer = smallBlind
            if (smallBlind.folded){
                nextCurrentPlayer()
            }
        }
    }
}

data class CreateGameResponse(
    val gameId: Int,
    val startingFunds: Int,
    val smallBlind: Int
)

@Generated
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = CreateGameResponse::class)
object CreateGameResponseSerializer

data class PlayerResponse(
    val nickname: String,
    val playerHash: String,
    val turn: Int
)

@Generated
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PlayerResponse::class)
object PlayerResponseSerializer

data class JoinGameResponse(
    val gameMasterHash: String,
    val startingFunds: Int,
    val smallBlind: Int,
    val players: JsonArray
)

@Generated
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = JoinGameResponse::class)
object JoinGameResponseSerializer

data class PlayerWithTurnResponse(
    val nickname: String,
    val playerHash: String,
    val turn: Int
)

@Generated
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PlayerWithTurnResponse::class)
object PlayerWithTurnResponseSerializer
