package com.pokerio.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import com.pokerio.app.screens.LobbyScreen
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.Rule
import org.junit.Test

class LobbyInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAddingPlayer() {
        GameState.players.clear()
        GameState.addPlayer(Player("test1", "123", true))

        composeTestRule.setContent {
            LobbyScreen()
        }

        val column = composeTestRule.onNodeWithTag("lobby_column")
        // 1 - game code, 2 - "players:", 3 - us
        column.onChildren().assertCountEquals(3)

        GameState.addPlayer(Player("test2", "321"))
        column.onChildren().assertCountEquals(4)
    }
}
