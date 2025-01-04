package org.piepmeyer.gauguin.creation

import io.kotest.assertions.withClue
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant.Companion.createClassic
import org.piepmeyer.gauguin.options.GameVariant
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalKotest::class)
class TestGridRandomizer :
    FunSpec({
        context("digitsFromOneOn").config(timeout = 10.seconds) {

            val parameters = mutableListOf<Pair<Int, Int>>()

            for (width in 3..11) {
                for (height in 3..11) {
                    parameters += Pair(width, height)
                }
            }

            withData(
                parameters,
            ) { (width, heigth) ->
                run {
                    val grid = Grid(GameVariant(GridSize(width, heigth), createClassic()))
                    grid.clearUserValues()
                    val randomizer = GridRandomizer(RandomPossibleDigitsShuffler(), grid)
                    randomizer.createGridValues()
                    for (cell in grid.cells) {
                        withClue("Invalid solution of $grid") {
                            cell.value shouldBeIn grid.variant.possibleDigits
                        }
                    }
                }
            }
        }

        test("deterministic random number generator leads to deterministic grids") {
            val variant = GameVariant(GridSize(9, 9), createClassic())

            val gridOne = Grid(variant)
            val gridTwo = Grid(variant)

            randomizeGrid(gridOne)
            randomizeGrid(gridTwo)

            gridOne.toString() shouldBe gridTwo.toString()
        }
    })

private fun randomizeGrid(grid: Grid) {
    val random = SeedRandomizerMock(1)
    val randomizer = GridRandomizer(RandomPossibleDigitsShuffler(random.random), grid)
    randomizer.createGridValues()
}
