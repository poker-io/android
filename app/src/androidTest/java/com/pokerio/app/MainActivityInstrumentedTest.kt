package com.pokerio.app

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class MainActivityInstrumentedTest {

    @get:Rule
    val androidTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNicknameNotSet() {
        val context = androidTestRule.activity
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )
        with(sharedPreferences.edit()) {
            remove(context.getString(R.string.sharedPreferences_nickname))
            apply()
        }

        androidTestRule.activity.setContent {
            MainActivityComposable()
        }

        // Check if we are on the initial setup screen
        val button = androidTestRule.onNodeWithTag("continue_button")
        button.assertExists()
        button.assertIsDisplayed()
    }

    @Test
    fun testNicknameSet() {
        val context = androidTestRule.activity
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )
        with(sharedPreferences.edit()) {
            putString(context.getString(R.string.sharedPreferences_nickname), "nickname")
            apply()
        }

        androidTestRule.activity.setContent {
            MainActivityComposable()
        }

        // Check if we are on the home screen
        val startGameCard = androidTestRule.onNodeWithTag("StartGameCard")
        startGameCard.assertExists()
        startGameCard.assertIsDisplayed()
    }
}
