package com.pokerio.app.utils

import com.pokerio.app.R

class GameCard private constructor(val suit: Suit, val value: Int) {
    enum class Suit(val resId: Int) {
        Club(R.drawable.club),
        Diamond(R.drawable.diamond),
        Heart(R.drawable.heart),
        Spade(R.drawable.spade),
        None(0)
    }

    fun isNone(): Boolean {
        return suit == Suit.None
    }

    fun valueString(): String {
        return when (value) {
            ACE -> "A"
            JACK -> "J"
            QUEEN -> "Q"
            KING -> "K"
            else -> value.toString()
        }
    }

    fun shouldUnderline(): Boolean {
        return value == UNDERLINE_6 || value == UNDERLINE_9
    }

    override fun toString(): String = suit.toString() + value

    companion object {
        private const val STRING_LENGTH = 3
        private const val NUMBER_START = 0
        private const val NUMBER_END = 1
        private const val SUIT_START = 2
        private const val SUIT_END = 2
        private const val MIN_VALUE = 1
        private const val MAX_VALUE = 13

        private const val ACE = 1
        private const val JACK = 11
        private const val QUEEN = 12
        private const val KING = 13

        private const val HEART = "K"
        private const val DIAMOND = "O"
        private const val CLUB = "T"
        private const val SPADE = "P"

        private const val UNDERLINE_6 = 6
        private const val UNDERLINE_9 = 9

        fun fromString(string: String): GameCard {
            require(string.length == STRING_LENGTH)

            val valueString = string.substring(NUMBER_START..NUMBER_END)
            val value = valueString.toInt()

            require(value in MIN_VALUE..MAX_VALUE)

            val suit = when (val suitString = string.substring(SUIT_START..SUIT_END)) {
                HEART -> Suit.Heart
                DIAMOND -> Suit.Diamond
                CLUB -> Suit.Club
                SPADE -> Suit.Spade
                else -> throw IllegalArgumentException("Unknown card suite '$suitString'")
            }

            return GameCard(suit, value)
        }

        fun none(): GameCard {
            return GameCard(Suit.None, 0)
        }
    }
}
