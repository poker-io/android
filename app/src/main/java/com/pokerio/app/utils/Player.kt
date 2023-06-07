package com.pokerio.app.utils

class Player(
    val nickname: String,
    val playerID: String,
    var isAdmin: Boolean = false,
    var funds: Int = 0,
    var bet: Int = 0,
    var folded: Boolean = false,
    var turn: Int = 0
) {
    fun isSmallBlind(): Boolean {
//        return true
        return playerID == GameState.getSmallBlindPlayer()?.playerID
    }

    fun isBigBlind(): Boolean {
        return playerID == GameState.getBigBlindPlayer()?.playerID
    }

    companion object {
        const val MAX_NICKNAME_LEN = 20

        fun validateNickname(nickname: String): Boolean {
            return nickname.isNotBlank() && nickname.length <= MAX_NICKNAME_LEN
        }
    }
}
