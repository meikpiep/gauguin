package org.piepmeyer.gauguin.difficulty.human2.strategy

import org.piepmeyer.gauguin.difficulty.human2.GridLine
import org.piepmeyer.gauguin.difficulty.human2.GridLines
import org.piepmeyer.gauguin.difficulty.human2.GridLinesProvider
import org.piepmeyer.gauguin.difficulty.human2.HumanSolverCache
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
