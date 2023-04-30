package com.pokerio.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokerio.app.utils.GameState

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Game() {
    val sectionTitleFontSize = 24.sp
    val sectionTitleFontWeight = FontWeight.Bold
    val sectionTitleModifier = Modifier.padding(10.dp)
    val spacerModifier = Modifier.padding(10.dp)

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
