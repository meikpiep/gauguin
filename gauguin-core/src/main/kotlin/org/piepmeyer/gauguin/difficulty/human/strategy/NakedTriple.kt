package org.piepmeyer.gauguin.difficulty.human.strategy

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.difficulty.human.GridLinesProvider
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid

private val logger = KotlinLogging.logger {}

/**
 * Finds a naked triple, that is three cells in the same row or column which have to same list of
 * exactly two possible values. As these values could not occur in any other cells beside these
 * two, these values get deletes from the other cages possibles.
 */
class NakedTriple : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        GridLinesProvider(grid)
            .allLines()
            .map { it.cells() }
            .forEach { lineCells ->
                val relevantCells = lineCells.filter { !it.isUserValueSet && it.possibles.size <= 3 }

                if (relevantCells.size >= 3) {
                    relevantCells.forEach { cellOne ->
                        (relevantCells - cellOne).forEach { cellTwo ->
                            (relevantCells - cellOne - cellTwo).forEach { cellThree ->
                                val possibles = cellOne.possibles + cellTwo.possibles + cellThree.possibles

                                if (possibles.size == 3) {
                                    val otherCellsWithPossibles =
                                        (lineCells - cellOne - cellTwo - cellThree)
                                            .filter { !it.isUserValueSet }
                                            .filter { it.possibles.intersect(possibles).isNotEmpty() }

                                    if (otherCellsWithPossibles.isNotEmpty()) {
                                        otherCellsWithPossibles.forEach {
                                            it.possibles -= possibles
                                        }

                                        logger.debug {
                                            "Naked triple found: ${cellOne.cellNumber}, ${cellTwo.cellNumber}, ${cellThree.cellNumber}"
                                        }

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
}
