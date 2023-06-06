package com.pokerio.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokerio.app.R
import com.pokerio.app.utils.Player
import com.pokerio.app.utils.PlayerProvider

val TEXT_MODIFIER = Modifier.padding(2.dp)

// val NICK_BACKGROUND_COLOR = Color.
val PLAYER_VIEW_CARD_SHAPE =
    RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = 8.dp,
        bottomEnd = 8.dp
    )

@Composable
@Preview
fun PlayerView(
    @PreviewParameter(PlayerProvider::class) player: Player
) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = PLAYER_VIEW_CARD_SHAPE
        ) {
            Column(
                modifier = Modifier.padding(1.dp)
            ) {
                Card(
                    modifier = Modifier.width(IntrinsicSize.Max).align(CenterHorizontally)
                ) {
                    Text(
                        text = player.nickname,
                        modifier = TEXT_MODIFIER,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black
                    )
                }
                Card(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    shape = RectangleShape
                ) {
                    Text(
                        text = "Funds",
                        fontSize = 10.sp
                    )
                }
                OutlinedCard(
                    shape = PLAYER_VIEW_CARD_SHAPE,
                    modifier = Modifier.align(CenterHorizontally).fillMaxWidth()
                ) {
                    Text(
                        text = "${player.funds}",
                        modifier = Modifier.align(CenterHorizontally),
                        fontSize = 13.sp
                    )
                }
                Card(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    shape = RectangleShape
                ) {
                    Text(
                        text = "Bet",
                        fontSize = 10.sp
                    )
                }
                OutlinedCard(
                    shape = PLAYER_VIEW_CARD_SHAPE,
                    modifier = Modifier.align(CenterHorizontally).fillMaxWidth()
                ) {
                    Text(
                        text = "${player.bet}",
                        modifier = Modifier.align(CenterHorizontally),
                        fontSize = 11.sp
                    )
                }
                if (player.folded) {
                    Text(
                        stringResource(R.string.fold),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
