package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLineType
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

            val cagesIntersectingWithLines =
                dualLines
                    .map { it.cages() }
                    .flatten()
                    .filter { it.cells.any { !it.isUserValueSet && cellsOfLines.contains(it) } }
                    .toSet()

            val possiblesInLines =
                cagesIntersectingWithLines.associateWith { cage ->
                    cache
                        .possibles(cage)
                        .map {
                            it.filterIndexed {
                                    index,
                                    _,
                                ->
                                !cage.cells[index].isUserValueSet && cellsOfLines.contains(cage.cells[index])
                            }
                        }.toSet()
                }

            if (dualLines.all { it.type == GridLineType.ROW } && dualLines.all { it.lineNumber == 6 || it.lineNumber == 7 }) {
                println("hau!")
            }

            cagesIntersectingWithLines.forEach { cage ->
                val possiblesForFirstCage = possiblesInLines[cage]!!

                val possibleInEachFirstCageCombination =
                    grid.variant.possibleDigits.filter { possible ->
                        possiblesForFirstCage.all { it.contains(possible) }
                    }

                if (possibleInEachFirstCageCombination.isNotEmpty()) {
                    cagesIntersectingWithLines
                        .filter { it.id > cage.id }
                        .forEach { otherCage ->
                            if (cage.id == 3 && otherCage.id == 15) {
                                println("Hau2")
                            }
                            val possiblesForOtherCage = possiblesInLines[otherCage]!!

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
