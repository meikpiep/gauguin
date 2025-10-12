package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class HiddenPairTest :
    FunSpec({

        test("1x6 grid") {
            var allSinglePossibles = setOf(intArrayOf(1), intArrayOf(2), intArrayOf(3), intArrayOf(4), intArrayOf(5), intArrayOf(6))

            val (grid, cageToPossibles) =
                GridBuilder(1, 6)
                    .addCageSingle(2, allSinglePossibles)
                    .addCageSingle(4, allSinglePossibles)
                    .addCageMultiply(
                        12,
                        GridCageType.DOUBLE_VERTICAL,
                        setOf(
                            intArrayOf(2, 6),
                            intArrayOf(3, 4),
                            intArrayOf(4, 3),
                            intArrayOf(6, 2),
                        ),
                    ).addCageSingle(4, setOf(intArrayOf(2), intArrayOf(3)))
                    .addCageSingle(4, setOf(intArrayOf(1), intArrayOf(2), intArrayOf(3)))
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
