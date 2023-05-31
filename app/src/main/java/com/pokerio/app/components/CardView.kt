package com.pokerio.app.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokerio.app.utils.GameCard

val CARD_WIDTH = 57.dp
val CARD_HEIGHT = 80.dp
val CARD_SHAPE = RoundedCornerShape(6.dp)

@Composable
@Preview
fun CardView(
    gameCard: GameCard = GameCard.none(),
    paddingValues: PaddingValues = PaddingValues(4.dp)
) {
    if (gameCard.isHidden()) {
        CardReverse(paddingValues)
    } else {
        CardObverse(gameCard, paddingValues)
    }
}

@Composable
fun CardReverse(
    paddingValues: PaddingValues
) {
    Card(
        modifier = Modifier
            .padding(paddingValues)
            .height(CARD_HEIGHT)
            .width(CARD_WIDTH)
            .border(1.dp, Color.Gray, CARD_SHAPE)
            .padding(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red
        ),
        shape = CARD_SHAPE
    ) {}
}

@Composable
fun CardObverse(
    gameCard: GameCard,
    paddingValues: PaddingValues
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .padding(paddingValues)
            .height(CARD_HEIGHT)
            .width(CARD_WIDTH)
            .border(1.dp, Color.Gray, CARD_SHAPE),
        shape = CARD_SHAPE
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = gameCard.value.toString(),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
            Icon(
                painter = painterResource(id = gameCard.suit.resId),
                contentDescription = "TODO",
                modifier = Modifier
                    .width(CARD_WIDTH * 1 / 2)
                    .height(CARD_WIDTH * 1 / 2),
                tint = Color.Unspecified
            )
            Text(
                text = gameCard.value.toString(),
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}
