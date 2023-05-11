package com.pokerio.app

import com.pokerio.app.utils.Player
import org.junit.Test

class PlayerTests {

    @Test
    fun validateCorrectNickname() {
        assert(Player.validateNickname("thisIsGood"))
        assert(Player.validateNickname("alsoGood"))
        assert(Player.validateNickname("1"))
        assert(Player.validateNickname("12345678901234567890"))
        assert(Player.validateNickname("    98"))
    }

    @Test
    fun validateIncorrectNickname() {
        assert(!Player.validateNickname("thisIsAVeryBadNicknameIThinkTOOLong"))
        assert(!Player.validateNickname(""))
        assert(!Player.validateNickname("     "))
    }

}