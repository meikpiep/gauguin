package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesCache
import org.piepmeyer.gauguin.grid.Grid

class DetectPossibleUsedInLinesByOtherCagesDualLines : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: PossiblesCache,
    ): Boolean {
        val lines = GridLines(grid).adjacentlines(2)

        lines.forEach { dualLines ->

            val cellsOfLines =
                dualLines.map { it.cells() }.flatten()

            val cagesContainedInBothLines =
                dualLines
                    .map { it.cages() }
                    .flatten()
                    .filter { it.cells.all { it.isUserValueSet || cellsOfLines.contains(it) } }
                    .filter { it.cells.any { !it.isUserValueSet } }
                    .toSet()

            cagesContainedInBothLines.forEach { cage ->
                val possiblesForFirstCage =
                    cache
                        .possibles(cage)
                        .map { it.filterIndexed { index, _ -> dualLines.any { it.contains(cage.getCell(index)) } } }

                val possibleInEachFirstCageCombination =
                    grid.variant.possibleDigits.filter { possible ->
                        possiblesForFirstCage.all { it.contains(possible) }
                    }

                if (possibleInEachFirstCageCombination.isNotEmpty()) {
                    cagesContainedInBothLines
                        .filter { it.id > cage.id }
                        .forEach { otherCage ->
                            val possiblesForOtherCage =
                                cache
                                    .possibles(otherCage)
                                    .map { it.filterIndexed { index, _ -> dualLines.any { it.contains(otherCage.getCell(index)) } } }

                            val possibleInEachOtherCageCombination =
                                grid.variant.possibleDigits.filter { possible ->
                                    possiblesForOtherCage.all { it.contains(possible) }
                                }

                            val possiblesInBothCages =
                                possibleInEachFirstCageCombination.intersect(
                                    possibleInEachOtherCageCombination,
                                )

                            if (possiblesInBothCages.isNotEmpty()) {
                                val foreignCells = cellsOfLines - cage.cells - otherCage.cells

                                var found = false

                                possiblesInBothCages.forEach { possible ->
                                    if (foreignCells.any { it.possibles.contains(possible) }) {
                                        found = true

                                        foreignCells.forEach { it.possibles -= possible }
                                    }
                                }

                                if (found) {
                                    return true
                                }
                            }
                        }
                }
            }
        }

        return false
    }
}
