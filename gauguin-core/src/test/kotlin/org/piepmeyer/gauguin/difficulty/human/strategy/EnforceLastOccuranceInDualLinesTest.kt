package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction

class EnforceLastOccuranceInDualLinesTest :
    FunSpec({

        test("1x6 grid") {
            val (grid, cageToPossibles) =
                GridBuilder(2, 7)
                    .addCage(
                        120,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.DOUBLE_HORIZONTAL,
                        0,
                        setOf(intArrayOf(4, 5), intArrayOf(4, 6), intArrayOf(5, 6), intArrayOf(6, 4), intArrayOf(6, 5)),
                    ) // originally had 4 cells
                    .addCage(
                        8,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.ANGLE_RIGHT_BOTTOM,
                        2,
                        setOf(
                            intArrayOf(1, 2, 4),
                            intArrayOf(1, 4, 2),
                            intArrayOf(2, 1, 4),
                            intArrayOf(2, 4, 1),
                            intArrayOf(4, 1, 2),
                            intArrayOf(4, 2, 1),
                        ),
                    ).addSingleCage(0, 5, setOf(intArrayOf(4), intArrayOf(5), intArrayOf(6), intArrayOf(7)))
                    .addCage(
                        11,
                        GridCageAction.ACTION_ADD,
                        GridCageType.ANGLE_LEFT_BOTTOM,
                        6,
                        setOf(
                            intArrayOf(2, 3, 6),
                            intArrayOf(2, 4, 5),
                            intArrayOf(2, 5, 4),
                            intArrayOf(2, 6, 3),
                            intArrayOf(2, 7, 2),
                            intArrayOf(3, 2, 6),
                            intArrayOf(3, 5, 3),
                            intArrayOf(3, 6, 2),
                            intArrayOf(4, 2, 5),
                            intArrayOf(4, 3, 4),
                            intArrayOf(4, 5, 2),
                            intArrayOf(5, 2, 4),
                            intArrayOf(5, 4, 2),
                            intArrayOf(6, 2, 3),
                            intArrayOf(6, 3, 2),
                        ),
                    ).addCage(
                        105,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.L_VERTICAL_SHORT_RIGHT_BOTTOM,
                        8,
                        setOf(
                            intArrayOf(3, 1, 7, 5),
                            intArrayOf(3, 5, 7, 1),
                            intArrayOf(3, 7, 1, 5),
                            intArrayOf(3, 7, 5, 1),
                            intArrayOf(5, 1, 7, 3),
                            intArrayOf(5, 3, 7, 1),
                            intArrayOf(5, 7, 1, 3),
                            intArrayOf(5, 7, 3, 1),
                        ),
                    ).addSingleCage(0, 11, setOf(intArrayOf(5), intArrayOf(6), intArrayOf(7)))
                    .createGridAndCageToPossibles()

            println(grid)

            val cacheWithPossibles = HumanSolverCacheWithFixedPossibles(grid, cageToPossibles)

            val solver = EnforceLastOccuranceInDualLines()

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
