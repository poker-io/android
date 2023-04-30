package com.pokerio.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pokerio.app.utils.GameState

@Preview
@Composable
fun Game() {
    Column() {
        Text(text = "Player cards:")
        Text(text = GameState.card1.toString())
        Text(text = GameState.card2.toString())
        GameState.players.forEach {
            Column() {
                Text(text = it.nickname)
                // Text(text = it.playerID)
                Text(text = it.funds.toString())
            }
        }
    }
}
