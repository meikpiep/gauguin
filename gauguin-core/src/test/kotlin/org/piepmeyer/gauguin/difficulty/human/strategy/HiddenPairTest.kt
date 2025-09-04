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
            var allSinglePossibles = setOf(intArrayOf(1), intArrayOf(2), intArrayOf(3), intArrayOf(4), intArrayOf(5), intArrayOf(6))

            val (grid, cageToPossibles) =
                GridBuilder(1, 6)
                    .addSingleCage(2, 0, allSinglePossibles)
                    .addSingleCage(4, 1, allSinglePossibles)
                    .addCage(
                        12,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.DOUBLE_VERTICAL,
                        2,
                        setOf(
                            intArrayOf(2, 6),
                            intArrayOf(3, 4),
                            intArrayOf(4, 3),
                            intArrayOf(6, 2),
                        ),
                    ).addSingleCage(4, 4, setOf(intArrayOf(2), intArrayOf(3)))
                    .addSingleCage(4, 5, setOf(intArrayOf(1), intArrayOf(2), intArrayOf(3)))
                    .createGridAndCageToPossibles()

            println(grid)

            val cacheWithPossibles = HumanSolverCacheWithFixedPossibles(grid, cageToPossibles)

            val solver = HiddenPair()

            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe true
            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe false

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
