package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant

class NishioWithPairsTest :
    FunSpec({

        test("no nishio gets found") {
            val grid =
                GridBuilder(5, 5)
                    .addCageMultiply(20, GridCageType.DOUBLE_VERTICAL)
                    .addCageAdd(7, GridCageType.ANGLE_RIGHT_BOTTOM)
                    .addCageSingle(1)
                    .addCageMultiply(0, GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM)
                    .addCageSubtract(2, GridCageType.DOUBLE_VERTICAL)
                    .addCageSubtract(2, GridCageType.DOUBLE_HORIZONTAL)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 3)
            grid.cells[1].userValue = 2
            grid.cells[2].possibles = setOf(0, 3)
            grid.cells[3].possibles = setOf(0, 1)
            grid.cells[4].possibles = setOf(0, 2, 3)
            grid.cells[5].possibles = setOf(0, 3)
            grid.cells[6].userValue = 1
            grid.cells[7].possibles = setOf(2, 3)
            grid.cells[8].possibles = setOf(0, 1, 2, 3)
            grid.cells[9].possibles = setOf(0, 1, 3)
            grid.cells[10].possibles = setOf(0, 2, 3)
            grid.cells[11].possibles = setOf(0, 1, 2, 3)
            grid.cells[12].possibles = setOf(1, 2, 3)
            grid.cells[13].possibles = setOf(0, 1, 3)
            grid.cells[14].possibles = setOf(0, 2, 3)
            grid.cells[15].possibles = setOf(0, 1, 2, 3)

            println(grid)

            val solver = NishioWithPairs()

            solver.tryWithNishio(grid, grid.cells[2], 0)::class shouldBe NishioResult.Solved::class
        }

        test("nishio at top right cell with value 4 leads to contradiction and gets value 1") {
            val grid = createGridWithContradiction()

            println(grid)

            val solver = NishioWithPairs()

            val solverResult = solver.fillCellsWithNewCacheReturningDetails(grid)
            solverResult::class shouldBe HumanSolverStrategyResult.Success::class

            grid.cells[2].userValue shouldBe 1

            withClue("solver details") {
                (solverResult as HumanSolverStrategyResult.Success).changedCells shouldContainExactlyInAnyOrder
                    listOf(
                        grid.cells[1],
                        grid.cells[2],
                        grid.cells[5],
                    )
            }
        }

        test("tryWithNishio finds contradiction") {
            val grid = createGridWithContradiction()

            println(grid)

            val solver = NishioWithPairs()

            solver.tryWithNishio(grid, grid.cells[2], 4)::class shouldBe NishioResult.Contradictions::class
        }

        test("fillCells fills all cells as the grid got solved") {
            val grid = createGridToBeSolvedViaNishio()

            println(grid)

            val expectedChangedCells = grid.cells.filter { !it.isUserValueSet }

            val solver = NishioWithPairs()

            val solverResult = solver.fillCellsWithNewCacheReturningDetails(grid)
            solverResult::class shouldBe HumanSolverStrategyResult.Success::class

            grid.cells.all { it.isUserValueSet } shouldBe true

            withClue("solver details") {
                (solverResult as HumanSolverStrategyResult.Success).changedCells shouldContainExactlyInAnyOrder expectedChangedCells
            }
        }

        test("tryWithNishio results nishio solved") {
            val grid = createGridToBeSolvedViaNishio()

            println(grid)

            val solver = NishioWithPairs()

            solver.tryWithNishio(grid, grid.cells[0], 1)::class shouldBe NishioResult.Solved::class
        }

        test("nishio gets found in complicated 4x4 with zeros") {
            val grid =
                GridBuilder(4, 4, GameOptionsVariant.createClassic(DigitSetting.FIRST_DIGIT_ZERO))
                    .addCageMultiply(0, GridCageType.ANGLE_RIGHT_TOP)
                    .addCageSingle(2)
                    .addCageMultiply(0, GridCageType.ANGLE_LEFT_BOTTOM)
                    .addCageSingle(1)
                    .addCageMultiply(0, GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM)
                    .addCageSubtract(2, GridCageType.DOUBLE_VERTICAL)
                    .addCageSubtract(2, GridCageType.DOUBLE_HORIZONTAL)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 3)
            grid.cells[1].userValue = 2
            grid.cells[2].possibles = setOf(0, 3)
            grid.cells[3].possibles = setOf(0, 1)
            grid.cells[4].possibles = setOf(0, 2, 3)
            grid.cells[5].possibles = setOf(0, 3)
            grid.cells[6].userValue = 1
            grid.cells[7].possibles = setOf(2, 3)
            grid.cells[8].possibles = setOf(0, 1, 2, 3)
            grid.cells[9].possibles = setOf(0, 1, 3)
            grid.cells[10].possibles = setOf(0, 2, 3)
            grid.cells[11].possibles = setOf(0, 1, 2, 3)
            grid.cells[12].possibles = setOf(1, 2, 3)
            grid.cells[13].possibles = setOf(0, 1, 3)
            grid.cells[14].possibles = setOf(0, 2, 3)
            grid.cells[15].possibles = setOf(0, 1, 2, 3)

            println(grid)

            val solver = NishioWithPairs()

            solver.tryWithNishio(grid, grid.cells[2], 0)::class shouldBe NishioResult.Solved::class
        }
    })

private fun createGridWithContradiction(): Grid {
    val grid =
        GridBuilder(3, 4)
            .addCageSubtract(2, GridCageType.DOUBLE_HORIZONTAL)
            .addCageSubtract(3, GridCageType.DOUBLE_VERTICAL)
            .addCageMultiply(36, GridCageType.TETRIS_HORIZONTAL_LEFT_TOP)
            .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
            .addCageDivide(2, GridCageType.DOUBLE_HORIZONTAL)
            .createGrid()

    grid.cells[0].possibles = setOf(2, 3, 4)
    grid.cells[1].possibles = setOf(1, 2, 4)
    grid.cells[2].possibles = setOf(1, 4)
    grid.cells[3].possibles = setOf(1, 2, 4)
    grid.cells[4].userValue = 3
    grid.cells[5].possibles = setOf(1, 4)
    grid.cells[6].possibles = setOf(2, 4)
    grid.cells[7].possibles = setOf(1, 2, 4)
    grid.cells[8].userValue = 3
    grid.cells[9].possibles = setOf(1, 3)
    grid.cells[10].possibles = setOf(1, 4)
    grid.cells[11].userValue = 2
    return grid
}

private fun createGridToBeSolvedViaNishio(): Grid {
    val grid =
        GridBuilder(4, 3)
            .addCageSubtract(1, GridCageType.DOUBLE_HORIZONTAL)
            .addCageAdd(9, GridCageType.ANGLE_LEFT_BOTTOM)
            .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
            .addCageMultiply(8, GridCageType.L_HORIZONTAL_SHORT_LEFT_TOP)
            .addCageSingle(3)
            .createGrid()

    grid.cells[0].possibles = setOf(1, 2)
    grid.cells[1].possibles = setOf(2, 3)
    grid.cells[2].userValue = 4
    grid.cells[3].possibles = setOf(1, 3)
    grid.cells[4].possibles = setOf(2, 4)
    grid.cells[5].userValue = 1
    grid.cells[6].userValue = 3
    grid.cells[7].possibles = setOf(2, 4)
    grid.cells[8].userValue = 3
    grid.cells[9].possibles = setOf(2, 4)
    grid.cells[10].possibles = setOf(1, 2)
    grid.cells[11].possibles = setOf(1, 2, 4)
    return grid
}
