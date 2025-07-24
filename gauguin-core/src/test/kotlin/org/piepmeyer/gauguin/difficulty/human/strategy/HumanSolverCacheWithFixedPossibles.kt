package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.GridLinesProvider
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class HumanSolverCacheWithFixedPossibles(
    grid: Grid,
    private val cageToPossibles: Map<GridCage, Set<IntArray>>,
) : HumanSolverCache {
    private val gridinesCache = GridLinesProvider(grid)

    override fun possibles(cage: GridCage): Set<IntArray> = cageToPossibles[cage]!!

    override fun adjacentlinesWithEachPossibleValue(numberOfLines: Int): Set<GridLines> =
        gridinesCache.adjacentlinesWithEachPossibleValue(numberOfLines)

    override fun linesWithEachPossibleValue(): Set<GridLine> = gridinesCache.linesWithEachPossibleValue()

    override fun adjacentlines(numberOfLines: Int): Set<GridLines> = gridinesCache.adjacentlines(numberOfLines)

    override fun allLines(): Set<GridLine> = gridinesCache.allLines()
}
