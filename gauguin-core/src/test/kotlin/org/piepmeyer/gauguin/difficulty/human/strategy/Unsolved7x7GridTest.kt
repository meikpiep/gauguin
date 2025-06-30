package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolver
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.grid.GridCageAction

class Unsolved7x7GridTest :
    FunSpec({

        test("4x4 grid") {
            // 126*: 3 6 7
            // 18*: 1 3 6
            // first square: 1 4 5 5
            // second square: 2 2 4 7

            val grid =
                GridBuilder(2, 7)
                    .addCage(
                        126,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.ANGLE_RIGHT_BOTTOM,
                        0,
                    ).addCage(
                        18,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.ANGLE_LEFT_TOP,
                        3,
                    ).addCage(15, GridCageAction.ACTION_ADD, GridCageType.SQUARE, 6)
                    .addCage(15, GridCageAction.ACTION_ADD, GridCageType.SQUARE, 10)
                    .createGrid()

            println(grid)

            // 126*
            grid.cells[0].possibles = setOf(3, 6, 7)
            grid.cells[1].possibles = setOf(3, 6, 7)
            grid.cells[2].possibles = setOf(3, 6, 7)
            // 18*
            grid.cells[3].possibles = setOf(1, 3, 6)
            grid.cells[4].possibles = setOf(1, 3, 6)
            grid.cells[5].possibles = setOf(1, 2, 3, 6)
            // squares
            grid.cells[6].possibles = setOf(1, 2, 3, 4, 5, 6, 7)
            grid.cells[7].possibles = setOf(1, 2, 3, 4, 5, 6, 7)
            grid.cells[8].possibles = setOf(1, 2, 3, 4, 5, 6, 7)
            grid.cells[9].possibles = setOf(1, 2, 3, 4, 5, 6, 7)
            grid.cells[10].possibles = setOf(1, 2, 3, 4, 5, 6, 7)
            grid.cells[11].possibles = setOf(1, 2, 3, 4, 5, 6, 7)
            grid.cells[12].possibles = setOf(1, 2, 3, 4, 5, 6, 7)
            grid.cells[13].possibles = setOf(1, 2, 3, 4, 5, 6, 7)

            val cache = HumanSolverCache(grid)
            cache.initialize()
            cache.validateAllEntries()

            val solver = HumanSolver(grid) // LinesSingleCagePossiblesSumDual()
            solver.prepareGrid()
            solver.solveAndCalculateDifficulty(true)

            println(grid)

            // solver should find two possibles and delete one of them for each run
            // solver.fillCells(grid, cache).first shouldBe true

            println(grid)

            assertSoftly {
                withClue("cell 5") {
                    grid.cells[5].possibles shouldContainExactly setOf(1, 3, 6)
                }
            }
        }
    })
