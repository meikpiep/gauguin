package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction

class SinglePossibleExhaustingTwoLinesTest :
    FunSpec({

        test("3x4 detects 3,1,3 in top right 7+ as invalid") {
            val grid =
                GridBuilder(4, 3)
                    .addCage(
                        7,
                        GridCageAction.ACTION_ADD,
                        GridCageType.ANGLE_RIGHT_BOTTOM,
                        0,
                    ).addCage(
                        7,
                        GridCageAction.ACTION_ADD,
                        GridCageType.ANGLE_LEFT_BOTTOM,
                        2,
                    ).addCage(
                        11,
                        GridCageAction.ACTION_ADD,
                        GridCageType.TETRIS_HORIZONTAL_LEFT_TOP,
                        5,
                    ).addCage(
                        4,
                        GridCageAction.ACTION_DIVIDE,
                        GridCageType.DOUBLE_HORIZONTAL,
                        8,
                    ).createGrid()

            grid.cells[0].possibles = setOf(1, 2)
            grid.cells[1].possibles = setOf(3, 4)
            grid.cells[2].possibles = setOf(2, 3, 4)
            grid.cells[3].possibles = setOf(1, 2, 4)
            grid.cells[4].possibles = setOf(1, 3)
            grid.cells[5].possibles = setOf(2, 4)
            grid.cells[6].possibles = setOf(2, 4)
            grid.cells[7].possibles = setOf(1, 3)
            grid.cells[8].userValue = 4
            grid.cells[9].userValue = 1
            grid.cells[10].possibles = setOf(2, 3)
            grid.cells[11].possibles = setOf(2, 3)

            println(grid)

            val solver = SinglePossibleExhaustingTwoLines()
            solver.fillCellsWithNewCache(grid) shouldBe true
            solver.fillCellsWithNewCache(grid) shouldBe false

            println(grid)

            assertSoftly {
                withClue("combination 3 1 3 should be deleted") {
                    grid.cells[2].possibles shouldContainExactly setOf(2, 4)
                    grid.cells[3].possibles shouldContainExactly setOf(2, 4)
                    grid.cells[7].possibles shouldContainExactly setOf(1)
                }
                withClue("cells without combination 3 1 3 should be left untouched") {
                    grid.cells[0].possibles shouldContainExactly setOf(1, 2)
                    grid.cells[1].possibles shouldContainExactly setOf(3, 4)
                    grid.cells[4].possibles shouldContainExactly setOf(1, 3)
                    grid.cells[5].possibles shouldContainExactly setOf(2, 4)
                    grid.cells[6].possibles shouldContainExactly setOf(2, 4)
                    grid.cells[10].possibles shouldContainExactly setOf(2, 3)
                    grid.cells[11].possibles shouldContainExactly setOf(2, 3)
                }
            }
        }
    })
