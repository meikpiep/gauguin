package org.piepmeyer.gauguin.difficulty.human2

import org.piepmeyer.gauguin.grid.Grid

class HumanDifficulty2CalculatorImpl(
    private val grid: Grid,
    private val avoidNishioAndReveal: Boolean = false,
) : HumanDifficulty2Calculator {
    override fun ensureDifficultyCalculated() {
        if (grid.difficulty.humanDifficulty2 != null) {
            return
        }

        val solverResult = calculateDifficulty()

        grid.difficulty =
            grid.difficulty.copy(
                humanDifficulty2 = solverResult.difficulty,
            )
    }

    private fun calculateDifficulty(): HumanSolverResult {
        val newGrid = grid.copyWithEmptyUserValues()

        val solver = HumanSolver(newGrid, avoidNishio = avoidNishioAndReveal)
        solver.prepareGrid()

        return solver.solveAndCalculateDifficulty(avoidNishioAndReveal)
    }
}
