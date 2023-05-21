package com.pokerio.app

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
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
    fun checkIfPlayersDisplayed() {
        val playerNickname1 = "test1"
        val playerHash1 = "testHash1"
        val playerNickname2 = "test2"
        val playerHash2 = "testHash2"

        GameState.addPlayer(Player(playerNickname1, playerHash1))
        GameState.thisPlayer = Player(playerNickname2, playerHash2)
        GameState.addPlayer(GameState.thisPlayer)

        androidTestRule.activity.setContent {
            val activity = LocalContext.current as Activity
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            GameScreen()
        }

        androidTestRule.onNodeWithTag("raise_dialog_close").assertDoesNotExist()
        androidTestRule.onNodeWithTag("call_button").performClick()
        androidTestRule.onNodeWithTag("fold_button").performClick()
        androidTestRule.onNodeWithTag("check_button").performClick()
        androidTestRule.onNodeWithTag("raise_button").performClick()
        val raiseDialogCloseButton = androidTestRule.onNodeWithTag("raise_dialog_close")
        raiseDialogCloseButton.assertIsDisplayed()
        raiseDialogCloseButton.performClick()
        androidTestRule.onNodeWithTag("raise_dialog_close").assertDoesNotExist()
    }

    @After
    fun tearDown() {
        // Clean up after each test
        GameState.resetGameState()
    }
}
