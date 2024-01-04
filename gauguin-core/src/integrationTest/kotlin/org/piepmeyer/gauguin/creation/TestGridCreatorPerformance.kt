package org.piepmeyer.gauguin.creation

import io.kotest.core.spec.style.FunSpec
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameOptionsVariant.Companion.createClassic
import org.piepmeyer.gauguin.options.GameVariant

class TestGridCreatorPerformance : FunSpec({
    xtest("9 x 9 Extreme GridCreator").config(invocations = 100) {
        val variant =
            GameVariant(
                GridSize(9, 9),
                createClassic(),
            )
        variant.options.difficultySetting = DifficultySetting.EXTREME

        GridCreator(variant).createRandomizedGridWithCages()
    }
})
