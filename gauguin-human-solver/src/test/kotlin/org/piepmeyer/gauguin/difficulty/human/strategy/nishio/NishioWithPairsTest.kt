package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid

class NishioWithPairsTest :
    FunSpec({

        test("tryWithNishio finds contradiction") {
            val grid = createGridWithContradiction()

            println(grid)

            val solver = NishioCore(grid, PossiblesCacheByCageNumber(grid), grid.cells[2], 4)

            solver.tryWithNishio()::class shouldBe NishioResult.Contradictions::class
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

            val solver = NishioCore(grid, PossiblesCacheByCageNumber(grid), grid.cells[0], 1)

            solver.tryWithNishio()::class shouldBe NishioResult.Solved::class
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
