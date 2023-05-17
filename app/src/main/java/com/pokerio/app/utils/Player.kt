package com.pokerio.app.utils

class Player(
    val nickname: String,
    val playerID: String,
    var isAdmin: Boolean = false,
    var funds: Int = 0
) {
    companion object {
        const val MAX_NICKNAME_LEN = 20

        fun validateNickname(nickname: String): Boolean {
            return nickname.isNotBlank() && nickname.length <= MAX_NICKNAME_LEN
        }
    }
}
