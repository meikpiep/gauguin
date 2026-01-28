package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class GridNumberOfCagesWithPossibleForcesPossibleInCageTest :
    FunSpec({

        test("3x3 with artifical possibles detects a cage which must avoid 1 as possible") {
            var possibleWhoseContainMayBeOneShouldBeDetected =
                setOf(intArrayOf(1, 3, 1, 2), intArrayOf(1, 3, 2, 3), intArrayOf(2, 3, 2, 3))

            val (grid, cageToPossibles) =
                GridBuilder(3, 3)
                    .addCageMultiply(4, GridCageType.ANGLE_RIGHT_BOTTOM, setOf(intArrayOf(1, 2, 1)))
                    .addCageAdd(9, GridCageType.TETRIS_T_RIGHT_UP, possibleWhoseContainMayBeOneShouldBeDetected)
                    .addCageDivide(3, GridCageType.DOUBLE_HORIZONTAL, setOf(intArrayOf(1, 3), intArrayOf(3, 1)))
                    .createGridAndCageToPossibles()

            println(grid)

            val cacheWithPossibles = HumanSolverCacheWithFixedPossibles(grid, cageToPossibles)

            val solver = GridNumberOfCagesWithPossibleForcesPossibleInCage()

            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe true
            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe false

            println(grid)

            assertSoftly {
                withClue("cells of 9+ cage get 1 erased from possibles") {
                    grid.cells[2].possibles shouldNotContain 1
                    grid.cells[4].possibles shouldNotContain 1
                    grid.cells[5].possibles shouldNotContain 1
                    grid.cells[8].possibles shouldNotContain 1
                }
            }
        }

        test("3x3 with artifical possibles detects a cage which must include exactly a single 1 as possible") {
            var possibleWhoseContainMayBeOneShouldBeDetected =
                setOf(intArrayOf(1, 3, 1, 2), intArrayOf(3, 1, 2, 3), intArrayOf(2, 3, 2, 3))

            val (grid, cageToPossibles) =
                GridBuilder(3, 3)
                    .addCageMultiply(4, GridCageType.ANGLE_RIGHT_BOTTOM, setOf(intArrayOf(2, 1, 2)))
                    .addCageAdd(9, GridCageType.TETRIS_T_RIGHT_UP, possibleWhoseContainMayBeOneShouldBeDetected)
                    .addCageDivide(3, GridCageType.DOUBLE_HORIZONTAL, setOf(intArrayOf(1, 3), intArrayOf(3, 1)))
                    .createGridAndCageToPossibles()

            println(grid)

            val cacheWithPossibles = HumanSolverCacheWithFixedPossibles(grid, cageToPossibles)

            val solver = GridNumberOfCagesWithPossibleForcesPossibleInCage()

            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe true
            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe false

            println(grid)

            assertSoftly {
                withClue("cells of 9+ cage get 1 erased from possibles but cell 4 is untouched") {
                    grid.cells[2].possibles shouldNotContain 1
                    grid.cells[4].possibles shouldContain 1
                    grid.cells[5].possibles shouldNotContain 1
                }
            }
        }

        test("3x3 with artifical possibles detects a cage which must include exactly a two times 1 as possible") {
            var possibleWhoseContainMayBeOneShouldBeDetected =
                setOf(intArrayOf(1, 3, 1, 2), intArrayOf(3, 1, 2, 3), intArrayOf(2, 3, 2, 3))

            val (grid, cageToPossibles) =
                GridBuilder(3, 3)
                    .addCageMultiply(4, GridCageType.ANGLE_RIGHT_BOTTOM, setOf(intArrayOf(2, 2, 3), intArrayOf(2, 3, 3)))
                    .addCageAdd(9, GridCageType.TETRIS_T_RIGHT_UP, possibleWhoseContainMayBeOneShouldBeDetected)
                    .addCageDivide(3, GridCageType.DOUBLE_HORIZONTAL, setOf(intArrayOf(1, 3), intArrayOf(3, 1)))
                    .createGridAndCageToPossibles()

            println(grid)

            val cacheWithPossibles = HumanSolverCacheWithFixedPossibles(grid, cageToPossibles)

            val solver = GridNumberOfCagesWithPossibleForcesPossibleInCage()

            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe true
            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe false

            println(grid)

            assertSoftly {
                withClue("cells of 9+ cage get 1 erased from possibles but cell 4 is untouched") {
                    grid.cells[2].possibles shouldContain 1
                    grid.cells[4].possibles shouldNotContain 1
                    grid.cells[5].possibles shouldContain 1
                }
            }
        }
    })
