package org.piepmeyer.gauguin.grid

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class GridCellTest :
    FunSpec({

        context("possiblesToBeFilled") {
            test("two cells with one empty other cell") {
                val cell = cellWithPossibles(emptySet())

                val cage = createCage()

                cage.cells =
                    listOf(
                        cell,
                        cellWithPossibles(emptySet()),
                    )

                cell.cage = cage

                cell.possiblesToBeFilled() shouldBe emptySet()
            }

            test("cells with user value results in empty set of possible values") {
                val cell = cellWithPossibles(emptySet())

                val cage = createCage()

                cage.cells =
                    listOf(
                        cell,
                        cellWithPossibles(setOf(1, 3)),
                    )

                cell.cage = cage
                cell.userValue = 2

                cell.possiblesToBeFilled() shouldBe emptySet()
            }

            test("two cells with one filled other cell") {
                val cell = cellWithPossibles(emptySet())

                val cage = createCage()

                cage.cells =
                    listOf(
                        cell,
                        cellWithPossibles(setOf(1, 3)),
                    )

                cell.cage = cage

                cell.possiblesToBeFilled() shouldBe setOf(1, 3)
            }

            test("three cells with one filled and one unfilled other cell") {
                val cell = cellWithPossibles(emptySet())

                val cage = createCage()

                cage.cells =
                    listOf(
                        cell,
                        cellWithPossibles(setOf(1, 3)),
                        cellWithPossibles(emptySet()),
                    )

                cell.cage = cage

                cell.possiblesToBeFilled() shouldBe setOf(1, 3)
            }

            test("three cells with two filled cells with same data") {
                val cell = cellWithPossibles(emptySet())

                val cage = createCage()

                cage.cells =
                    listOf(
                        cell,
                        cellWithPossibles(setOf(1, 3)),
                        cellWithPossibles(setOf(1, 3)),
                    )

                cell.cage = cage

                cell.possiblesToBeFilled() shouldBe setOf(1, 3)
            }

            test("1 three cells with two filled cells with data were one cell contains all possibles of the other cell") {
                val cell = cellWithPossibles(emptySet())

                val cage = createCage()

                cage.cells =
                    listOf(
                        cell,
                        cellWithPossibles(setOf(1, 3)),
                        cellWithPossibles(setOf(1, 2, 3)),
                    )

                cell.cage = cage

                cell.possiblesToBeFilled() shouldBe setOf(1, 3)
            }

            test("2 three cells with two filled cells with data were one cell contains all possibles of the other cell") {
                val cell = cellWithPossibles(emptySet())

                val cage = createCage()

                cage.cells =
                    listOf(
                        cell,
                        cellWithPossibles(setOf(1, 2, 3)),
                        cellWithPossibles(setOf(1, 3)),
                    )

                cell.cage = cage

                cell.possiblesToBeFilled() shouldBe setOf(1, 3)
            }
        }
    })

private fun createCage() = GridCage(0, true, mockk(), mockk())

private fun cellWithPossibles(possibles: Set<Int>): GridCell {
    val cell = GridCell(0, 0, 0)

    cell.possibles = possibles

    return cell
}
