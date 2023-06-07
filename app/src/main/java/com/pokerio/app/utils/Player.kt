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
    companion object {
        const val MAX_NICKNAME_LEN = 20

        fun validateNickname(nickname: String): Boolean {
            return nickname.isNotBlank() && nickname.length <= MAX_NICKNAME_LEN
        }

        fun fixNickname(nickname: String): String {
            // Do not allow newlines
            var returnNickname = nickname.replace('\n', ' ')
            // Do not allow trailing and leading spaces
            returnNickname = returnNickname.trimEnd(' ')
            returnNickname = returnNickname.trimStart(' ')
            // Do not allow double spaces
            returnNickname = returnNickname.replace(Regex("\\s+"), " ")

            return returnNickname
        }
    }
}
