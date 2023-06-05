package com.pokerio.app

import com.pokerio.app.utils.GameCard
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.After
import org.junit.Assert.assertTrue
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

        val map = HashMap<String, String>()
        map["players"] = playersString
        map["card1"] = "01T"
        map["card2"] = "02T"

        PokerioFirebaseMessagingService.startGame(map)

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
    fun handleActionCallTest() {
        val playerNickname = "test1"
        val playerID = GameState.sha256("testHash1")
        val thisPlayerNickname = "test2"
        val thisPlayerID = "testHash2"
        val thisPlayerHash = GameState.sha256(thisPlayerID)

        GameState.addPlayer(Player(playerNickname, playerID, true))
        GameState.thisPlayer = Player(thisPlayerNickname, thisPlayerID)
        GameState.addPlayer(GameState.thisPlayer)

        val funds = 1000
        val bet = 100
        GameState.thisPlayer.funds = funds
        GameState.thisPlayer.bet = bet
        val map = HashMap<String, String>()
        map["player"] = thisPlayerHash
        PokerioFirebaseMessagingService.actionCall(map)

        assert(GameState.thisPlayer.funds == funds)
        assert(GameState.thisPlayer.bet == bet)
    }

    @Test
    fun handleActionCheckTest() {
        val playerNickname = "test1"
        val playerID = GameState.sha256("testHash1")
        val thisPlayerNickname = "test2"
        val thisPlayerID = "testHash2"
        val thisPlayerHash = GameState.sha256(thisPlayerID)

        GameState.addPlayer(Player(playerNickname, playerID, true))
        GameState.thisPlayer = Player(thisPlayerNickname, thisPlayerID)
        GameState.addPlayer(GameState.thisPlayer)

        val funds = 1000
        val bet = 100
        GameState.thisPlayer.funds = funds
        GameState.thisPlayer.bet = bet
        val map = HashMap<String, String>()
        map["player"] = thisPlayerHash
        PokerioFirebaseMessagingService.actionCheck(map)

        assert(GameState.thisPlayer.funds == funds)
        assert(GameState.thisPlayer.bet == bet)
    }

    @Test
    fun handleActionRaiseTest() {
        val playerNickname = "test1"
        val playerID = GameState.sha256("testHash1")
        val thisPlayerNickname = "test2"
        val thisPlayerID = "testHash2"
        val thisPlayerHash = GameState.sha256(thisPlayerID)

        GameState.addPlayer(Player(playerNickname, playerID, true))
        GameState.thisPlayer = Player(thisPlayerNickname, thisPlayerID)
        GameState.addPlayer(GameState.thisPlayer)

        val funds = 1000
        val bet = 100
        val raise = 200

        GameState.thisPlayer.funds = funds
        GameState.thisPlayer.bet = bet

        val map = HashMap<String, String>()
        map["player"] = thisPlayerHash
        map["actionPayload"] = raise.toString()

        PokerioFirebaseMessagingService.actionRaise(map)

        assert(GameState.thisPlayer.funds == funds - (raise - bet))
        assert(GameState.thisPlayer.bet == raise)
    }

    @Test
    fun handleActionFoldTest() {
        val playerNickname = "test1"
        val playerID = GameState.sha256("testHash1")
        val thisPlayerNickname = "test2"
        val thisPlayerID = "testHash2"

        GameState.addPlayer(Player(playerNickname, playerID, true))
        GameState.thisPlayer = Player(thisPlayerNickname, thisPlayerID)
        GameState.addPlayer(GameState.thisPlayer)

        val map = HashMap<String, String>()
        map["player"] = playerID

        PokerioFirebaseMessagingService.actionFold(map)

        assertTrue(GameState.players[0].folded)
    }

    @Test
    fun handleActionWonTest() {
        val playerNickname = "test1"
        val playerID = GameState.sha256("testHash1")
        val thisPlayerNickname = "test2"
        val thisPlayerID = "testHash2"

        GameState.addPlayer(Player(playerNickname, playerID, true))
        GameState.thisPlayer = Player(thisPlayerNickname, thisPlayerID)
        GameState.addPlayer(GameState.thisPlayer)

        val map = HashMap<String, String>()
        map["player"] = playerID

        var onWonCalled = 0
        val onWon = { player: Player ->
            onWonCalled++
            assert(player.nickname == playerNickname)
        }
        GameState.onWon = onWon

        PokerioFirebaseMessagingService.actionWon(map)

        assert(onWonCalled == 1)
    }

    @After
    fun tearDown() {
        // Clean up after each test
        GameState.resetGameState()
    }
}
