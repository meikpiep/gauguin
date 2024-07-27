package org.piepmeyer.gauguin.difficulty.human

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.grid.Grid

private val logger = KotlinLogging.logger {}

/*
 * Detects and deletes possibles if a possible is included in a single combination
 * of the cage and that combination may not be chosen because there is another cell
 * in the line which only has possibles left contained in the single combination
 */
class HumanSolverStrategyRemoveImpossibleCombinationInLine : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val lines = GridLines(grid).linesWithEachPossibleValue()

        lines.forEach { line ->
            line.cages().forEach { cage ->
                val validPossibles =
                    ValidPossiblesCalculator(grid, cage).calculatePossibles()

                val lineCageCells =
                    cage.cells
                        .filter { line.contains(it) && !it.isUserValueSet }

                lineCageCells.forEach { cell ->
                    val cellIndex = cage.cells.indexOf(cell)

                    logger.info { "analysing $line, $cage, $cell" }

                    val validPossiblesOfCell = validPossibles.map { it[cellIndex] }

                    val validPossiblesSetOfCell = validPossiblesOfCell.distinct()

                    val possiblesWithSingleCombination =
                        validPossiblesSetOfCell.filter { validPossible ->
                            validPossiblesOfCell.count { it == validPossible } == 1
                        }

                    logger.info { "set of possible cell values: $validPossiblesSetOfCell" }
                    logger.info { "set of possible cell values with single combination: $possiblesWithSingleCombination" }

                    possiblesWithSingleCombination.forEach { singleCombinationPossible ->
                        val lineCageCellsIndexes =
                            lineCageCells
                                .map { cage.cells.indexOf(it) }

                        val singlePossible =
                            validPossibles
                                .first { it[cellIndex] == singleCombinationPossible }
                                .filterIndexed { index, _ ->
                                    lineCageCellsIndexes.contains(index)
                                }

                        logger.info { "Relevant line indexes: $lineCageCellsIndexes" }
                        logger.info { "Single possible: $singlePossible" }

                        if (singlePossible.isNotEmpty()) {
                            line
                                .cells()
                                .filter { it.cage() != cage && !it.isUserValueSet }
                                .forEach { otherCell ->
                                    if (singlePossible.containsAll(otherCell.possibles)) {
                                        cell.removePossible(singleCombinationPossible)
                                        return true
                                    }
                                }
                        }
                    }
                }
            }
        }

        return false
    }

    override fun difficulty(): Int = 25
}
