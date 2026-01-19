package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class XWingSameCageTest :
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

            val solver = XWingSameCage()

            solver.fillCellsWithNewCache(grid) shouldBe true

            println(grid)

            assertSoftly {
                withClue("cell 8, possibles 1 and 3 should have been deleted") {
                    grid.cells[8].possibles shouldContainExactly setOf(2)
                }
                withClue("cell 6, inner cage wing cell shloud be unchanged") {
                    grid.cells[6].possibles shouldContainExactly setOf(1, 3)
                }
                withClue("cell 11, inner cage wing cell shloud be unchanged") {
                    grid.cells[11].possibles shouldContainExactly setOf(1, 3)
                }
            }
        }
    })
