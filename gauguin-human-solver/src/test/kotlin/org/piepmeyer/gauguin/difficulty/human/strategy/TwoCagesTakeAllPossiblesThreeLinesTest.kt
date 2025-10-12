package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCacheImpl

class TwoCagesTakeAllPossiblesThreeLinesTest :
    FunSpec({

        test("6x6 grid") {
            val grid =
                GridBuilder(6, 6)
                    .addCageAdd(12, GridCageType.TRIPLE_HORIZONTAL)
                    .addCageSubtract(3, GridCageType.DOUBLE_VERTICAL)
                    .addCageAdd(15, GridCageType.SQUARE)
                    .addCageAdd(7, GridCageType.DOUBLE_VERTICAL)
                    .addCageAdd(13, GridCageType.L_VERTICAL_SHORT_RIGHT_BOTTOM)
                    .addCageMultiply(48, GridCageType.ANGLE_RIGHT_TOP)
                    .addCageMultiply(150, GridCageType.TETRIS_HORIZONTAL_RIGHT_TOP)
                    .addCageAdd(7, GridCageType.ANGLE_RIGHT_TOP)
                    .addCageMultiply(60, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
                    .addCageMultiply(180, GridCageType.L_HORIZONTAL_SHORT_RIGHT_TOP)
                    .addCageMultiply(24, GridCageType.ANGLE_RIGHT_BOTTOM)
                    .createGrid()

            grid.cells[0].possibles = setOf(3, 4, 5, 6)
            grid.cells[1].possibles = setOf(1, 3, 4, 5, 6)
            grid.cells[2].possibles = setOf(1, 3, 4, 5, 6)
            grid.cells[3].possibles = setOf(1, 2, 4)
            grid.cells[4].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[5].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[6].possibles = setOf(1, 3, 4, 5, 6)
            grid.cells[7].possibles = setOf(1, 2, 3, 4, 5)
            grid.cells[8].possibles = setOf(2, 4, 6)
            grid.cells[9].possibles = setOf(1, 4, 5)
            grid.cells[10].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[11].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[12].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[13].possibles = setOf(1, 2, 3, 4)
            grid.cells[14].possibles = setOf(2, 4, 6)
            grid.cells[15].possibles = setOf(4, 6)
            grid.cells[16].possibles = setOf(1, 2, 3, 5)
            grid.cells[17].possibles = setOf(1, 2, 3, 5)
            grid.cells[18].possibles = setOf(1, 2, 3, 4)
            grid.cells[19].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[20].possibles = setOf(1, 3, 4, 6)
            grid.cells[21].possibles = setOf(2, 3, 5, 6)
            grid.cells[22].possibles = setOf(2, 3, 5, 6)
            grid.cells[23].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[24].possibles = setOf(1, 2, 3, 4)
            grid.cells[25].possibles = setOf(1, 2, 3, 4)
            grid.cells[26].possibles = setOf(3, 5, 6)
            grid.cells[27].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[28].possibles = setOf(1, 2, 3, 4)
            grid.cells[29].possibles = setOf(1, 2, 3, 4, 5, 6)
            grid.cells[30].possibles = setOf(2, 3, 5, 6)
            grid.cells[31].possibles = setOf(1, 2, 3, 5, 6)
            grid.cells[32].possibles = setOf(1, 3, 5, 6)
            grid.cells[33].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[34].possibles = setOf(1, 2, 3, 4, 5, 6)
            grid.cells[35].possibles = setOf(1, 2, 3, 4, 5, 6)

            println(grid)

            val cache = HumanSolverCacheImpl(grid)
            cache.initialize()

            val solver = TwoCagesTakeAllPossiblesThreeLines()

            solver.fillCells(grid, cache).madeChanges() shouldBe true
            solver.fillCells(grid, cache).madeChanges() shouldBe false

            println(grid)

            assertSoftly {
                withClue("5 should be deleted from other cell") {
                    grid.cells[9].possibles shouldContainExactly setOf(1, 4)
                }
            }
        }
    })
