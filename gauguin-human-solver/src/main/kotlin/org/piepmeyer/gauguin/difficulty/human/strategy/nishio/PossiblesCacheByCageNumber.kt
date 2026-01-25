package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.grid.Grid

class PossiblesCacheByCageNumber(
    grid: Grid,
    cache: HumanSolverCache,
) {
    val cageNumberToPossiblesMap: Map<Int, Set<IntArray>> = grid.cages.associate { Pair(it.id, cache.possibles(it)) }

    fun possibles(cageId: Int): Set<IntArray> =
        checkNotNull(cageNumberToPossiblesMap[cageId]) {
            "No cage with cageId $cageId found."
        }
}
