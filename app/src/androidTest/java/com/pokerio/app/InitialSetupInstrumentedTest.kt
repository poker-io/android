package com.pokerio.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.pokerio.app.screens.InitialSetupScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class InitialSetupInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkButtonDisabled() {
        composeTestRule.setContent {
            InitialSetupScreen(exitInitialSetup = {})
        }

        val textField = composeTestRule.onNodeWithTag("nickname_input")
        val button = composeTestRule.onNodeWithTag("continue_button")
        button.assertIsDisplayed()
        textField.assertIsDisplayed()

        button.assertIsNotEnabled()
        // Blank nickname should not be possible
        textField.performTextReplacement("   ")
        button.assertIsNotEnabled()
        textField.performTextReplacement("test")
        button.assertIsEnabled()
    }

    @Test
    fun checkContinueNavigation() {
        var exitInitialSetupSuccess = false
        val exitInitialSetup = {
            exitInitialSetupSuccess = true
        }

        composeTestRule.setContent {
            InitialSetupScreen(exitInitialSetup = exitInitialSetup)
        }

        val textField = composeTestRule.onNodeWithTag("nickname_input")
        val button = composeTestRule.onNodeWithTag("continue_button")
        button.assertIsDisplayed()
        textField.assertIsDisplayed()

        textField.performTextReplacement("test")
        button.performClick()
        assertTrue("Pressing button did not exit initial setup", exitInitialSetupSuccess)
    }
}
