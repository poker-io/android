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

    @Test
    fun fixIncorrectNickname() {
        val incorrect1 = "test\n"
        val correct1 = "test"
        val incorrect2 = "test   "
        val correct2 = "test"
        val incorrect3 = "     test"
        val correct3 = "test"
        val incorrect4 = "test\n\n"
        val correct4 = "test"
        val incorrect5 = "\n\ntest"
        val correct5 = "test"
        val incorrect6 = "te\n\nst"
        val correct6 = "te st"
        val incorrect7 = "te    st"
        val correct7 = "te st"

        assert(Player.fixNickname(incorrect1) == correct1)
        assert(Player.fixNickname(incorrect2) == correct2)
        assert(Player.fixNickname(incorrect3) == correct3)
        assert(Player.fixNickname(incorrect4) == correct4)
        assert(Player.fixNickname(incorrect5) == correct5)
        assert(Player.fixNickname(incorrect6) == correct6)
        assert(Player.fixNickname(incorrect7) == correct7)
    }
}
