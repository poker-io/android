package com.pokerio.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.pokerio.app.R
import com.pokerio.app.utils.Player
import com.pokerio.app.utils.PlayerProvider

@Composable
@Preview
fun PlayerView(
    @PreviewParameter(PlayerProvider::class) player: Player
) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .width(IntrinsicSize.Max),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 8.dp,
                bottomEnd = 8.dp
            )
        ) {
            Text(player.nickname)
            Text("Funds: ${player.funds}")
        }
        OutlinedCard(
            modifier = Modifier
                .padding(4.dp)
                .width(IntrinsicSize.Max)
        ) {
            Text("Bet: ${player.bet}")
        }
        if (player.folded) {
            Text(
                stringResource(R.string.fold),
                color = Color.Gray
            )
        }
    }
}
