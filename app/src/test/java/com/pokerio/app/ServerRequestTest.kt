package com.pokerio.app

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as mockWhen

class ServerRequestTest {
    private val server = MockWebServer()
    private var port = 42069
    private var url = ""

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    @Before
    fun setup() {
        // Setup mocks
        context = mock(Context::class.java)
        sharedPreferences = mock(SharedPreferences::class.java)
        editor = mock(Editor::class.java)

        mockWhen(
            context.getSharedPreferences(
                context.getString(R.string.shared_preferences_file),
                Context.MODE_PRIVATE
            )
        ).thenReturn(sharedPreferences)
        mockWhen(sharedPreferences.edit()).thenReturn(editor)

        // Start server on localhost
        server.start(port)
        url = server.url("/").toString().trimEnd { it == '/' }

        // Make sure we're using a clean game state
        GameState.resetGameState()
    }

    @Test
    fun createGameRequestTest() {
        val gameKey = "123456"
        val startingFunds = 2137
        val smallBlind = 1000

        server.enqueue(
            MockResponse().setBody(
                """{
                    "gameKey": $gameKey,
                    "startingFunds": $startingFunds,
                    "smallBlind": $smallBlind
                }
                """.trimMargin()
            )
        )

        var onSuccessCalled = 0
        val onSuccess = {
            onSuccessCalled += 1
        }

        val onError = {
            assertTrue("CreateGame should not error", false)
        }

        runBlocking {
            GameState.createGameRequest(context, onSuccess, onError, url, "idTest")
        }

        assert(GameState.gameID == gameKey)
        assert(GameState.startingFunds == startingFunds)
        assert(GameState.smallBlind == smallBlind)
        assert(onSuccessCalled == 1)
    }

    @Test
    fun createGameWrongResponseTest() {
        server.enqueue(MockResponse().setBody(""))

        val onSuccess = {
            assertTrue("CreateGame should error", false)
        }

        var onErrorCalled = 0
        val onError = {
            onErrorCalled += 1
        }

        runBlocking {
            GameState.createGameRequest(context, onSuccess, onError, url, "idTest")
        }

        assert(onErrorCalled == 1)
    }

    @Test
    fun joinGameRequestTest() {
        val gameKey = "123456"
        val gameMasterHash = "123456789"
        val startingFunds = 1000
        val smallBlind = 100

        server.enqueue(
            MockResponse().setBody(
                """{
                "gameMasterHash": "$gameMasterHash",
                "startingFunds": $startingFunds,
                "smallBlind": $smallBlind,
                "players": [
                    {
                        "nickname": "test1",
                        "playerHash": "$gameMasterHash"
                    }
                ]
            }
                """.trimMargin()
            )
        )

        var onSuccessCalled = 0
        val onSuccess = {
            onSuccessCalled += 1
        }

        val onError = {
            assertTrue("JoinGame should not error", false)
        }

        runBlocking {
            GameState.joinGameRequest(gameKey, context, onSuccess, onError, url, "idTest")
        }

        assert(GameState.gameID == gameKey)
        assert(GameState.startingFunds == startingFunds)
        assert(GameState.smallBlind == smallBlind)
        // There are only two players
        assert(GameState.players.size == 2)
        // Only one of the players is an admin
        assert(GameState.players[0].isAdmin.xor(GameState.players[1].isAdmin))
        // Check their names and ids
        assert(
            GameState.players[0].nickname == "test1" &&
                GameState.players[0].playerID == gameMasterHash ||
                GameState.players[1].nickname == "test1" &&
                GameState.players[1].playerID == gameMasterHash
        )
        assert(
            GameState.players[0].playerID == "idTest" || GameState.players[1].playerID == "idTest"
        )
        assert(onSuccessCalled == 1)
    }

    @Test
    fun joinGameWrongResponseTest() {
        server.enqueue(MockResponse().setBody(""))

        val onSuccess = {
            assertTrue("JoinGame should error", false)
        }

        var onErrorCalled = 0
        val onError = {
            onErrorCalled += 1
        }

        runBlocking {
            GameState.joinGameRequest("123456", context, onSuccess, onError, url, "idTest")
        }

        assert(onErrorCalled == 1)
    }

