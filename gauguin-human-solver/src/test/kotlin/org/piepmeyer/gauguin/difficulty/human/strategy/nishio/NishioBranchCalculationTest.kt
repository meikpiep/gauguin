package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class NishioBranchCalculationTest :
    FunSpec({

        test("grid without possible of simple nishio is unchanged") {
            val grid =
                GridBuilder(3, 4)
                    .addCageAdd(8, GridCageType.ANGLE_RIGHT_TOP)
                    .addCageSubtract(2, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageDivide(2, GridCageType.DOUBLE_VERTICAL)
                    .addCageMultiply(12, GridCageType.L_HORIZONTAL_SHORT_LEFT_TOP)
                    .addCageSingle(4)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 2, 3, 4)
            grid.cells[1].possibles = setOf(1, 2, 3)
            grid.cells[2].possibles = setOf(1, 3, 4)
            grid.cells[3].possibles = setOf(1, 2, 3, 4)
            grid.cells[4].possibles = setOf(1, 2, 3)
            grid.cells[5].possibles = setOf(1, 2, 4)
            grid.cells[6].possibles = setOf(1, 2)
            grid.cells[7].userValue = 4
            grid.cells[8].possibles = setOf(1, 2)
            grid.cells[9].possibles = setOf(1, 3, 4)
            grid.cells[10].possibles = setOf(1, 2)
            grid.cells[11].possibles = setOf(1, 3, 4)

            println(grid)

            val solver = NishioWithPairs()

            solver.fillCellsWithNewCache(grid) shouldBe false
            println(grid)
        }

        test("nishio at top right cell with value 4 leads to contradiction and gets value 1") {
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

            println(grid)

            val solver = NishioWithPairs()

            solver.fillCellsWithNewCache(grid) shouldBe true

            grid.cells[2].userValue shouldBe 1
        }

        test("nishio") {
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

            println(grid)

            val solver = NishioWithPairs()

            solver.tryWithNishio(grid, grid.cells[2], 4) shouldBe NishioResult.CONTRADICTION
        }
    })
