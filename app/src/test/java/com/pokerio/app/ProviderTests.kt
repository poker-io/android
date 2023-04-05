package com.pokerio.app

import com.pokerio.app.utils.IntUnitProvider
import com.pokerio.app.utils.UnitUnitProvider
import org.junit.Test

class ProviderTests {

    @Test
    fun intUnitProviderTest() {
        val provider = IntUnitProvider()
        val iterator = provider.values.iterator()

        assert(iterator.hasNext())
    }

    @Test
    fun unitUnitProviderTest() {
        val provider = UnitUnitProvider()
        val iterator = provider.values.iterator()

        assert(iterator.hasNext())
    }
}
