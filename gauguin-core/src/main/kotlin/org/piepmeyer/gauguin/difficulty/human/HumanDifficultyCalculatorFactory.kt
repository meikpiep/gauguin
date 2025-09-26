package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

fun interface HumanDifficultyCalculatorFactory {
    fun createCalculator(grid: Grid): HumanDifficultyCalculator
}
