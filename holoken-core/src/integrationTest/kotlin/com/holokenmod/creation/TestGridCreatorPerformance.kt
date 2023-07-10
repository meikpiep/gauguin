package com.holokenmod.creation

import com.holokenmod.grid.GridSize
import com.holokenmod.options.DifficultySetting
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import io.kotest.core.spec.style.FunSpec

class TestGridCreatorPerformance : FunSpec({
    xtest("9 x 9 Extreme GridCreator").config(invocations = 100) {
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
})
