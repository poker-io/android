package com.pokerio.app.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokerio.app.utils.GameCard

val CARD_MODIFIER = Modifier
    .width(57.dp)
    .height(80.dp)
val CARD_SHAPE = RoundedCornerShape(6.dp)

@Composable
@Preview
fun CardView(
    gameCard: GameCard? = null
) {
    if (gameCard == null) {
        CardReverse()
    } else {
        CardObverse(gameCard)
    }
}

@Composable
@Preview
fun CardReverse() {
    Card(
        modifier = CARD_MODIFIER,
        colors = CardDefaults.cardColors(
            containerColor = Color.Red
        ),
        shape = CARD_SHAPE
    ) {}
}

@Composable
fun CardObverse(
    gameCard: GameCard
) {
    Card(
        modifier = CARD_MODIFIER
            .border(2.dp, Color.Black, CARD_SHAPE),
        shape = CARD_SHAPE
    ) {
        Text(text = gameCard.toString())
    }
}
