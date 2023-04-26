package com.pokerio.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokerio.app.utils.Player
import com.pokerio.app.utils.PlayerProvider

@Preview
@Composable
fun PlayerListItem(
    @PreviewParameter(PlayerProvider::class) player: Player
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 6.dp
            )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
        ) {
            Text(
                text = player.nickname,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ID: ${player.playerID.substring(0..6)}",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}
