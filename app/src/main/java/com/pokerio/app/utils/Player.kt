package com.pokerio.app.utils

class Player(
    val nickname: String,
    val playerID: String,
    var isAdmin: Boolean = false,
    var funds: Int = 0
) {
    companion object {
        fun validateNickname(nickname: String): Boolean {
            return nickname.isNotBlank() && nickname.length <= GameState.MAX_NICKNAME_LEN
        }
    }
}
