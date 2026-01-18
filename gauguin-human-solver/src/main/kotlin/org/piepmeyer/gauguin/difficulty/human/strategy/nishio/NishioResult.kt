package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import org.piepmeyer.gauguin.grid.Grid

sealed interface NishioResult {
    fun hasFindings(): Boolean

    class NothingFound : NishioResult {
        override fun hasFindings(): Boolean = false
    }

    class Contradictions : NishioResult {
        override fun hasFindings(): Boolean = true
    }

    class Solved(
        val solvedGrid: Grid,
    ) : NishioResult {
        override fun hasFindings(): Boolean = true
    }
}
