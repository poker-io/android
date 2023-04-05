package com.pokerio.app

import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.Assert.assertTrue
import org.junit.Test

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
        assertTrue("onPlayerAdded callback not called or called to many times", playerAddedCalledCounter == 1)
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
        assertTrue("onPlayerRemoved callback not called or called to many times", playerRemovedCalledCounter == 1)

        GameState.removeOnPlayerRemovedCallback(playerRemovedCallbackId)
        GameState.addPlayer(testPlayer)
        GameState.removePlayer(testPlayer.playerID)
        assertTrue("onPlayerRemoved callback not removed", playerRemovedCalledCounter == 1)
    }
}
