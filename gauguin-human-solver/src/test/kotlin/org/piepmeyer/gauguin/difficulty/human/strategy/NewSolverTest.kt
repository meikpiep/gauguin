package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolver

class NewSolverTest :
    FunSpec({

        test("3x4 grid") {
            val grid =
                GridBuilder(3, 4)
                    .addCageSubtract(
                        1,
                        GridCageType.DOUBLE_VERTICAL,
                    ).addCageAdd(
                        7,
                        GridCageType.DOUBLE_HORIZONTAL,
                    ).addCageSingle(1)
                    .addCageSubtract(
                        1,
                        GridCageType.DOUBLE_VERTICAL,
                    ).addCageMultiply(
                        24,
                        GridCageType.L_HORIZONTAL_SHORT_LEFT_TOP,
                    ).addCageSingle(4)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 2)
            grid.cells[1].userValue = 3
            grid.cells[2].userValue = 4
            grid.cells[3].possibles = setOf(2, 3)
            grid.cells[4].userValue = 1
            grid.cells[5].possibles = setOf(2, 3)
            grid.cells[6].possibles = setOf(1, 3)
            grid.cells[7].userValue = 4
            grid.cells[8].possibles = setOf(1, 2, 3)
            grid.cells[9].userValue = 4
            grid.cells[10].userValue = 2
            grid.cells[11].possibles = setOf(1, 3)

            println(grid)

            val solver = HumanSolver(grid, avoidNishio = true)
            solver.prepareGrid()
            solver.solveAndCalculateDifficulty(true)

            // solver.fillCellsWithNewCache(grid) shouldBe true

            println(grid)

            assertSoftly {
                withClue("cell 6, possible 1 should be removed") {
                    grid.cells[6].possibles shouldContainExactly setOf(3)
                }
                withClue("cell 11, possible 3 should be removed") {
                    grid.cells[11].possibles shouldContainExactly setOf(1)
                }
            }
        }
    })
