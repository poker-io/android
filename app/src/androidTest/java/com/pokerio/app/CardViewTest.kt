package com.pokerio.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.pokerio.app.components.CardView
import com.pokerio.app.utils.GameCard
import org.junit.Rule
import org.junit.Test

class CardViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyCardView() {
        composeTestRule.setContent {
            CardView()
        }

        val reverse = composeTestRule.onNodeWithTag("card_reverse")
        val obverse = composeTestRule.onNodeWithTag("card_obverse")

        reverse.assertExists()
        reverse.assertIsDisplayed()
        obverse.assertDoesNotExist()
    }

    @Test
    fun cardView() {
        composeTestRule.setContent {
            CardView(gameCard = GameCard.fromString("01T"))
        }

        val reverse = composeTestRule.onNodeWithTag("card_reverse")
        val obverse = composeTestRule.onNodeWithTag("card_obverse")

        reverse.assertDoesNotExist()
        obverse.assertExists()
        obverse.assertIsDisplayed()
    }
}
