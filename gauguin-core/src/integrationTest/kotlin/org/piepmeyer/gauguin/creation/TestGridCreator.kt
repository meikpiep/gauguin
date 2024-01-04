package org.piepmeyer.gauguin.creation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage

class TestGridCreator : FunSpec({
    test("3x3GridCreationWithoutRandomValues") {
        val creator =
            GridCreator(
                GameVariant(
                    GridSize(3, 3),
                    GameOptionsVariant(
                        true,
                        GridCageOperation.OPERATIONS_ALL,
                        DigitSetting.FIRST_DIGIT_ONE,
                        DifficultySetting.ANY,
                        SingleCageUsage.FIXED_NUMBER,
                        NumeralSystem.Decimal,
                    ),
                ),
                OnlyZeroRandomizerMock(),
                ShufflerStub(),
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

    test("deterministic random number generator leads to deterministic grids") {
        val variant =
            GameVariant(
                GridSize(10, 10),
                GameOptionsVariant.createClassic(),
            )

        val gridOne = calculateGrid(variant)
        val gridTwo = calculateGrid(variant)

        gridOne.toString() shouldBe gridTwo.toString()
    }
})

private suspend fun calculateGrid(variant: GameVariant): Grid {
    val randomizer = SeedRandomizerMock(1)

    val creator =
        GridCalculator(
            variant,
            randomizer,
            RandomPossibleDigitsShuffler(randomizer.random),
        )

    return creator.calculate()
}
