package com.pokerio.app

import androidx.compose.ui.test.junit4.createComposeRule
import com.pokerio.app.screens.LobbyScreen
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.Rule
import org.junit.Test

class LobbyInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAddPlayerAdmin() {
        GameState.players.clear()
        GameState.addPlayer(Player("test1", "123", true))

        composeTestRule.setContent {
            LobbyScreen()
        }
    }

    @Test
    fun testAddPlayerNonAdmin() {
    }
}
