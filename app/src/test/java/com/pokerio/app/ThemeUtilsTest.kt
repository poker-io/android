package com.pokerio.app

import androidx.compose.material3.ColorScheme
import com.pokerio.app.utils.ThemeUtils
import org.junit.Test

class ThemeUtilsTest {
    @Test
    fun lightColorThemeTest() {
        val lightColorScheme = ThemeUtils.lightColorScheme
        assert(lightColorScheme is ColorScheme)
    }
}
