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
        val cardSpadeString = "13P"

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
        assert(cardSpade.value == 13)
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
            GameCard.fromString("14K")
        }

        assertThrows(IllegalArgumentException::class.java) {
            GameCard.fromString("09Z")
        }
    }

    @Test
    fun valueStringTest() {
        val ace = GameCard.fromString("01T")
        val card2 = GameCard.fromString("02T")
        val card3 = GameCard.fromString("03T")
        val card4 = GameCard.fromString("04T")
        val card5 = GameCard.fromString("05T")
        val card6 = GameCard.fromString("06T")
        val card7 = GameCard.fromString("07T")
        val card8 = GameCard.fromString("08T")
        val card9 = GameCard.fromString("09T")
        val card10 = GameCard.fromString("10T")
        val jack = GameCard.fromString("11T")
        val queen = GameCard.fromString("12T")
        val king = GameCard.fromString("13T")

        assert(ace.valueString() == "A")
        assert(card2.valueString() == "2")
        assert(card3.valueString() == "3")
        assert(card4.valueString() == "4")
        assert(card5.valueString() == "5")
        assert(card6.valueString() == "6")
        assert(card7.valueString() == "7")
        assert(card8.valueString() == "8")
        assert(card9.valueString() == "9")
        assert(card10.valueString() == "10")
        assert(jack.valueString() == "J")
        assert(queen.valueString() == "Q")
        assert(king.valueString() == "K")
    }

    @Test
    fun emptyCardTest() {
        val emptyCard = GameCard.none()
        assert(emptyCard.suit == GameCard.Suit.None)
    }
}
