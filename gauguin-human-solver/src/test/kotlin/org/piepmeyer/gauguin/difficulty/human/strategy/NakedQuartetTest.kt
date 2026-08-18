package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolver

class NakedQuartetTest :
    FunSpec({

        test("7x1 grid, two 6x cages force 1,2,3,6 to be deleted on every other cell") {
            val grid =
                GridBuilder(7, 1)
                    .addCageSingleAllPossibles(1)
                    .addCageMultiply(6, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageMultiply(6, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .createGrid()

            val solver = HumanSolver(grid, validate = false, avoidNishio = true)
            solver.prepareGrid()

            println(grid)

            solver.solveAndCalculateDifficulty(avoidReveal = true)

            println(grid)

            assertSoftly {
                withClue("cell 0 should have possibles 1,2,3,6 deleted, resulting in 4,5,7") {
                    grid.cells[0].possibles shouldBe setOf(4, 5, 7)
                }
                withClue("cell 5 should have possibles 1,2,3,6 deleted, resulting in 4,5,7") {
                    grid.cells[5].possibles shouldBe setOf(4, 5, 7)
                }
                withClue("cell 6 should have possibles 1,2,3,6 deleted, resulting in 4,5,7") {
                    grid.cells[6].possibles shouldBe setOf(4, 5, 7)
                }
            }
        }
    })
