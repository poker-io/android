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

    fun isHidden(): Boolean {
        return suit == Suit.None
    }

    override fun toString(): String = suit.toString() + value

    companion object {
        fun fromString(string: String): GameCard {
            val valueString = string.substring(0..1)

            val suit = when (val suitString = string.substring(2..2)) {
                "K" -> Suit.Heart
                "O" -> Suit.Diamond
                "T" -> Suit.Club
                "P" -> Suit.Spade
                else -> throw IllegalArgumentException("Unknown card suite '$suitString'")
            }

            return GameCard(suit, valueString.toInt())
        }

        fun none(): GameCard {
            return GameCard(Suit.None, 0)
        }
    }
}
