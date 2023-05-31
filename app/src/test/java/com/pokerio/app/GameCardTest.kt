package com.pokerio.app

import com.pokerio.app.utils.GameCard
import org.junit.Assert.assertThrows
import org.junit.Test

class GameCardTest {

    @Test
    fun constructorTest() {
        val cardClubString = "03T"
        val cardDiamondString = "09O"
        val cardHeartString = "11K"
        val cardSpadeString = "12P"

        val cardClubs = GameCard.fromString(cardClubString)
        val cardDiamond = GameCard.fromString(cardDiamondString)
        val cardHeart = GameCard.fromString(cardHeartString)
        val cardSpade = GameCard.fromString(cardSpadeString)

        assert(cardClubs.suit == GameCard.Suit.Club)
        assert(cardDiamond.suit == GameCard.Suit.Diamond)
        assert(cardHeart.suit == GameCard.Suit.Heart)
        assert(cardSpade.suit == GameCard.Suit.Spade)

        assert(cardClubs.value == 3)
        assert(cardDiamond.value == 9)
        assert(cardHeart.value == 11)
        assert(cardSpade.value == 12)
    }

    @Test
    fun incorrectConstructorParameterTest() {
        assertThrows(IllegalArgumentException::class.java) {
            GameCard.fromString("")
        }

        assertThrows(IllegalArgumentException::class.java) {
            GameCard.fromString("09K123")
        }

        assertThrows(IllegalArgumentException::class.java) {
            GameCard.fromString("00K")
        }

        assertThrows(IllegalArgumentException::class.java) {
            GameCard.fromString("13K")
        }

        assertThrows(IllegalArgumentException::class.java) {
            GameCard.fromString("09Z")
        }
    }

    @Test
    fun emptyCardTest() {
        val emptyCard = GameCard.none()
        assert(emptyCard.suit == GameCard.Suit.None)
    }
}
