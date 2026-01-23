package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import org.piepmeyer.gauguin.difficulty.human.PossiblesCache
import org.piepmeyer.gauguin.grid.Grid

class PossiblesCacheByCageNumber(
    grid: Grid,
) {
    val cageNumberToPossiblesMap: Map<Int, Set<IntArray>>

    init {
        val possiblesCache = PossiblesCache(grid)

        possiblesCache.initialize()

        cageNumberToPossiblesMap = grid.cages.associate { Pair(it.id, possiblesCache.possibles(it)) }
    }

    fun possibles(cageId: Int): Set<IntArray> =
        checkNotNull(cageNumberToPossiblesMap[cageId]) {
            "No cage with cageId $cageId found."
        }
}
