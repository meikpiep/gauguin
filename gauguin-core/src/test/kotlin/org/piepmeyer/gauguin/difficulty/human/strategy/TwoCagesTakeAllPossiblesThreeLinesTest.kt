package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCacheImpl
import org.piepmeyer.gauguin.grid.GridCageAction

class TwoCagesTakeAllPossiblesThreeLinesTest :
    FunSpec({

        test("6x6 grid") {
            val grid =
                GridBuilder(6, 6)
                    .addCage(
                        12,
                        GridCageAction.ACTION_ADD,
                        GridCageType.TRIPLE_HORIZONTAL,
                        0,
                    ).addCage(
                        3,
                        GridCageAction.ACTION_SUBTRACT,
                        GridCageType.DOUBLE_VERTICAL,
                        3,
                    ).addCage(
                        15,
                        GridCageAction.ACTION_ADD,
                        GridCageType.SQUARE,
                        4,
                    ).addCage(
                        7,
                        GridCageAction.ACTION_ADD,
                        GridCageType.DOUBLE_VERTICAL,
                        6,
                    ).addCage(
                        13,
                        GridCageAction.ACTION_ADD,
                        GridCageType.L_VERTICAL_SHORT_RIGHT_BOTTOM,
                        7,
                    ).addCage(
                        48,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.ANGLE_RIGHT_TOP,
                        8,
                    ).addCage(
                        150,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.TETRIS_HORIZONTAL_RIGHT_TOP,
                        16,
                    ).addCage(
                        7,
                        GridCageAction.ACTION_ADD,
                        GridCageType.ANGLE_RIGHT_TOP,
                        18,
                    ).addCage(
                        60,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM,
                        23,
                    ).addCage(
                        180,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.L_HORIZONTAL_SHORT_RIGHT_TOP,
                        26,
                    ).addCage(
                        24,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.ANGLE_RIGHT_BOTTOM,
                        27,
                    ).createGrid()

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

            solver.fillCells(grid, cache).first shouldBe true
            solver.fillCells(grid, cache).first shouldBe false

            println(grid)

            assertSoftly {
                withClue("5 should be deleted from other cell") {
                    grid.cells[9].possibles shouldContainExactly setOf(1, 4)
                }
            }
        }
    })
