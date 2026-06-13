package org.piepmeyer.gauguin.difficulty.human2

import org.piepmeyer.gauguin.grid.Grid

class HumanDifficultyCalculatorImpl(
    private val grid: Grid,
    private val avoidNishioAndReveal: Boolean = false,
) : HumanDifficulty2Calculator {
    override fun ensureDifficultyCalculated() {
        if (grid.difficulty.humanDifficulty != null) {
            return
        }

        val solverResult = calculateDifficulty()

        grid.difficulty =
            grid.difficulty.copy(
                humanDifficulty = solverResult.difficulty,
                solvedViaHumanDifficulty = solverResult.success,
                solvedViaHumanDifficultyIncludingNishio = solverResult.usedNishio,
            )
    }

    private fun calculateDifficulty(): HumanSolverResult {
        val newGrid = grid.copyWithEmptyUserValues()

        val solver = HumanSolver(newGrid, avoidNishio = avoidNishioAndReveal)
        solver.prepareGrid()

        return solver.solveAndCalculateDifficulty(avoidNishioAndReveal)
    }
}