    @Test
    fun kickPlayerCorrectResponseTest() {
        server.enqueue(MockResponse().setBody(""))

        var onSuccessCalled = 0
        val onSuccess = {
            onSuccessCalled += 1
        }

        val onError = {
            assertTrue("KickPlayer should not error", false)
        }

        runBlocking {
            GameState.kickPlayerRequest("testId", onSuccess, onError, url, "idTest")
        }

        assert(onSuccessCalled == 1)
    }

    @Test
    fun kickPlayerWrongResponseTest() {
        server.enqueue(MockResponse().setResponseCode(400))

        val onSuccess = {
            assertTrue("KickPlayer should error", false)
        }

        var onErrorCalled = 0
        val onError = {
            onErrorCalled += 1
        }

        runBlocking {
            GameState.kickPlayerRequest("testId", onSuccess, onError, url, "idTest")
        }

        assert(onErrorCalled == 1)
    }

    @Test
    fun leaveGameRequestTest() {
        GameState.gameID = "123456"

        server.enqueue(MockResponse().setResponseCode(200))

        var onSuccessCalled = 0
        val onSuccess = {
            onSuccessCalled += 1
        }

        val onError = {
            assertTrue("LeaveGame should not error", false)
        }

        runBlocking {
            GameState.leaveGameRequest(onSuccess, onError, url, "idTest")
        }

        assert(GameState.gameID == "")
        assert(GameState.players.isEmpty())
        assert(onSuccessCalled == 1)
    }

    @Test
    fun leaveGameWrongResponseTest() {
        val gameId = "123456"
        GameState.gameID = gameId
        GameState.addPlayer(Player("testPlayer", "testHash"))

        server.enqueue(MockResponse().setResponseCode(500))

        val onSuccess = {
            assertTrue("LeaveGame should error", false)
        }

        var onErrorCalled = 0
        val onError = {
            onErrorCalled += 1
        }

        runBlocking {
            GameState.leaveGameRequest(onSuccess, onError, url, "idTest")
        }

        assert(GameState.gameID == gameId)
        assert(GameState.players.size == 1)
        assert(onErrorCalled == 1)
    }

    @Test
    fun modifyGameRequestTest() {
        GameState.gameID = "123456"

        server.enqueue(MockResponse().setResponseCode(200))

        var onSuccessCalled = 0
        val onSuccess = {
            onSuccessCalled += 1
        }

        val onError = {
            assertTrue("ModifyGame should not error", false)
        }

        runBlocking {
            GameState.modifyGameRequest(2137, 271837, onSuccess, onError, url, "idTest")
        }

        assert(onSuccessCalled == 1)
    }

    @Test
    fun modifyGameWrongResponseTest() {
        GameState.gameID = "123456"

        server.enqueue(MockResponse().setResponseCode(400))

        val onSuccess = {
            assertTrue("ModifyGame should error", false)
        }

        var onErrorCalled = 0
        val onError = {
            onErrorCalled += 1
        }

        runBlocking {
            GameState.modifyGameRequest(100, 1000, onSuccess, onError, url, "idTest")
        }

        assert(onErrorCalled == 1)
    }

    @Test
    fun startGameRequestTest() {
        GameState.gameID = "123456"

        server.enqueue(MockResponse().setResponseCode(200))

        var onSuccessCalled = 0
        val onSuccess = {
            onSuccessCalled += 1
        }

        val onError = {
            assertTrue("StartGame should not error", false)
        }

        runBlocking {
            GameState.startGameRequest(onSuccess, onError, url, "idTest")
        }

        assert(onSuccessCalled == 1)
    }

    @Test
    fun startGameWrongResponseTest() {
        GameState.gameID = "123456"

        server.enqueue(MockResponse().setResponseCode(400))

        val onSuccess = {
            assertTrue("StartGame should error", false)
        }

        var onErrorCalled = 0
        val onError = {
            onErrorCalled += 1
        }

        runBlocking {
            GameState.startGameRequest(onSuccess, onError, url, "idTest")
        }

        assert(onErrorCalled == 1)
    }

    @After
    fun tearDown() {
        server.shutdown()

        // Clean up after each test
        GameState.resetGameState()
    }
}
