package com.pokerio.app

import com.pokerio.app.utils.GameCard
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class GameStateTest {
    @Test
    fun setup() {
        // Make sure we're using a clean game state
        GameState.resetGameState()
    }

    @Test
    fun addRemovePlayerTest() {
        GameState.gameID = "gameID"
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
        val onPlayerAdded = { player: Player ->
            playerAddedCalledCounter++
            assertTrue("Player passed to callback incorrectly", player == testPlayer)
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
        val onPlayerRemoved = { player: Player ->
            playerRemovedCalledCounter++
            assertTrue("Player passed to callback incorrectly", player == testPlayer)
        }
        val playerRemovedCallbackId = GameState.addOnPlayerRemovedCallback(onPlayerRemoved)

        GameState.gameID = "gameID"
        GameState.addPlayer(testPlayer)
        assertTrue("onPlayerRemoved callback should not have been called", playerRemovedCalledCounter == 0)
        GameState.removePlayer(testPlayer.playerID)
        assertTrue("onPlayerRemoved callback not called or called too many times", playerRemovedCalledCounter == 1)

        GameState.removeOnPlayerRemovedCallback(playerRemovedCallbackId)
        GameState.addPlayer(testPlayer)
        GameState.removePlayer(testPlayer.playerID)
        assertTrue("onPlayerRemoved callback not removed", playerRemovedCalledCounter == 1)
    }

    @Test
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
    fun onNewActionCallbackTest() {
        val playerNickname = "test"
        val playerID = GameState.sha256("testId")

        GameState.addPlayer(Player(playerNickname, playerID))

        var onNewActionCalled = 0
        val onNewAction = { player: Player? ->
            onNewActionCalled++

            require(player != null)
            assert(player.nickname == playerNickname)
            assert(player.playerID == playerID)
        }

        val callbackId = GameState.addOnNewActionCallback(onNewAction)
        GameState.handleActionFold(playerID)
        assertTrue("onNewAction not called", onNewActionCalled == 1)

        GameState.removeOnNewActionCallback(callbackId)
        GameState.handleActionFold(playerID)
        assertTrue("onNewAction called after removal", onNewActionCalled == 1)
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
        GameState.thisPlayer.isAdmin = true
        GameState.onGameReset = onResetState
        GameState.gameCard1 = GameCard.none()
        GameState.gameCard2 = GameCard.none()
        GameState.cards[0] = GameCard.fromString("01K")

        GameState.resetGameState()
        assertTrue("gameID not reset", GameState.gameID.isEmpty())
        assertTrue("players list not reset", GameState.players.isEmpty())
        assertTrue("startingFunds not reset", GameState.startingFunds == -1)
        assertTrue("smallBlind not reset", GameState.smallBlind == -1)
        assertFalse("isPlayerAdmin not reset", GameState.thisPlayer.isAdmin)
        assertTrue("onGameReset not called", resetCalled)
        assertTrue("card1 not reset", GameState.gameCard1.isNone())
        assertTrue("card2 not reset", GameState.gameCard2.isNone())
        assertTrue("cards array not reset", GameState.cards[0].isNone())
    }

    @Test
    fun removeNonAdminTest() {
        val player1Nickname = "testPlayer1"
        val player1Id = GameState.sha256("testId1")
        val thisPlayerNickname = "testPlayer2"
        val thisPlayerId = "testId2"

        var onPlayerRemovedCalled = 0
        val onPlayerRemoved = { player: Player ->
            assert(player.nickname == player1Nickname)
            assert(player.playerID == player1Id)

            onPlayerRemovedCalled += 1
        }

        GameState.gameID = "gameID"
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

        GameState.gameID = "gameID"
        GameState.addOnPlayerRemovedCallback(onPlayerRemoved)
        GameState.addPlayer(Player(player1Nickname, player1Id, true))
        GameState.addPlayer(Player(player2Nickname, player2Id))
        GameState.thisPlayer = Player(thisPlayerNickname, thisPlayerId)
        GameState.addPlayer(GameState.thisPlayer)

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

        GameState.gameID = "gameID"
        GameState.addOnPlayerRemovedCallback(onPlayerRemoved)
        GameState.addPlayer(Player(player1Nickname, player1Id, true))
        GameState.addPlayer(Player(thisPlayerNickname, thisPlayerId))

        assert(GameState.players.size == 2)

        assertThrows(IllegalArgumentException::class.java) {
            GameState.removePlayer(player1Id)
        }

        assert(GameState.players.size == 2)
        assert(onPlayerRemovedCalled == 0)
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

        GameState.gameID = "gameID"
        GameState.onGameReset = onGameReset
        GameState.addOnPlayerRemovedCallback(onPlayerRemoved)
        GameState.addPlayer(Player(player1Nickname, player1Id))
        GameState.thisPlayer = Player(thisPlayerNickname, thisPlayerId)
        GameState.addPlayer(GameState.thisPlayer)

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

        GameState.gameID = "gameID"
        GameState.addOnPlayerRemovedCallback(onPlayerRemoved)
        GameState.addPlayer(Player(player1Nickname, player1Id))
        GameState.addPlayer(Player(thisPlayerNickname, thisPlayerId))

        assert(GameState.players.size == 2)

        assertThrows(IllegalArgumentException::class.java) {
            GameState.removePlayer("trashId")
        }

        assert(GameState.players.size == 2)
        assert(onPlayerRemovedCalled == 0)
    }

    @Test
    fun sha256Test() {
        val testString = "asdfhwerljsbdly7yro72y4oyihdfl"
        val sha256TestString = MessageDigest
            .getInstance("SHA-256")
            .digest(testString.toByteArray())
            .fold("") { str, byte -> str + "%02x".format(byte) }

        assertTrue("sha256 doesn't match", GameState.sha256(testString) == sha256TestString)
    }

    @Test
    fun startGameTest() {
        val playerNickname = "test1"
        val playerID = GameState.sha256("testHash1")
        val thisPlayerNickname = "test2"
        val thisPlayerID = "testHash2"
        val thisPlayerHash = GameState.sha256(thisPlayerID)

        GameState.addPlayer(Player(playerNickname, playerID, true))
        GameState.thisPlayer = Player(thisPlayerNickname, thisPlayerID)
        GameState.addPlayer(GameState.thisPlayer)

        var onGameStartedCalled = 0
        GameState.onGameStart = { onGameStartedCalled++ }

        val playersString = """[
            |   {
            |       "nickname": "$playerNickname",
            |       "playerHash": "$playerID",
            |       "turn": 1
            |   },
            |   {
            |       "nickname": "$thisPlayerNickname",
            |       "playerHash": "$thisPlayerHash",
            |       "turn": 0
            |   }
            |]
        """.trimMargin()

        GameState.startGame("01T", "02T", playersString)

        assert(GameState.players[0].nickname == thisPlayerNickname)
        assert(GameState.players[0].playerID == thisPlayerID)
        assert(GameState.players[1].nickname == playerNickname)
        assert(GameState.players[1].playerID == playerID)
        assert(GameState.gameCard1.suit == GameCard.Suit.Club)
        assert(GameState.gameCard1.value == 1)
        assert(GameState.gameCard2.suit == GameCard.Suit.Club)
        assert(GameState.gameCard2.value == 2)
        assert(onGameStartedCalled == 1)
    }

    @Test
    fun testGetMaxBet() {
        assertThrows(NoSuchElementException::class.java) {
            GameState.getMaxBet()
        }

        GameState.addPlayer(Player("test1", "testHash1", bet = 100))
        GameState.addPlayer(Player("test2", "testHash2", bet = 101))
        GameState.addPlayer(Player("test3", "testHash3", bet = 102))

        assert(GameState.getMaxBet() == 102)
    }

    @Test
    fun newCardsTest() {
        val card1 = "01K"
        val card2 = "12O"
        val card3 = "08T"
        val card4 = "04P"
        val card5 = "09O"

        // Second round
        val cardsArray = mutableListOf<String>()
        cardsArray.clear()
        cardsArray.add(card1)
        cardsArray.add(card2)
        cardsArray.add(card3)

        GameState.newCards(cardsArray)

        assert(GameState.cards[0].valueString() == GameCard.fromString(card1).valueString())
        assert(GameState.cards[1].valueString() == GameCard.fromString(card2).valueString())
        assert(GameState.cards[2].valueString() == GameCard.fromString(card3).valueString())
        assert(GameState.cards[3].isNone())
        assert(GameState.cards[4].isNone())

        // Third round
        cardsArray.clear()
        cardsArray.add(card4)

        GameState.newCards(cardsArray)

        assert(GameState.cards[0].valueString() == GameCard.fromString(card1).valueString())
        assert(GameState.cards[1].valueString() == GameCard.fromString(card2).valueString())
        assert(GameState.cards[2].valueString() == GameCard.fromString(card3).valueString())
        assert(GameState.cards[3].valueString() == GameCard.fromString(card4).valueString())
        assert(GameState.cards[4].isNone())

        // Last round
        cardsArray.clear()
        cardsArray.add(card5)

        GameState.newCards(cardsArray)

        assert(GameState.cards[0].valueString() == GameCard.fromString(card1).valueString())
        assert(GameState.cards[1].valueString() == GameCard.fromString(card2).valueString())
        assert(GameState.cards[2].valueString() == GameCard.fromString(card3).valueString())
        assert(GameState.cards[3].valueString() == GameCard.fromString(card4).valueString())
        assert(GameState.cards[4].valueString() == GameCard.fromString(card5).valueString())
    }

    @Test
    fun singleWinnerTest() {
        val baseFunds = 100
        val winAmount = 100

        val player1 = Player("player1", "id1", true)
        val player2 = Player("player2", GameState.sha256("id2"), false)
        val player3 = Player("player3", GameState.sha256("id3"), false)

        player1.funds = baseFunds
        player2.funds = baseFunds
        player3.funds = baseFunds

        player1.bet = 123
        player2.bet = 123
        player3.bet = 123

        player2.folded = true

        GameState.thisPlayer = player1
        GameState.addPlayer(player1)
        GameState.addPlayer(player2)
        GameState.addPlayer(player3)

        val winners = listOf(GameState.sha256(player1.playerID))

        GameState.handleActionWon(winners, winAmount)

        assert(player1.funds == baseFunds + winAmount)
        assert(player1.bet == 0)
        assert(player2.funds == baseFunds)
        assert(player2.bet == 0)
        assertFalse(player2.folded)
        assert(player3.funds == baseFunds)
        assert(player3.bet == 0)
    }

    @Test
    fun multipleWinnersTest() {
        val baseFunds = 100
        val winAmount = 100

        val player1 = Player("player1", "id1", true)
        val player2 = Player("player2", GameState.sha256("id2"), false)
        val player3 = Player("player3", GameState.sha256("id3"), false)

        player1.funds = baseFunds
        player2.funds = baseFunds
        player3.funds = baseFunds

        player1.bet = 123
        player2.bet = 123
        player3.bet = 123

        GameState.thisPlayer = player1
        GameState.addPlayer(player1)
        GameState.addPlayer(player2)
        GameState.addPlayer(player3)

        val winners = listOf(player2.playerID, player3.playerID)

        GameState.handleActionWon(winners, winAmount)

        assert(player1.funds == baseFunds)
        assert(player1.bet == 0)
        assert(player2.funds == baseFunds + winAmount)
        assert(player2.bet == 0)
        assert(player3.funds == baseFunds + winAmount)
        assert(player3.bet == 0)
    }

    @Test
    fun allWinnersTest() {
        val baseFunds = 100
        val winAmount = 100

        val player1 = Player("player1", "id1", true)
        val player2 = Player("player2", GameState.sha256("id2"), false)
        val player3 = Player("player3", GameState.sha256("id3"), false)

        player1.funds = baseFunds
        player2.funds = baseFunds
        player3.funds = baseFunds

        player1.bet = 123
        player2.bet = 123
        player3.bet = 123

        GameState.thisPlayer = player1
        GameState.addPlayer(player1)
        GameState.addPlayer(player2)
        GameState.addPlayer(player3)

        val winners = listOf(GameState.sha256(player1.playerID), player2.playerID, player3.playerID)

        GameState.handleActionWon(winners, winAmount)

        assert(player1.funds == baseFunds + winAmount)
        assert(player1.bet == 0)
        assert(player2.funds == baseFunds + winAmount)
        assert(player2.bet == 0)
        assert(player3.funds == baseFunds + winAmount)
        assert(player3.bet == 0)
    }

    @Test
    fun checkIfServerAddressCorrect() {
        // We change this all the time for testing locally. Let's just make sure it is correct, when
        // we want to merge into main

        assert(GameState.BASE_URL == "http://158.101.160.143:42069")
    }

    @After
    fun tearDown() {
        // Clean up after each test
        GameState.resetGameState()
    }
}
