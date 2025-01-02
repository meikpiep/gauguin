package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class HumanSolverCache(
    grid: Grid,
) {
    private val possiblesCache = PossiblesCache(grid)
    private val gridinesCache = GridLinesProvider(grid)

    fun initialize() {
        possiblesCache.initialize()
    }

    fun validateEntries() {
        possiblesCache.validateEntries()
    }

    fun possibles(cage: GridCage): Set<IntArray> = possiblesCache.possibles(cage)

    fun adjacentlinesWithEachPossibleValue(numberOfLines: Int): Set<GridLines> =
        gridinesCache.adjacentlinesWithEachPossibleValue(numberOfLines)

    fun linesWithEachPossibleValue(): Set<GridLine> = gridinesCache.linesWithEachPossibleValue()

    fun adjacentlines(numberOfLines: Int): Set<GridLines> = gridinesCache.adjacentlines(numberOfLines)

    fun allLines(): Set<GridLine> = gridinesCache.allLines()
}
