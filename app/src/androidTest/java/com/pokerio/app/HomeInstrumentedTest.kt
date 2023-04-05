package com.pokerio.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.pokerio.app.screens.HomeScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkSettingsNavigation() {
        // Start the app
        var settingsNavigationSuccess = false
        val navigateToSettings = {
            settingsNavigationSuccess = true
        }

        composeTestRule.setContent {
            HomeScreen(
                navigateToSettings = navigateToSettings,
                navigateToLobby = {}
            )
        }
        val settingsButton = composeTestRule.onNodeWithTag("settings_button")

        settingsButton.assertIsDisplayed()
        settingsButton.performClick()
        assertTrue("navigateToSettings wasn't called!", settingsNavigationSuccess)
    }
}
