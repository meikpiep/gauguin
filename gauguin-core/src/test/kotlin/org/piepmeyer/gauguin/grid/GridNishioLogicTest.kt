package org.piepmeyer.gauguin.grid

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

private val logger = KotlinLogging.logger {}

class GridNishioLogicTest :
    FunSpec({

        test("valid nishio solution having all cells without value without any possible gets detected") {
            val grid = smallGrid()

            grid.getValidCellAt(0, 0).userValue = 2
            grid.getValidCellAt(1, 1).userValue = 2

            logger.debug { grid }

            GridNishioLogic(grid).isNishioCheckable() shouldBe true
            GridNishioLogic(grid).isNishioSolution() shouldBe true
        }

        test("valid nishio solution having all cells without value without any possible gets solved") {
            val grid = smallGrid()

            grid.getValidCellAt(0, 0).userValue = 2
            grid.getValidCellAt(1, 1).userValue = 2

            logger.debug { grid }

            GridNishioLogic(grid).solveViaNishioSolution()

            grid.getValidCellAt(0, 1).userValue shouldBe 1
            grid.getValidCellAt(1, 0).userValue shouldBe 1
        }

        test("invalid nishio solution gets detected") {
            val grid = smallGrid()

            grid.getValidCellAt(0, 0).userValue = 2
            grid.getValidCellAt(1, 1).userValue = 1

            logger.debug { grid }

            GridNishioLogic(grid).isNishioCheckable() shouldBe true
            GridNishioLogic(grid).isNishioSolution() shouldBe false
        }

        test("3x2 grid valid nishio solution gets filled") {
            val grid =
                GridBuilder(3, 2)
                    .addCageMultiply(9, GridCageType.ANGLE_RIGHT_TOP)
                    .addCageSingle(2)
                    .addCageDivide(2, GridCageType.DOUBLE_VERTICAL)
                    .addValueRow(3, 2, 1)
                    .addValueRow(1, 3, 2)
                    .createGrid()

            grid.cells[0].userValue = 3
            grid.cells[1].userValue = 2
            grid.cells[3].userValue = 1
            grid.cells[4].userValue = 3

            logger.debug { grid }

            GridNishioLogic(grid).isNishioCheckable() shouldBe true
            GridNishioLogic(grid).isNishioSolution() shouldBe true

            withClue("validate solution") {
                GridNishioLogic(grid).solveViaNishioSolution()

                logger.debug { grid }

                grid.cells[2].userValue shouldBe 1
                grid.cells[5].userValue shouldBe 2
            }
        }

        test("3x2 grid empty cell gets not filled on rectangle grid via small side") {
            val grid =
                GridBuilder(3, 2)
                    .addCageMultiply(9, GridCageType.ANGLE_RIGHT_TOP)
                    .addCageSingle(2)
                    .addCageDivide(2, GridCageType.DOUBLE_VERTICAL)
                    .addValueRow(3, 2, 1)
                    .addValueRow(1, 3, 2)
                    .createGrid()

            grid.cells[0].possibles = setOf(3)
            grid.cells[1].possibles = setOf(2)
            grid.cells[3].possibles = setOf(1)
            grid.cells[4].possibles = setOf(3)
            grid.cells[5].userValue = 2

            logger.debug { grid }

            GridNishioLogic(grid).isNishioCheckable() shouldBe false
            GridNishioLogic(grid).isNishioSolution() shouldBe false

            withClue("provoke solution even if not valid to ensure that no small side leads to value being put in cell") {
                GridNishioLogic(grid).solveViaNishioSolution()

                logger.debug { grid }

                grid.cells[2].isUserValueSet shouldBe false
            }
        }

        test("2x3 grid empty cell gets not filled on rectangle grid via small side") {
            val grid =
                GridBuilder(2, 3)
                    .addCageMultiply(9, GridCageType.ANGLE_RIGHT_BOTTOM)
                    .addCageSingle(2)
                    .addCageDivide(2, GridCageType.DOUBLE_HORIZONTAL)
                    .addValueRow(1, 3)
                    .addValueRow(3, 2)
                    .addValueRow(2, 1)
                    .createGrid()

            grid.cells[0].possibles = setOf(1)
            grid.cells[1].possibles = setOf(3)
            grid.cells[2].possibles = setOf(3)
            grid.cells[3].possibles = setOf(2)
            grid.cells[4].userValue = 2

            logger.debug { grid }

            GridNishioLogic(grid).isNishioCheckable() shouldBe false
            GridNishioLogic(grid).isNishioSolution() shouldBe false

            withClue("provoke solution even if not valid to ensure that no small side leads to value being put in cell") {
                GridNishioLogic(grid).solveViaNishioSolution()

                logger.debug { grid }

                grid.cells[5].isUserValueSet shouldBe false
            }
        }

        test("3x3 grid wrong value gets detected") {
            val grid =
                GridBuilder(3, 3)
                    .addCageAdd(6, GridCageType.ANGLE_RIGHT_TOP)
                    .addCageSubtract(2, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
                    .addCageMultiply(2, GridCageType.DOUBLE_HORIZONTAL)
                    .addValueRow(2, 3, 1)
                    .addValueRow(3, 2, 2)
                    .addValueRow(1, 2, 3)
                    .createGrid()

            grid.cells[0].possibles = emptySet()
            grid.cells[1].userValue = 3
            grid.cells[2].userValue = 1
            grid.cells[3].possibles = emptySet()
            grid.cells[4].userValue = 1
            grid.cells[5].userValue = 2
            grid.cells[6].possibles = emptySet()
            grid.cells[7].userValue = 1
            grid.cells[8].userValue = 3

            logger.debug { grid }

            GridNishioLogic(grid).isNishioCheckable() shouldBe true
            GridNishioLogic(grid).isNishioSolution() shouldBe false
        }
    })

private fun smallGrid(): Grid =
    GridBuilder(2)
        .addCageMultiply(2, GridCageType.ANGLE_RIGHT_BOTTOM)
        .addCageSingle(2)
        .addValueRow(2, 1)
        .addValueRow(1, 2)
        .createGrid()
