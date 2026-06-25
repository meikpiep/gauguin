package org.piepmeyer.gauguin.difficulty.human2

import org.piepmeyer.gauguin.grid.Grid

class HumanDifficultyCalculatorFactoryImpl : HumanDifficulty2CalculatorFactory {
    override fun createCalculator(grid: Grid): HumanDifficulty2Calculator = HumanDifficulty2CalculatorImpl(grid)
}
