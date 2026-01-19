package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid

class AdvancedNishioWithPairsTest :
    FunSpec({

        test("NishioCore finds contradiction") {
            val grid = createGridToBeSolvedViaNishio()

            println(grid)

            val solver = NishioCore(grid, PossiblesCacheByCageNumber(grid), grid.cells[11], 6)

            solver.tryWithNishio()::class shouldBe NishioResult.Contradictions::class
        }

        test("nishio gets found") {
            val grid = createGridToBeSolvedViaNishio()

            println(grid)

            val solver = AdvancedNishioWithPairs()

            solver.fillCellsWithNewCache(grid) shouldBe true

            println(grid)
        }
    })

private fun createGridToBeSolvedViaNishio(): Grid {
    val grid =
        GridBuilder(3, 6)
            .addCageMultiply(60, GridCageType.ANGLE_RIGHT_BOTTOM)
            .addCageDivide(2, GridCageType.DOUBLE_VERTICAL)
            .addCageAdd(9, GridCageType.ANGLE_RIGHT_TOP)
            .addCageSubtract(2, GridCageType.DOUBLE_VERTICAL)
            .addCageSingle(5)
            .addCageMultiply(240, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
            .addCageAdd(7, GridCageType.ANGLE_RIGHT_BOTTOM)
            .createGrid()

    grid.cells[0].possibles = setOf(2, 3, 4, 5, 6)
    grid.cells[1].possibles = setOf(2, 3, 4, 6)
    grid.cells[2].possibles = setOf(1, 2, 3, 6)
    grid.cells[3].possibles = setOf(2, 3, 4, 5, 6)
    grid.cells[4].possibles = setOf(1, 2, 3, 4, 6)
    grid.cells[5].possibles = setOf(1, 2, 3, 6)
    grid.cells[6].possibles = setOf(1, 2, 3, 4, 6)
    grid.cells[7].possibles = setOf(1, 2, 3, 4, 6)
    grid.cells[8].possibles = setOf(1, 2, 3, 4, 6)
    grid.cells[9].possibles = setOf(1, 2, 3, 4, 6)
    grid.cells[10].userValue = 5
    grid.cells[11].possibles = setOf(2, 3, 4, 6)
    grid.cells[12].possibles = setOf(1, 2, 3, 4)
    grid.cells[13].possibles = setOf(1, 2, 3, 4)
    grid.cells[14].possibles = setOf(2, 3, 4, 5, 6)
    grid.cells[15].possibles = setOf(1, 2, 3, 4)
    grid.cells[16].possibles = setOf(2, 4, 6)
    grid.cells[17].possibles = setOf(2, 3, 4, 5, 6)

    return grid
}
