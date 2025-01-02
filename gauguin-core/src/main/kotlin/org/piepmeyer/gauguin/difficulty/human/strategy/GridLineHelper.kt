package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.grid.GridCage

object GridLineHelper {
    fun getIntersectingCagesAndPossibles(
        dualLines: Set<GridLine>,
        cache: HumanSolverCache,
    ): Pair<Set<GridCage>, Map<GridCage, Set<List<Int>>>> {
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

        return Pair(cagesIntersectingWithLines, possiblesInLines)
    }
}
