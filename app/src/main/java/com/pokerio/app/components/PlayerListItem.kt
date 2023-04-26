package com.pokerio.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 5.dp
            )
        ) {
            Text(
                text = player.nickname,
                fontSize = 20.sp
            )
            Text(
                text = "ID: ${player.playerID.substring(0..6)}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Thin
            )
        }
    }
}
