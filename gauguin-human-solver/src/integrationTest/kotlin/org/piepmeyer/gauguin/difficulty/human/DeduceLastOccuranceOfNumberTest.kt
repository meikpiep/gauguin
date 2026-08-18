package org.piepmeyer.gauguin.difficulty.human

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class DeduceLastOccuranceOfNumberTest :
    FunSpec({

        test("7x7 grid, last 7 can be set in cell on right column") {
            val grid =
                GridBuilder(7, 7)
                    .addCageMultiply(
                        84,
                        GridCageType.TRIPLE_HORIZONTAL,
                    ).addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageMultiply(28, GridCageType.DOUBLE_VERTICAL)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingle(7)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageMultiply(
                        392,
                        GridCageType.SQUARE,
                    ).addCageSingleAllPossibles(1)
                    .addCageMultiply(
                        84,
                        GridCageType.L_HORIZONTAL_SHORT_LEFT_TOP,
                    ).addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .addCageSingleAllPossibles(1)
                    .createGrid()

            val solver = HumanSolver(grid, validate = false, avoidNishio = true)
            solver.prepareGrid()

            println(grid)

            solver.solveAndCalculateDifficulty(avoidReveal = true)

            println(grid)

            withClue("cell 27 should got the use value 7 as this is the only cell left to hold a seven regarindg the last column") {
                grid.cells[27].userValue shouldBe 7
            }
        }
    })
