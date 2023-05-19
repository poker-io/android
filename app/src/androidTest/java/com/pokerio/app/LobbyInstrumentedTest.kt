package com.pokerio.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
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
            LobbyScreen(navigateToSettings = {})
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
            LobbyScreen(navigateToSettings = {})
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
            LobbyScreen(navigateToSettings = {})
        }

        composeTestRule.onNodeWithTag("leave_game").assertIsDisplayed()
        composeTestRule.onNodeWithTag("update_settings").assertDoesNotExist()
        composeTestRule.onNodeWithTag("start_game").assertDoesNotExist()
    }

    @Test
    fun testRecomposeOnSettingsChange() {
        GameState.players.clear()
        GameState.isPlayerAdmin = false
        GameState.startingFunds = 0
        GameState.smallBlind = 0
        GameState.addPlayer(Player("test1", "123", true))
        GameState.addPlayer(Player("test2", "124", false))

        composeTestRule.setContent {
            LobbyScreen(navigateToSettings = {})
        }

        val fundsValue = composeTestRule
            .onNodeWithTag("setting_funds")
            .onChildAt(1)
        fundsValue.assertIsDisplayed()
        fundsValue.assertTextEquals("${GameState.startingFunds}")

        val smallBlindValue = composeTestRule
            .onNodeWithTag("setting_small_blind")
            .onChildAt(1)
        smallBlindValue.assertIsDisplayed()
        smallBlindValue.assertTextEquals("${GameState.smallBlind}")

        GameState.changeGameSettings(1200, 100)
        fundsValue.assertTextEquals("${GameState.startingFunds}")
        smallBlindValue.assertTextEquals("${GameState.smallBlind}")
    }
}
