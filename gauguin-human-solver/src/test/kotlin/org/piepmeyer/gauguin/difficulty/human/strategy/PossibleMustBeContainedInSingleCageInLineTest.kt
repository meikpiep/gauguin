package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class PossibleMustBeContainedInSingleCageInLineTest :
    FunSpec({

        test("solver matches 1x5 grid and 5x1 grid with same data") {
            for ((width, height) in mapOf(1 to 5, 5 to 1)) {
                withClue("width $width, height $height") {
                    val grid =
                        GridBuilder(width, height)
                            .addCageSingle(2)
                            .addCageAdd(
                                10,
                                if (width == 1) {
                                    GridCageType.TRIPLE_VERTICAL
                                } else {
                                    GridCageType.TRIPLE_HORIZONTAL
                                },
                            ).addCageSingle(2)
                            .createGrid()

                    grid.cells[0].possibles = setOf(3, 4)
                    grid.cells[1].possibles = setOf(2, 3, 4)
                    grid.cells[2].userValue = 5
                    grid.cells[3].possibles = setOf(1, 2, 3)
                    grid.cells[4].possibles = setOf(1, 4)

                    println(grid)

                    val solver = PossibleMustBeContainedInSingleCageInLine()

                    solver.fillCellsWithNewCache(grid) shouldBe true
                    solver.fillCellsWithNewCache(grid) shouldBe true

                    grid.cells[1].possibles shouldContainExactly setOf(2, 3)
                    grid.cells[3].possibles shouldContainExactly setOf(2, 3)
                }
            }
        }

        test("solver does nothing if there are no possibles to remove") {
            val grid =
                GridBuilder(1, 5)
                    .addCageSingle(2)
                    .addCageAdd(10, GridCageType.TRIPLE_VERTICAL)
                    .addCageSingle(2)
                    .createGrid()

            grid.cells[0].possibles = setOf(3, 4)
            grid.cells[1].possibles = setOf(2, 3)
            grid.cells[2].userValue = 5
            grid.cells[3].possibles = setOf(2, 3)
            grid.cells[4].possibles = setOf(1, 4)

            println(grid)

            val solver = PossibleMustBeContainedInSingleCageInLine()

            solver.fillCellsWithNewCache(grid) shouldBe false

            grid.cells[1].possibles shouldContainExactly setOf(2, 3)
            grid.cells[3].possibles shouldContainExactly setOf(2, 3)
        }
    })
