package org.piepmeyer.gauguin.difficulty.human.strategy

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

private val logger = KotlinLogging.logger {}

/*
 * Detects and deletes possibles if a possible is included in a single combination
 * of the cage and that combination may not be chosen because there is another cell
 * in the line which only has possibles left contained in the single combination.
 *
 * Second check is currently not documented.
 *
 * These checks are done on all single lines, regardless if they contain all
 * possibles or not.
 */
class RemoveImpossibleCombinationInLine : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        val lines = cache.allLines()
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
                            isImpossible(line, cage, cache, singlePossible)
                        ) {
                            cell.removePossible(singleCombinationPossible)
                            return HumanSolverStrategyResult.Success(listOf(cell))
                        }
                    }
                }
            }
        }
        return HumanSolverStrategyResult.NothingChanged()
    }

    private fun isImpossible(
        line: GridLine,
        cage: GridCage,
        cache: HumanSolverCache,
        singlePossible: List<Int>,
    ): Boolean =
        isImpossibleCombinationInLineBecauseOfSingleCell(line, cage, singlePossible) ||
            isImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage(line, cage, cache, singlePossible)

    private fun isImpossibleCombinationInLineBecauseOfSingleCell(
        line: GridLine,
        cage: GridCage,
        singlePossible: List<Int>,
    ): Boolean {
        line
            .cells()
            .filter { it.cage() != cage && !it.isUserValueSet }
            .forEach { otherCell ->
                if (singlePossible.containsAll(otherCell.possibles)) {
                    return true
                }
            }

        return false
    }

    private fun isImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage(
        line: GridLine,
        cage: GridCage,
        cache: HumanSolverCache,
        singlePossible: List<Int>,
    ): Boolean {
        line
            .cages()
            .filter { it != cage }
            .forEach { otherCage ->
                val validPossiblesOtherCage =
                    cache.possibles(otherCage)

                val otherCageLineCellsIndexes =
                    otherCage.cells
                        .filter { line.contains(it) && !it.isUserValueSet }
                        .map { otherCage.cells.indexOf(it) }

                val allPossiblesInvalid =
                    validPossiblesOtherCage.all { validPossibles ->
                        validPossibles
                            .filterIndexed { index, _ ->
                                otherCageLineCellsIndexes.contains(index)
                            }.intersect(singlePossible.toSet())
                            .isNotEmpty()
                    }

                if (allPossiblesInvalid) {
                    return true
                }
            }

        return false
    }
}
