package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class HumanDifficultyCalculatorFactoryImpl : HumanDifficultyCalculatorFactory {
    override fun createCalculator(grid: Grid): HumanDifficultyCalculator = HumanDifficultyCalculatorImpl(grid)
}
