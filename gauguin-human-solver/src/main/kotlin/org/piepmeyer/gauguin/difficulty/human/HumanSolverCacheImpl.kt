package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

class HumanSolverCacheImpl(
    grid: Grid,
) : HumanSolverCache {
    private val possiblesCache = PossiblesCache(grid)
    private val gridinesCache = GridLinesProvider(grid)

    fun initialize() {
        possiblesCache.initialize()
    }

    fun validateEntries(changedCells: Collection<GridCell>) {
        possiblesCache.validateEntries(changedCells)
    }

    fun validateAllEntries() {
        possiblesCache.validateAllEntries()
    }

    override fun possibles(cage: GridCage): Set<IntArray> = possiblesCache.possibles(cage)

    override fun adjacentlinesWithEachPossibleValue(numberOfLines: Int): Set<GridLines> =
        gridinesCache.adjacentlinesWithEachPossibleValue(numberOfLines)

    override fun linesWithEachPossibleValue(): Set<GridLine> = gridinesCache.linesWithEachPossibleValue()

    override fun adjacentlines(numberOfLines: Int): Set<GridLines> = gridinesCache.adjacentlines(numberOfLines)

    override fun allLines(): Set<GridLine> = gridinesCache.allLines()

    companion object {
        fun createValidatedCache(grid: Grid): HumanSolverCacheImpl {
            val cache = HumanSolverCacheImpl(grid)

            cache.initialize()
            cache.validateAllEntries()

            return cache
        }
    }
}
