package com.holokenmod.creation

import com.holokenmod.grid.GridSize
import com.holokenmod.options.DifficultySetting
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameOptionsVariant
import com.holokenmod.options.GameVariant
import com.holokenmod.options.GridCageOperation
import com.holokenmod.options.SingleCageUsage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestGridCreator : FunSpec({
    test("3x3GridCreationWithoutRandomValues") {
        val creator = GridCreator(
            RandomizerMock(),
            ShufflerStub(),
            GameVariant(
                GridSize(3, 3),
                GameOptionsVariant(
                    true,
                    GridCageOperation.OPERATIONS_ALL,
                    DigitSetting.FIRST_DIGIT_ONE,
                    DifficultySetting.ANY,
                    SingleCageUsage.FIXED_NUMBER,
                    false
                )
            )
        )

        val grid = creator.createRandomizedGridWithCages()

        grid.getValidCellAt(0, 0).value shouldBe 1
        grid.getValidCellAt(0, 1).value shouldBe 2
        grid.getValidCellAt(0, 2).value shouldBe 3
        grid.getValidCellAt(1, 0).value shouldBe 2
        grid.getValidCellAt(1, 1).value shouldBe 3
        grid.getValidCellAt(1, 2).value shouldBe 1
        grid.getValidCellAt(2, 0).value shouldBe 3
        grid.getValidCellAt(2, 1).value shouldBe 1
        grid.getValidCellAt(2, 2).value shouldBe 2
    }
})
