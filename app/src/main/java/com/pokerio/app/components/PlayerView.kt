package com.pokerio.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import com.pokerio.app.utils.PlayerProvider

val TEXT_MODIFIER = Modifier.padding(2.dp)
val AMOUNT_SIZE = 11.sp
val LABEL_SIZE = 10.sp

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
    val smallBlindColor = MaterialTheme.colorScheme.tertiary
    val bigBlindColor = MaterialTheme.colorScheme.onTertiaryContainer
    val cardColor =
        if (player == GameState.currentPlayer) {
            MaterialTheme.colorScheme.primary
        } else if (player.folded) {
            Color.LightGray
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }

    Column(
        modifier = Modifier
            .width(100.dp)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = PLAYER_VIEW_CARD_SHAPE,
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {
            Column(
                modifier = Modifier.padding(1.dp)
            ) {
                Card(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .align(CenterHorizontally),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    )
                ) {
                    Text(
                        text = player.nickname,
                        modifier = TEXT_MODIFIER,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Card(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    )
                ) {
                    Text(
                        text = "Funds",
                        fontSize = LABEL_SIZE
                    )
                }
                OutlinedCard(
                    shape = PLAYER_VIEW_CARD_SHAPE,
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "${player.funds}",
                        modifier = Modifier.align(CenterHorizontally),
                        fontSize = AMOUNT_SIZE
                    )
                }
                Card(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    )
                ) {
                    Text(
                        text = "Bet",
                        fontSize = LABEL_SIZE
                    )
                }
                OutlinedCard(
                    shape = PLAYER_VIEW_CARD_SHAPE,
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "${player.bet}",
                        modifier = Modifier.align(CenterHorizontally),
                        fontSize = AMOUNT_SIZE
                    )
                }
            }
        }
        if (player.isSmallBlind()) {
            Text(
                modifier = Modifier.align(CenterHorizontally),
                text = stringResource(R.string.small_blind),
                color = smallBlindColor
            )
        } else if (player.isBigBlind()) {
            Text(
                modifier = Modifier.align(CenterHorizontally),
                text = stringResource(R.string.big_blind),
                color = bigBlindColor
            )
        }
    }
}
