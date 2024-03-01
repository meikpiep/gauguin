package org.piepmeyer.gauguin.difficulty.human

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction

class HumanSolverStrategyPossibleMustBeContainedInSingleCageInLineDeleteFromOtherCagesTest :
    FunSpec({

        test("1x6 grid and 6x1 grid with same data") {
            for ((width, height) in mapOf(1 to 6, 6 to 1)) {
                withClue("width $width, height $height") {
                    val grid =
                        GridBuilder(width, height)
                            .addSingleCage(0, 0)
                            .addSingleCage(0, 1)
                            .addSingleCage(0, 2)
                            .addCage(
                                10,
                                GridCageAction.ACTION_MULTIPLY,
                                if (width == 1) GridCageType.TRIPLE_VERTICAL else GridCageType.TRIPLE_HORIZONTAL,
                                3,
                            )
                            .createGrid()

                    grid.cells[0].possibles = setOf(2, 3, 4, 6)
                    grid.cells[1].possibles = setOf(4, 6)
                    grid.cells[2].possibles = setOf(2, 3, 4, 5, 6)
                    grid.cells[3].possibles = setOf(1, 2, 5)
                    grid.cells[4].possibles = setOf(1, 2, 5)
                    grid.cells[5].possibles = setOf(1, 2)

                    val solver = HumanSolverStrategyPossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages()

                    // solver should find two possibles and delete one of them for each run
                    solver.fillCells(grid) shouldBe true
                    solver.fillCells(grid) shouldBe true
                    solver.fillCells(grid) shouldBe true

                    println(grid)

                    grid.cells[0].possibles shouldContainExactly setOf(3, 4, 6)
                    grid.cells[2].possibles shouldContainExactly setOf(3, 4, 6)
                }
            }
        }
    })
