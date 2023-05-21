package com.pokerio.app

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.pokerio.app.components.PlayerListItemView
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import org.junit.Rule
import org.junit.Test
import java.lang.Integer.min

class PlayerListItemInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPlayerAdminHostAdmin() {
        GameState.thisPlayer.isAdmin = true
        val nickname = "TestPlayer"
        val id = "ThisIsALongId"
        val player = Player(nickname, id, true)

        composeTestRule.setContent {
            PlayerListItemView(player = player)
        }

        val nicknameText = composeTestRule.onNodeWithTag("nickname")
        nicknameText.assertExists()
        nicknameText.assert(hasText(nickname))

        val idText = composeTestRule.onNodeWithTag("player_id")
        idText.assertExists()
        idText.assert(hasText("ID: ${id.substring(0, min(id.length, 7))}"))

        val adminIcon = composeTestRule.onNodeWithTag("admin_icon")
        adminIcon.assertIsDisplayed()

        val kickButton = composeTestRule.onNodeWithTag("kick_button")
        kickButton.assertDoesNotExist()
    }

    @Test
    fun testPlayerAdminHostNotAdmin() {
        GameState.thisPlayer.isAdmin = false
        val nickname = "TestPlayer"
        val id = "ThisIsALongId"
        val player = Player(nickname, id, true)

        composeTestRule.setContent {
            PlayerListItemView(player = player)
        }

        val nicknameText = composeTestRule.onNodeWithTag("nickname")
        nicknameText.assertExists()
        nicknameText.assert(hasText(nickname))

        val idText = composeTestRule.onNodeWithTag("player_id")
        idText.assertExists()
        idText.assert(hasText("ID: ${id.substring(0, min(id.length, 7))}"))

        val adminIcon = composeTestRule.onNodeWithTag("admin_icon")
        adminIcon.assertIsDisplayed()

        val kickButton = composeTestRule.onNodeWithTag("kick_button")
        kickButton.assertDoesNotExist()
    }

    @Test
    fun testPlayerNotAdminHostAdmin() {
        GameState.thisPlayer.isAdmin = true
        val nickname = "TestPlayer"
        val id = "ThisIsALongId"
        val player = Player(nickname, id, false)

        composeTestRule.setContent {
            PlayerListItemView(player = player)
        }

        val nicknameText = composeTestRule.onNodeWithTag("nickname")
        nicknameText.assertExists()
        nicknameText.assert(hasText(nickname))

        val idText = composeTestRule.onNodeWithTag("player_id")
        idText.assertExists()
        idText.assert(hasText("ID: ${id.substring(0, min(id.length, 7))}"))

        val adminIcon = composeTestRule.onNodeWithTag("admin_icon")
        adminIcon.assertDoesNotExist()

        val kickButton = composeTestRule.onNodeWithTag("kick_button")
        kickButton.assertIsDisplayed()
    }

    @Test
    fun testPlayerNotAdminHostNotAdmin() {
        GameState.thisPlayer.isAdmin = false
        val nickname = "TestPlayer"
        val id = "ThisIsALongId"
        val player = Player(nickname, id, false)

        composeTestRule.setContent {
            PlayerListItemView(player = player)
        }

        val nicknameText = composeTestRule.onNodeWithTag("nickname")
        nicknameText.assertExists()
        nicknameText.assert(hasText(nickname))

        val idText = composeTestRule.onNodeWithTag("player_id")
        idText.assertExists()
        idText.assert(hasText("ID: ${id.substring(0, min(id.length, 7))}"))

        val adminIcon = composeTestRule.onNodeWithTag("admin_icon")
        adminIcon.assertDoesNotExist()

        val kickButton = composeTestRule.onNodeWithTag("kick_button")
        kickButton.assertDoesNotExist()
    }

    @Test
    fun shortIDTest() {
        GameState.thisPlayer.isAdmin = false
        val nickname = "TestPlayer"
        val id = "123"
        val player = Player(nickname, id, false)

        composeTestRule.setContent {
            PlayerListItemView(player = player)
        }

        val nicknameText = composeTestRule.onNodeWithTag("nickname")
        nicknameText.assertExists()
        nicknameText.assert(hasText(nickname))

        val idText = composeTestRule.onNodeWithTag("player_id")
        idText.assertExists()
        idText.assert(hasText("ID: ${id.substring(0, min(id.length, 7))}"))
    }
}
