package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

class GridLines(
    lines: Set<GridLine>,
) : HashSet<GridLine>(lines.size) {
    private val cells: Set<GridCell> by lazy {
        map { it.cells() }.flatten().toSet()
    }

    private val cages: Set<GridCage> by lazy {
        map { it.cages() }.flatten().toSet()
    }

    init {
        addAll(lines)
    }

    fun possiblesInLines(
        cage: GridCage,
        possibles: IntArray,
    ): List<Int> {
        val cellIndexesInLines =
            cage.cells.mapIndexedNotNull { index, cell ->
                if (cell in cells) {
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

    fun cells(): Set<GridCell> = cells

    fun cages(): Set<GridCage> = cages

    fun cageCellsInLines(cage: GridCage): List<GridCell> = cage.cells.filter { it in cells }

    fun cageContainedCompletly(cage: GridCage) = cage.cells.all { it in cells }
}
