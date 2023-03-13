package com.pokerio.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule

import org.junit.Test

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class HomeScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun basicTest() {
        // Start the app
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.onNodeWithTag("image_caption", useUnmergedTree = true)
            .assertDoesNotExist()
        composeTestRule.onRoot().performClick()
        composeTestRule.onNodeWithTag("image_caption", useUnmergedTree = true)
            .assertIsDisplayed()
    }
}