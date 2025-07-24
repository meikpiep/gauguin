package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction

class HiddenPairTest :
    FunSpec({

        test("1x6 grid") {
            val grid =
                GridBuilder(1, 6)
                    .addSingleCage(2, 0)
                    .addSingleCage(4, 1)
                    .addCage(
                        12,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.DOUBLE_VERTICAL,
                        2,
                    ).addSingleCage(4, 4)
                    .addSingleCage(4, 5)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 2, 3, 4, 5, 6)
            grid.cells[1].possibles = setOf(1, 2, 3, 4, 5, 6)
            grid.cells[2].possibles = setOf(2, 3, 4, 6)
            grid.cells[3].possibles = setOf(2, 3, 4, 6)
            grid.cells[4].possibles = setOf(2, 3)
            grid.cells[5].possibles = setOf(1, 2, 3)

            println(grid)

            var allSinglePossibles = setOf(intArrayOf(1), intArrayOf(2), intArrayOf(3), intArrayOf(4), intArrayOf(5), intArrayOf(6))

            val cageToPossibles =
                mapOf(
                    grid.cages[0] to allSinglePossibles,
                    grid.cages[1] to allSinglePossibles,
                    grid.cages[2] to
                        setOf(
                            intArrayOf(2, 6),
                            intArrayOf(3, 4),
                            intArrayOf(4, 3),
                            intArrayOf(6, 2),
                        ),
                    grid.cages[3] to setOf(intArrayOf(2), intArrayOf(3)),
                    grid.cages[4] to setOf(intArrayOf(1), intArrayOf(2), intArrayOf(3)),
                )
            val cacheWithPossibles = HumanSolverCacheWithFixedPossibles(grid, cageToPossibles)

            val solver = HiddenPair()

            solver.fillCells(grid, cacheWithPossibles).first shouldBe true
            solver.fillCells(grid, cacheWithPossibles).first shouldBe false

            println(grid)

            assertSoftly {
                withClue("possibles should be deleted from other cells") {
                    grid.cells[0].possibles shouldContainExactly setOf(1, 4, 5, 6)
                    grid.cells[1].possibles shouldContainExactly setOf(1, 4, 5, 6)
                    grid.cells[5].possibles shouldContainExactly setOf(1)
                }
            }
        }
    })
