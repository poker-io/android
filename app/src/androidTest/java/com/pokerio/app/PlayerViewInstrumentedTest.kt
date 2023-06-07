package com.pokerio.app

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.pokerio.app.components.PlayerView
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.Rule
import org.junit.Test

class PlayerViewInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPlayerAdminHostAdmin() {
        GameState.thisPlayer.isAdmin = true
        val nickname = "TestPlayer"
        val id = "ThisIsALongId"
        val funds = 1000
        val bet = 100
        val player = Player(nickname, id, true, folded = false, funds = 1000, bet = 100)

        composeTestRule.setContent {
            PlayerView(player = player)
        }

        val nicknameText = composeTestRule.onNodeWithTag("nickname")
        nicknameText.assertExists()
        nicknameText.assert(hasText(nickname))

        val fundsText = composeTestRule.onNodeWithTag("funds")
        fundsText.assertExists()
        fundsText.assert(hasText(funds.toString()))

        val betText = composeTestRule.onNodeWithTag("bet")
        betText.assertExists()
        betText.assert(hasText(bet.toString()))

        val playerCard = composeTestRule.onNodeWithTag("player_" + nickname + "_card")
        playerCard.assertExists()
    }
}
