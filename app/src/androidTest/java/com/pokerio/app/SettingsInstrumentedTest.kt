package com.pokerio.app

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.pokerio.app.screens.Selector
import com.pokerio.app.screens.SettingsScreen
import com.pokerio.app.utils.GameState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsInstrumentedTest {

    @get:Rule
    val androidTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNicknameSetter() {
        val testNickname = "os391j2kd9"
        val replaceNickname = "jqi93sl138"

        val context = androidTestRule.activity
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )

        with(sharedPreferences.edit()) {
            putString(context.getString(R.string.sharedPreferences_nickname), testNickname)
            apply()
        }

        androidTestRule.activity.setContent {
            SettingsScreen(navigateBack = {})
        }

        val textField = androidTestRule.onNodeWithTag("settings_nickname")
        textField.assertExists()
        textField.assertIsDisplayed()
        textField.assert(hasText(testNickname))
        textField.performTextReplacement(replaceNickname)
        textField.assert(hasText(replaceNickname))

        val backButton = androidTestRule.onNodeWithTag("settings_back")
        backButton.performClick()

        val newNickname = sharedPreferences.getString(
            context.getString(R.string.sharedPreferences_nickname),
            ""
        )
        assert(newNickname == replaceNickname)
    }

    @Test
    fun testNicknameSetterBlank() {
        val testNickname = "os391j2kd9"
        val replaceNickname = ""

        val context = androidTestRule.activity
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )

        with(sharedPreferences.edit()) {
            putString(context.getString(R.string.sharedPreferences_nickname), testNickname)
            apply()
        }

        androidTestRule.activity.setContent {
            SettingsScreen(navigateBack = {})
        }

        val textField = androidTestRule.onNodeWithTag("settings_nickname")
        textField.assertExists()
        textField.assertIsDisplayed()
        textField.assert(hasText(testNickname))
        textField.performTextReplacement(replaceNickname)
        textField.assert(hasText(replaceNickname))

        val backButton = androidTestRule.onNodeWithTag("settings_back")
        backButton.performClick()

        val newNickname = sharedPreferences.getString(
            context.getString(R.string.sharedPreferences_nickname),
            ""
        )
        assert(newNickname == testNickname)
    }

    @Test
    fun testNicknameSetterToLong() {
        val testNickname = "os391j2kd9"
        val replaceNickname = "012345678901234567890"

        val context = androidTestRule.activity
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )

        with(sharedPreferences.edit()) {
            putString(context.getString(R.string.sharedPreferences_nickname), testNickname)
            apply()
        }

        androidTestRule.activity.setContent {
            SettingsScreen(navigateBack = {})
        }

        val textField = androidTestRule.onNodeWithTag("settings_nickname")
        textField.assertExists()
        textField.assertIsDisplayed()
        textField.assert(hasText(testNickname))
        textField.performTextReplacement(replaceNickname)
        textField.assert(hasText(replaceNickname))

        val backButton = androidTestRule.onNodeWithTag("settings_back")
        backButton.performClick()

        val newNickname = sharedPreferences.getString(
            context.getString(R.string.sharedPreferences_nickname),
            ""
        )
        assert(newNickname == testNickname)
    }

    @Test
    fun testNicknameSetterInGame() {
        GameState.gameID = "699"
        androidTestRule.activity.setContent {
            SettingsScreen(navigateBack = {})
        }

        val textField = androidTestRule.onNodeWithTag("settings_nickname")
        textField.assertDoesNotExist()

        GameState.resetGameState()
    }

    @Test
    fun testNavigateBackNavbar() {
        var navigateBackSuccess = false
        val navigateBack = {
            navigateBackSuccess = true
        }

        androidTestRule.activity.setContent {
            SettingsScreen(navigateBack = navigateBack)
        }

        androidTestRule.onNodeWithTag("settings_back").performClick()
        assertTrue("Back navigation failed in settings", navigateBackSuccess)
    }

    @Test
    fun testSelector() {
        var selectedValue = 15
        val onValueSelected = { it: Int ->
            selectedValue = it
        }

        androidTestRule.activity.setContent {
            Selector(
                minValue = 10f,
                maxValue = 20f,
                initialValue = selectedValue.toFloat(),
                onValueSelected = onValueSelected
            )
        }

        // Assert initial values
        androidTestRule.onNodeWithTag("selector_slider")
            .assertRangeInfoEquals(ProgressBarRangeInfo(current = 0.5f, range = 0.0f..1.0f, steps = 0))
        androidTestRule.onNodeWithTag("slider_text").assert(hasText("15"))

        // Increase by 10 (over limit)
        androidTestRule.onNodeWithTag("slider+10").performClick()
        androidTestRule.onNodeWithTag("selector_slider")
            .assertRangeInfoEquals(ProgressBarRangeInfo(current = 1.0f, range = 0.0f..1.0f, steps = 0))
        androidTestRule.onNodeWithTag("slider_text").assert(hasText("20"))

        // Decrease by 20 (over limit)
        androidTestRule.onNodeWithTag("slider-10").performClick()
        androidTestRule.onNodeWithTag("slider-10").performClick()
        androidTestRule.onNodeWithTag("selector_slider")
            .assertRangeInfoEquals(ProgressBarRangeInfo(current = 0.0f, range = 0.0f..1.0f, steps = 0))
        androidTestRule.onNodeWithTag("slider_text").assert(hasText("10"))

        // Increase by 10 (within limit)
        androidTestRule.onNodeWithTag("slider+10").performClick()
        androidTestRule.onNodeWithTag("selector_slider")
            .assertRangeInfoEquals(ProgressBarRangeInfo(current = 1.0f, range = 0.0f..1.0f, steps = 0))
        androidTestRule.onNodeWithTag("slider_text").assert(hasText("20"))
    }

    @Test
    fun newlineNicknameTest() {
        val testNickname = "test\n"
        val correctNickname = "test"

        val context = androidTestRule.activity
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )

        androidTestRule.activity.setContent {
            SettingsScreen(navigateBack = {})
        }

        val textField = androidTestRule.onNodeWithTag("settings_nickname")
        textField.assertExists()
        textField.assertIsDisplayed()
        textField.performTextReplacement(testNickname)

        val backButton = androidTestRule.onNodeWithTag("settings_back")
        backButton.performClick()

        val newNickname = sharedPreferences.getString(
            context.getString(R.string.sharedPreferences_nickname),
            ""
        )
        assert(newNickname == correctNickname)
    }

    @Test
    fun newlineNickname2Test() {
        val testNickname = "test\n\ntest"
        val correctNickname = "test test"

        val context = androidTestRule.activity
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.shared_preferences_file),
            Context.MODE_PRIVATE
        )

        androidTestRule.activity.setContent {
            SettingsScreen(navigateBack = {})
        }

        val textField = androidTestRule.onNodeWithTag("settings_nickname")
        textField.assertExists()
        textField.assertIsDisplayed()
        textField.performTextReplacement(testNickname)

        val backButton = androidTestRule.onNodeWithTag("settings_back")
        backButton.performClick()

        val newNickname = sharedPreferences.getString(
            context.getString(R.string.sharedPreferences_nickname),
            ""
        )
        assert(newNickname == correctNickname)
    }
}
