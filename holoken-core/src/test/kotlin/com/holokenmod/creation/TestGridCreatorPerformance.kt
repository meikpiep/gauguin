package com.holokenmod.creation

import com.holokenmod.grid.GridSize
import com.holokenmod.options.DifficultySetting
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec

@Ignored
class TestGridCreatorPerformance : FunSpec({
    test("3x3GridCreationWithoutRandomValues") {
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