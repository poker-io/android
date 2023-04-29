package com.pokerio.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
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
    fun testAddPlayer() {
        GameState.players.clear()
        GameState.addPlayer(Player("test1", "123", true))

        composeTestRule.setContent {
            LobbyScreen()
        }

        val playerList = composeTestRule.onNodeWithTag("player_list")
        playerList.onChildren().assertCountEquals(1)

        GameState.addPlayer(Player("test2", "124", true))

        playerList.onChildren().assertCountEquals(2)
    }

    @Test
    fun testUIAdmin() {
        GameState.players.clear()
        GameState.isPlayerAdmin = true
        GameState.addPlayer(Player("test1", "123", true))

        composeTestRule.setContent {
            LobbyScreen()
        }

        composeTestRule.onNodeWithTag("leave_game").assertIsDisplayed()
        composeTestRule.onNodeWithTag("update_settings").assertIsDisplayed()
        composeTestRule.onNodeWithTag("start_game").assertIsDisplayed()
    }

    @Test
    fun testUINonAdmin() {
        GameState.players.clear()
        GameState.isPlayerAdmin = false
        GameState.addPlayer(Player("test1", "123", true))
        GameState.addPlayer(Player("test2", "124", false))

        composeTestRule.setContent {
            LobbyScreen()
        }

        composeTestRule.onNodeWithTag("leave_game").assertIsDisplayed()
        composeTestRule.onNodeWithTag("update_settings").assertDoesNotExist()
        composeTestRule.onNodeWithTag("start_game").assertDoesNotExist()
    }
}
