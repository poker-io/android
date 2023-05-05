package com.pokerio.app

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.pokerio.app.utils.GameState
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
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

        val onSuccess = {}

        val onError = {
            assertThat("CreateGame should not error", false)
        }

        runBlocking {
            GameState.createGameRequestSuspend(context, onSuccess, onError, url, "idTest")
        }

        assert(GameState.gameID == gameKey)
        assert(GameState.startingFunds == startingFunds)
        assert(GameState.smallBlind == smallBlind)
    }

    @Test
    fun createGameWrongResponseTest() {
        server.enqueue(MockResponse().setBody(""))

        val onSuccess = {
            assertThat("CreateGame should error", false)
        }

        var onErrorCalled = 0
        val onError = {
            onErrorCalled += 1
        }

        runBlocking {
            GameState.createGameRequestSuspend(context, onSuccess, onError, url, "idTest")
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

        val onSuccess = {}

        val onError = {
            assertThat("JoinGame should not error", false)
        }

        runBlocking {
            GameState.joinGameRequestSuspend(gameKey, context, onSuccess, onError, url, "idTest")
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
    }

    @Test
    fun joinGameWrongResponseTest() {
        server.enqueue(MockResponse().setBody(""))

        val onSuccess = {
            assertThat("JoinGame should error", false)
        }

        var onErrorCalled = 0
        val onError = {
            onErrorCalled += 1
        }

        runBlocking {
            GameState.joinGameRequestSuspend("123456", context, onSuccess, onError, url, "idTest")
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
            assertThat("KickPlayer should not error", false)
        }

        runBlocking {
            GameState.kickPlayerRequestSuspend("testId", onSuccess, onError, url, "idTest")
        }

        assert(onSuccessCalled == 1)
    }

    @Test
    fun kickPlayerWrongResponseTest() {
        server.enqueue(MockResponse().setResponseCode(400))

        val onSuccess = {
            assertThat("KickPlayer should error", false)
        }

        var onErrorCalled = 0
        val onError = {
            onErrorCalled += 1
        }

        runBlocking {
            GameState.kickPlayerRequestSuspend("testId", onSuccess, onError, url, "idTest")
        }

        assert(onErrorCalled == 1)
    }

    @Test
    fun leaveGameRequestTest() {
        GameState.gameID = "123456"

        server.enqueue(MockResponse().setResponseCode(200))

        val onSuccess = {}

        val onError = {
            assertThat("JoinGame should not error", false)
        }

        runBlocking {
            GameState.leaveGameRequestSuspend(onSuccess, onError, url, "idTest")
        }

        assert(GameState.gameID == "")
        assert(GameState.players.isEmpty())
    }

    @After
    fun tearDown() {
        server.shutdown()

        // Clean up after each test
        GameState.resetGameState()
    }
}
