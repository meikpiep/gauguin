package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.GridCage

interface HumanSolverCache {
    fun possibles(cage: GridCage): Set<IntArray>

    fun adjacentlinesWithEachPossibleValue(numberOfLines: Int): Set<GridLines>

    fun linesWithEachPossibleValue(): Set<GridLine>

    fun adjacentlines(numberOfLines: Int): Set<GridLines>

    fun allLines(): Set<GridLine>
}
