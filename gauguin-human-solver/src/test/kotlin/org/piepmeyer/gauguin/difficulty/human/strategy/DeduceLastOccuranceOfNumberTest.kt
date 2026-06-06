package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolver

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

            val solver = HumanSolver(grid, false, true)
            solver.prepareGrid()

            grid.setUserValueAndRemovePossibles(grid.getValidCellAt(1, 0), 7)
            grid.setUserValueAndRemovePossibles(grid.getValidCellAt(2, 0), 4)
            grid.setUserValueAndRemovePossibles(grid.getValidCellAt(2, 3), 7)

            println(grid)

            solver.solveAndCalculateDifficulty(avoidReveal = true)

            // HumanSolverStrategies.entries.forEach {  }

            println(grid)

            // solver.fillCells(grid, cache).madeChanges() shouldBe true
            // solver.fillCells(grid, cache).madeChanges() shouldBe false

            println(grid)

            assertSoftly {
                withClue("cell 14 got 1,4 removed as they must sum up to 5") {
                    grid.cells[14].possibles shouldContainExactly setOf(2, 3)
                }
                withClue("cell 15 got 1,4 removed as they must sum up to 5") {
                    grid.cells[15].possibles shouldContainExactly setOf(2, 3)
                }
            }
        }
    })
