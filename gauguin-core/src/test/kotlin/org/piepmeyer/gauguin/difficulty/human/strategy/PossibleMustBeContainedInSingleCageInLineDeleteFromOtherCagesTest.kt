package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction

class PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCagesTest :
    FunSpec({

        test("2x6 grid") {
            val grid =
                GridBuilder(2, 6)
                    .addCage(
                        72,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.ANGLE_RIGHT_BOTTOM,
                        0,
                    ).addCage(
                        9,
                        GridCageAction.ACTION_ADD,
                        GridCageType.ANGLE_LEFT_TOP,
                        3,
                    ).addCage(
                        10,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.TRIPLE_VERTICAL,
                        6,
                    ).addCage(
                        12,
                        GridCageAction.ACTION_ADD,
                        GridCageType.TRIPLE_VERTICAL,
                        7,
                    ).createGrid()

            // first column
            grid.cells[0].possibles = setOf(2, 3, 4, 6)
            grid.cells[2].possibles = setOf(4, 6)
            grid.cells[4].possibles = setOf(2, 3, 4, 5, 6)
            grid.cells[6].possibles = setOf(1, 2, 5)
            grid.cells[8].possibles = setOf(1, 2, 5)
            grid.cells[10].possibles = setOf(1, 2)
            // second column
            grid.cells[1].possibles = setOf(3, 4, 6)
            grid.cells[3].possibles = setOf(1, 2, 4)
            grid.cells[5].possibles = setOf(2, 3, 4, 5, 6)
            grid.cells[7].possibles = setOf(1, 2, 3, 4, 5, 6)
            grid.cells[9].possibles = setOf(1, 2, 3, 4, 5, 6)
            grid.cells[11].possibles = setOf(1, 2, 3, 4, 6)

            val solver = PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages()

            println(grid)

            // solver should find two possibles and delete one of them for each run
            solver.fillCellsWithNewCache(grid) shouldBe true
            solver.fillCellsWithNewCache(grid) shouldBe true
            solver.fillCellsWithNewCache(grid) shouldBe true

            println(grid)

            assertSoftly {
                withClue("cell 0") {
                    grid.cells[0].possibles shouldContainExactly setOf(3, 4, 6)
                }
                withClue("cell 2") {
                    grid.cells[2].possibles shouldContainExactly setOf(4, 6)
                }
                withClue("cell 4") {
                    grid.cells[4].possibles shouldContainExactly setOf(3, 4, 6)
                }
            }
        }
    })
