package com.holokenmod.creation

import com.holokenmod.grid.GridSize
import com.holokenmod.options.DifficultySetting
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TestGridCreatorPerformance {
    @Disabled
    @Test
    fun test() {
        val variant = GameVariant(
            GridSize(9, 9),
            createClassic()
        )
        variant.options.difficultySetting = DifficultySetting.EXTREME
        val creator = GridCreator(
            variant
        )
        creator.createRandomizedGridWithCages()
    }
}