package com.pokerio.app

import com.pokerio.app.utils.GameState
import org.junit.Test

class FirebaseMessagesTest {

    @Test
    fun playerJoinedTest() {
        val playerName = "Player432"
        val playerHash = "123"

        val map = HashMap<String, String>()
        map["nickname"] = playerName
        map["playerHash"] = playerHash

        GameState.players.clear()
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
}
