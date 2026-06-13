package org.piepmeyer.gauguin.difficulty.human2

import org.piepmeyer.gauguin.grid.Grid

fun interface HumanDifficulty2CalculatorFactory {
    fun createCalculator(grid: Grid): HumanDifficulty2Calculator
}
