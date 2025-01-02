package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.GridCage

class GridLines(
    lines: Set<GridLine>,
) : HashSet<GridLine>(lines.size) {
    init {
        addAll(lines)
    }

    fun possiblesInLines(
        cage: GridCage,
        possibles: IntArray,
    ): List<Int> {
        val cellIndexesInLines =
            cage.cells.mapIndexedNotNull { index, cell ->
                if (any { line -> line.contains(cell) }) {
                    index
                } else {
                    null
                }
            }

        return possibles.filterIndexed { index, _ ->
            cellIndexesInLines.contains(index)
        }
    }

    fun allPossiblesInLines(
        cage: GridCage,
        cache: HumanSolverCache,
    ): List<IntArray> {
        val possiblesInLines =
            cache.possibles(cage).map {
                possiblesInLines(cage, it).toIntArray()
            }

        return possiblesInLines
    }
}
