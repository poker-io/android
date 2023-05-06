package com.pokerio.app

import com.pokerio.app.utils.Card
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class GameStateTest {
    @Test
    fun addRemovePlayerTest() {
        val testPlayer = Player("Hi, I'm a test player", "42069")

        assertTrue("GameState.players not empty!", GameState.players.size == 0)
        GameState.addPlayer(testPlayer)
        assertTrue("Player not added to GameState", GameState.players.size == 1)
        assertTrue("Player added to GameState incorrectly", GameState.players[0] == testPlayer)
        GameState.removePlayer(testPlayer.playerID)
        assertTrue("Player not removed from GameState", GameState.players.size == 0)
    }

    @Test
    fun onPlayerJoinedCallbackTest() {
        val testPlayer = Player("Hi, I'm a test player", "42069")

        var playerAddedCalledCounter = 0
        val onPlayerAdded = { it: Player ->
            playerAddedCalledCounter++
            assertTrue("Player passed to callback incorrectly", it == testPlayer)
        }
        val playerAddedCallbackId = GameState.addOnPlayerJoinedCallback(onPlayerAdded)

        GameState.addPlayer(testPlayer)
        assertTrue("onPlayerAdded callback not called or called too many times", playerAddedCalledCounter == 1)
        GameState.removePlayer(testPlayer.playerID)
        assertTrue("onPlayerAdded callback should not have been called", playerAddedCalledCounter == 1)

        GameState.removeOnPlayerJoinedCallback(playerAddedCallbackId)
        GameState.addPlayer(testPlayer)
        GameState.removePlayer(testPlayer.playerID)
        assertTrue("onPlayerAdded callback not removed", playerAddedCalledCounter == 1)
    }

    @Test
    fun onPlayerRemovedCallbackTest() {
        val testPlayer = Player("Hi, I'm a test player", "42069")

        var playerRemovedCalledCounter = 0
        val onPlayerRemoved = { it: Player ->
            playerRemovedCalledCounter++
            assertTrue("Player passed to callback incorrectly", it == testPlayer)
        }
        val playerRemovedCallbackId = GameState.addOnPlayerRemovedCallback(onPlayerRemoved)

        GameState.addPlayer(testPlayer)
        assertTrue("onPlayerRemoved callback should not have been called", playerRemovedCalledCounter == 0)
        GameState.removePlayer(testPlayer.playerID)
        assertTrue("onPlayerRemoved callback not called or called too many times", playerRemovedCalledCounter == 1)

        GameState.removeOnPlayerRemovedCallback(playerRemovedCallbackId)
        GameState.addPlayer(testPlayer)
        GameState.removePlayer(testPlayer.playerID)
        assertTrue("onPlayerRemoved callback not removed", playerRemovedCalledCounter == 1)
    }

    fun changeSettingsTest() {
        GameState.startingFunds = 1000
        GameState.startingFunds = 100
        val newStartingFunds = 2137
        val newSmallBlind = 109

        GameState.changeGameSettings(newStartingFunds, newSmallBlind)

        assertTrue("Starting funds not updated", GameState.startingFunds == newStartingFunds)
        assertTrue("Small blind not updated", GameState.smallBlind == newSmallBlind)
    }

    @Test
    fun onSettingsChangedCallbackTest() {
        GameState.startingFunds = 1000
        GameState.startingFunds = 100
        val newStartingFunds = 2137
        val newSmallBlind = 109

        var onSettingsChangedCalled = 0
        val onSettingsChanged = {
            onSettingsChangedCalled += 1
        }

        val callbackId = GameState.addOnSettingsChangedCallback(onSettingsChanged)
        GameState.changeGameSettings(newStartingFunds, newSmallBlind)

        assertTrue("onSettingsChanged not called", onSettingsChangedCalled == 1)

        GameState.removeOnSettingsChangedCallback(callbackId)
        GameState.changeGameSettings(1000, 100)

        assertTrue("onSettingsChanged called after removal", onSettingsChangedCalled == 1)
    }

    @Test
    fun resetGameStateTest() {
        var resetCalled = false
        val onResetState = {
            resetCalled = true
        }

        GameState.gameID = "test"
        GameState.players.add(Player("testPlayer", "hash"))
        GameState.startingFunds = 123123
        GameState.smallBlind = 123123
        GameState.isPlayerAdmin = true
        GameState.onGameReset = onResetState
        GameState.card1 = Card("E", "E")
        GameState.card2 = Card("E", "E")

        GameState.resetGameState()
        assertTrue("gameID not reset", GameState.gameID.isEmpty())
        assertTrue("players list not reset", GameState.players.isEmpty())
        assertTrue("startingFunds not reset", GameState.startingFunds == -1)
        assertTrue("smallBlind not reset", GameState.smallBlind == -1)
        assertTrue("isPlayerAdmin not reset", GameState.isPlayerAdmin == false)
        assertTrue("onGameReset not called", resetCalled)
        assertTrue("card1 not reset", GameState.card1 == null)
        assertTrue("card2 not reset", GameState.card2 == null)
    }

    @Test
    fun removeNonAdminTest() {
        val player1Nickname = "testPlayer1"
        val player1Id = GameState.sha256("testId1")
        val thisPlayerNickname = "testPlayer2"
        val thisPlayerId = "testId2"

        var onPlayerRemovedCalled = 0
        val onPlayerRemoved = { it: Player ->
            assert(it.nickname == player1Nickname)
            assert(it.playerID == player1Id)

            onPlayerRemovedCalled += 1
        }

        GameState.addOnPlayerRemovedCallback(onPlayerRemoved)
        GameState.addPlayer(Player(player1Nickname, player1Id))
        GameState.addPlayer(Player(thisPlayerNickname, thisPlayerId))

        assert(GameState.players.size == 2)

        GameState.removePlayer(player1Id)

        assert(GameState.players.size == 1)
        assert(onPlayerRemovedCalled == 1)
    }

    @Test
    fun removeAdminTest() {
        val player1Nickname = "testPlayer1"
        val player1Id = GameState.sha256("testId1")
        val player2Nickname = "testPlayer2"
        val player2Id = GameState.sha256("testId2")
        val thisPlayerNickname = "testPlayer2"
        val thisPlayerId = "thisPlayerTestId"
        val thisPlayerIdSha = GameState.sha256(thisPlayerId)

        var onPlayerRemovedCalled = 0
        val onPlayerRemoved = { _: Player ->
            onPlayerRemovedCalled += 1
        }

        GameState.addOnPlayerRemovedCallback(onPlayerRemoved)
        GameState.addPlayer(Player(player1Nickname, player1Id, true))
        GameState.addPlayer(Player(player2Nickname, player2Id))
        GameState.addPlayer(Player(thisPlayerNickname, thisPlayerId))

        assert(GameState.players.size == 3)

        GameState.removePlayer(player1Id, player2Id)

        assert(GameState.players.size == 2)
        assert(GameState.players.find { it.isAdmin } != null)
        assert(GameState.players.find { it.isAdmin }!!.playerID == player2Id)
        assert(onPlayerRemovedCalled == 1)

        GameState.removePlayer(player2Id, thisPlayerIdSha)

        assert(GameState.players.size == 1)
        assert(GameState.players[0].playerID == thisPlayerId)
        assert(GameState.players[0].isAdmin)
        assert(onPlayerRemovedCalled == 2)
    }

    @Test
    fun removeAdminIncorrectTest() {
        val player1Nickname = "testPlayer1"
        val player1Id = GameState.sha256("testId1")
        val thisPlayerNickname = "testPlayer2"
        val thisPlayerId = "testId2"

        var onPlayerRemovedCalled = 0
        val onPlayerRemoved = { _: Player ->
            onPlayerRemovedCalled += 1
        }

        GameState.addOnPlayerRemovedCallback(onPlayerRemoved)
        GameState.addPlayer(Player(player1Nickname, player1Id, true))
        GameState.addPlayer(Player(thisPlayerNickname, thisPlayerId))

        assert(GameState.players.size == 2)

        var exceptionThrown = false
        try {
            GameState.removePlayer(player1Id)
        } catch (e: Exception) {
            exceptionThrown = true
        }

        assert(GameState.players.size == 2)
        assert(onPlayerRemovedCalled == 0)
        assert(exceptionThrown)
    }

    @Test
    fun removeThisPlayerTest() {
        val player1Nickname = "testPlayer1"
        val player1Id = GameState.sha256("testId1")
        val thisPlayerNickname = "testPlayer2"
        val thisPlayerId = "testId2"
        val thisPlayerIdSha = GameState.sha256(thisPlayerId)

        var onPlayerRemovedCalled = 0
        val onPlayerRemoved = { _: Player ->
            onPlayerRemovedCalled += 1
        }

        var onGameResetCalled = 0
        val onGameReset = {
            onGameResetCalled += 1
        }

        GameState.onGameReset = onGameReset
        GameState.addOnPlayerRemovedCallback(onPlayerRemoved)
        GameState.addPlayer(Player(player1Nickname, player1Id))
        GameState.addPlayer(Player(thisPlayerNickname, thisPlayerId))

        assert(GameState.players.size == 2)

        GameState.removePlayer(thisPlayerIdSha)

        assert(GameState.players.size == 0)
        assert(onPlayerRemovedCalled == 0)
        assert(onGameResetCalled == 1)
    }

    @Test
    fun removeIncorrectPlayerTest() {
        val player1Nickname = "testPlayer1"
        val player1Id = GameState.sha256("testId1")
        val thisPlayerNickname = "testPlayer2"
        val thisPlayerId = "testId2"

        var onPlayerRemovedCalled = 0
        val onPlayerRemoved = { _: Player ->
            onPlayerRemovedCalled += 1
        }

        GameState.addOnPlayerRemovedCallback(onPlayerRemoved)
        GameState.addPlayer(Player(player1Nickname, player1Id))
        GameState.addPlayer(Player(thisPlayerNickname, thisPlayerId))

        assert(GameState.players.size == 2)

        var exceptionThrown = false
        try {
            GameState.removePlayer("trashId")
        } catch (e: Exception) {
            exceptionThrown = true
        }

        assert(GameState.players.size == 2)
        assert(onPlayerRemovedCalled == 0)
        assert(exceptionThrown)
    }

    @Test
    fun sha256Test() {
        val testString = "asdfhwerljsbdly7yro72y4oyihdfl"
        val sha256TestString = MessageDigest
            .getInstance("SHA-256")
            .digest(testString.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }

        assertTrue("sha256 doesn't match", GameState.sha256(testString) == sha256TestString)
    }

    @After
    fun tearDown() {
        // Clean up after each test
        GameState.resetGameState()
    }
}
