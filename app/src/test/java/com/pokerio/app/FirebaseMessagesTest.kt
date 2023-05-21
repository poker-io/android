package com.pokerio.app

import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.After
import org.junit.Before
import org.junit.Test

class FirebaseMessagesTest {
    @Before
    fun setup() {
        // Make sure we're using a clean game state
        GameState.resetGameState()
    }

    @Test
    fun playerJoinedTest() {
        val playerName = "Player432"
        val playerHash = "123"

        val map = HashMap<String, String>()
        map["nickname"] = playerName
        map["playerHash"] = playerHash

        assert(GameState.players.size == 0)
        PokerioFirebaseMessagingService.playerJoined(map)
        assert(GameState.players.size == 1)
        val player = GameState.players[0]
        assert(!player.isAdmin)
        assert(player.nickname == playerName)
        assert(player.playerID == playerHash)

        // Clean up after ourselves
        GameState.players.clear()
    }

    @Test
    fun settingsUpdatedTest() {
        GameState.startingFunds = 1000
        GameState.smallBlind = 100
        val newStaringFunds = 2137
        val newSmallBlind = 420

        val map = HashMap<String, String>()
        map["startingFunds"] = newStaringFunds.toString()
        map["smallBlind"] = newSmallBlind.toString()

        PokerioFirebaseMessagingService.settingsUpdated(map)
        assert(GameState.startingFunds == newStaringFunds)
        assert(GameState.smallBlind == newSmallBlind)
    }

    @Test
    fun playerKickedTest() {
        val playerNickname = "testPlayer1"
        val playerId = GameState.sha256("testId1")

        GameState.gameID = "gameID"
        GameState.addPlayer(Player(playerNickname, playerId))

        val map = HashMap<String, String>()
        map["playerHash"] = playerId

        assert(GameState.players.size == 1)
        PokerioFirebaseMessagingService.playerKicked(map)
        assert(GameState.players.size == 0)
    }

    @Test
    fun playerLeftTest() {
        val playerNickname = "testPlayer1"
        val playerId = GameState.sha256("testId1")
        val thisPlayerNickname = "testPlayer2"
        val thisPlayerId = "testId2"
        val thisPlayerIdSha = GameState.sha256(thisPlayerId)

        GameState.gameID = "gameID"
        GameState.addPlayer(Player(playerNickname, playerId, true))
        GameState.thisPlayer = Player(thisPlayerNickname, thisPlayerId)
        GameState.addPlayer(GameState.thisPlayer)

        val map = HashMap<String, String>()
        map["playerHash"] = playerId
        map["gameMaster"] = thisPlayerIdSha

        assert(GameState.players.size == 2)
        PokerioFirebaseMessagingService.playerLeft(map)
        assert(GameState.players.size == 1)
        assert(GameState.players[0].playerID == thisPlayerId)
        assert(GameState.players[0].isAdmin)
    }

    @After
    fun tearDown() {
        // Clean up after each test
        GameState.resetGameState()
    }
}
