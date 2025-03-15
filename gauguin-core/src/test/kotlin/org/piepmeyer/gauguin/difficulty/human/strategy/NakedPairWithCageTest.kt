package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.grid.GridCageAction

class NakedPairWithCageTest :
    FunSpec({

        test("4x4 grid") {
            val grid =
                GridBuilder(4, 4)
                    .addCage(
                        9,
                        GridCageAction.ACTION_ADD,
                        GridCageType.ANGLE_RIGHT_BOTTOM,
                        0,
                    ).addCage(
                        4,
                        GridCageAction.ACTION_DIVIDE,
                        GridCageType.DOUBLE_HORIZONTAL,
                        2,
                    ).addCage(
                        6,
                        GridCageAction.ACTION_ADD,
                        GridCageType.TRIPLE_HORIZONTAL,
                        5,
                    ).addCage(
                        24,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM,
                        8,
                    ).addCage(
                        2,
                        GridCageAction.ACTION_SUBTRACT,
                        GridCageType.DOUBLE_VERTICAL,
                        11,
                    ).addCage(
                        2,
                        GridCageAction.ACTION_SUBTRACT,
                        GridCageType.DOUBLE_HORIZONTAL,
                        12,
                    ).createGrid()

            grid.cells[0].possibles = setOf(2, 3)
            grid.cells[1].possibles = setOf(2, 3)
            grid.cells[2].possibles = setOf(1, 4)
            grid.cells[3].possibles = setOf(1, 4)
            grid.cells[4].userValue = 4
            grid.cells[5].possibles = setOf(1, 2, 3)
            grid.cells[6].possibles = setOf(1, 2, 3)
            grid.cells[7].possibles = setOf(1, 2, 3)
            grid.cells[8].possibles = setOf(1, 2, 3)
            grid.cells[9].possibles = setOf(1, 2, 3, 4)
            grid.cells[10].possibles = setOf(1, 2, 3, 4)
            grid.cells[11].possibles = setOf(1, 2, 3, 4)
            grid.cells[12].possibles = setOf(1, 2, 3)
            grid.cells[13].possibles = setOf(1, 3, 4)
            grid.cells[14].possibles = setOf(1, 2, 3, 4)
            grid.cells[15].possibles = setOf(1, 2, 3, 4)

            val solver = NakedPairWithCage()

            println(grid)

            // solver should find two possibles and delete one of them for each run
            solver.fillCells(grid, HumanSolverCache(grid)) shouldBe true

            println(grid)

            assertSoftly {
                withClue("possibles should be deleted from other cells") {
                    grid.cells[7].possibles shouldContainExactly setOf(2, 3)
                }
            }
        }
    })
