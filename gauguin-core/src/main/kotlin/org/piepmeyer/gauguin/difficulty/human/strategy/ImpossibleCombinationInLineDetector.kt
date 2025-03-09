package org.piepmeyer.gauguin.difficulty.human.strategy

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.grid.GridCage

private val logger = KotlinLogging.logger {}

object ImpossibleCombinationInLineDetector {
    fun fillCells(
        cache: HumanSolverCache,
        isImpossible: (GridLine, GridCage, cache: HumanSolverCache, List<Int>) -> Boolean,
    ): Boolean {
        val lines = cache.linesWithEachPossibleValue()

        lines.forEach { line ->
            line.cages().forEach { cage ->
                val validPossibles =
                    cache.possibles(cage)

                val lineCageCells =
                    cage.cells
                        .filter { line.contains(it) && !it.isUserValueSet }

                lineCageCells.forEach { cell ->
                    val cellIndex = cage.cells.indexOf(cell)

                    logger.trace { "analysing $line, $cage, $cell" }

                    val validPossiblesOfCell = validPossibles.map { it[cellIndex] }

                    val validPossiblesSetOfCell = validPossiblesOfCell.distinct()

                    val possiblesWithSingleCombination =
                        validPossiblesSetOfCell.filter { validPossible ->
                            validPossiblesOfCell.count { it == validPossible } == 1
                        }

                    logger.trace { "set of possible cell values: $validPossiblesSetOfCell" }
                    logger.trace { "set of possible cell values with single combination: $possiblesWithSingleCombination" }

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

                        logger.trace { "Relevant line indexes: $lineCageCellsIndexes" }
                        logger.trace { "Single possible: $singlePossible" }

                        if (singlePossible.isNotEmpty() &&
                            singleCombinationPossible in cell.possibles &&
                            isImpossible.invoke(line, cage, cache, singlePossible)
                        ) {
                            cell.removePossible(singleCombinationPossible)
                            return true
                        }
                    }
                }
            }
        }

        return false
    }
}
