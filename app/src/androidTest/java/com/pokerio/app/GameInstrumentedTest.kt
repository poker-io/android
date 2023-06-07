package com.pokerio.app

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.pokerio.app.screens.GameScreen
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.After
import org.junit.Rule
import org.junit.Test

class GameInstrumentedTest {

    @get:Rule
    val androidTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun notEnoughFundsTest() {
        val playerNickname1 = "test1"
        val playerHash1 = "testHash1"
        val playerNickname2 = "test2"
        val playerHash2 = "testHash2"
        val winningsPool = 10000
        val biggestBet = 100
        val smallestBet = 10
        val notEnoughFunds = biggestBet - smallestBet - 1

        GameState.addPlayer(Player(playerNickname1, playerHash1, bet = biggestBet))
        GameState.thisPlayer = Player(playerNickname2, playerHash2, bet = smallestBet, funds = notEnoughFunds)
        GameState.addPlayer(GameState.thisPlayer)
        GameState.winningsPool = winningsPool
        GameState.currentPlayer = GameState.thisPlayer

        androidTestRule.activity.setContent {
            val activity = LocalContext.current as Activity
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            GameScreen()
        }

        androidTestRule.onNodeWithTag("raise_dialog_close").assertDoesNotExist()
        androidTestRule.onNodeWithTag("call_button").assertDoesNotExist()
        androidTestRule.onNodeWithTag("fold_button").performClick()
        androidTestRule.onNodeWithTag("check_button").assertDoesNotExist()
        androidTestRule.onNodeWithTag("raise_button").assertDoesNotExist()
        androidTestRule.onNodeWithTag("raise_dialog_close").assertDoesNotExist()
        val winningsPoolText = androidTestRule.onNodeWithTag("winnings_pool")
        winningsPoolText.assertExists()
        winningsPoolText.assert(hasText("$winningsPool$"))
    }

    @Test
    fun betEnoughTest() {
        val playerNickname1 = "test1"
        val playerHash1 = "testHash1"
        val playerNickname2 = "test2"
        val playerHash2 = "testHash2"
        val winningsPool = 10000
        val biggestBet = 100

        GameState.addPlayer(Player(playerNickname1, playerHash1, bet = biggestBet))
        GameState.thisPlayer = Player(playerNickname2, playerHash2, bet = biggestBet)
        GameState.addPlayer(GameState.thisPlayer)
        GameState.winningsPool = winningsPool
        GameState.currentPlayer = GameState.thisPlayer

        androidTestRule.activity.setContent {
            val activity = LocalContext.current as Activity
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            GameScreen()
        }

        androidTestRule.onNodeWithTag("raise_dialog_close").assertDoesNotExist()
        androidTestRule.onNodeWithTag("call_button").assertDoesNotExist()
        androidTestRule.onNodeWithTag("fold_button").performClick()
        androidTestRule.onNodeWithTag("check_button").performClick()
        androidTestRule.onNodeWithTag("raise_button").assertDoesNotExist()
        androidTestRule.onNodeWithTag("raise_dialog_close").assertDoesNotExist()
        val winningsPoolText = androidTestRule.onNodeWithTag("winnings_pool")
        winningsPoolText.assertExists()
        winningsPoolText.assert(hasText("$winningsPool$"))
    }

    @Test
    fun hasEnoughFundsTest() {
        val playerNickname1 = "test1"
        val playerHash1 = "testHash1"
        val playerNickname2 = "test2"
        val playerHash2 = "testHash2"
        val winningsPool = 10000
        val biggestBet = 100
        val smallestBet = 10
        val enoughFunds = biggestBet - smallestBet + 1

        GameState.addPlayer(Player(playerNickname1, playerHash1, bet = biggestBet))
        GameState.thisPlayer = Player(playerNickname2, playerHash2, bet = smallestBet, funds = enoughFunds)
        GameState.addPlayer(GameState.thisPlayer)
        GameState.winningsPool = winningsPool
        GameState.currentPlayer = GameState.thisPlayer

        androidTestRule.activity.setContent {
            val activity = LocalContext.current as Activity
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            GameScreen()
        }

        androidTestRule.onNodeWithTag("raise_dialog_close").assertDoesNotExist()
        androidTestRule.onNodeWithTag("call_button").performClick()
        androidTestRule.onNodeWithTag("fold_button").performClick()
        androidTestRule.onNodeWithTag("check_button").assertDoesNotExist()
        androidTestRule.onNodeWithTag("raise_button").performClick()
        val raiseDialogCloseButton = androidTestRule.onNodeWithTag("raise_dialog_close")
        raiseDialogCloseButton.assertIsDisplayed()
        raiseDialogCloseButton.performClick()
        androidTestRule.onNodeWithTag("raise_dialog_close").assertDoesNotExist()
        val winningsPoolText = androidTestRule.onNodeWithTag("winnings_pool")
        winningsPoolText.assertExists()
        winningsPoolText.assert(hasText("$winningsPool$"))
    }

    @Test
    fun notPlayersTurnTest() {
        val playerNickname1 = "test1"
        val playerHash1 = "testHash1"
        val playerNickname2 = "test2"
        val playerHash2 = "testHash2"
        val winningsPool = 10000

        GameState.addPlayer(Player(playerNickname1, playerHash1))
        GameState.thisPlayer = Player(playerNickname2, playerHash2)
        GameState.addPlayer(GameState.thisPlayer)
        GameState.winningsPool = winningsPool

        androidTestRule.activity.setContent {
            val activity = LocalContext.current as Activity
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            GameScreen()
        }

        androidTestRule.onNodeWithTag("raise_dialog_close").assertDoesNotExist()
        androidTestRule.onNodeWithTag("call_button").assertDoesNotExist()
        androidTestRule.onNodeWithTag("fold_button").assertDoesNotExist()
        androidTestRule.onNodeWithTag("check_button").assertDoesNotExist()
        androidTestRule.onNodeWithTag("raise_button").assertDoesNotExist()
        val winningsPoolText = androidTestRule.onNodeWithTag("winnings_pool")
        winningsPoolText.assertExists()
        winningsPoolText.assert(hasText("$winningsPool$"))
    }

    @After
    fun tearDown() {
        // Clean up after each test
        GameState.resetGameState()
    }
}
