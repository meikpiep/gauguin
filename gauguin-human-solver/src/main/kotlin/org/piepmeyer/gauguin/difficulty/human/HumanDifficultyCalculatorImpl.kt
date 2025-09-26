package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class HumanDifficultyCalculatorImpl(
    private val grid: Grid,
) : HumanDifficultyCalculator {
    override fun ensureDifficultyCalculated() {
        if (grid.difficulty.humanDifficulty != null) {
            return
        }

        val solverResult = calculateDifficulty()

        grid.difficulty =
            grid.difficulty.copy(
                humanDifficulty = solverResult.difficulty,
                solvedViaHumanDifficulty = solverResult.success,
            )
    }

    private fun calculateDifficulty(): HumanSolverResult {
        val newGrid = grid.copyWithEmptyUserValues()

        val solver = HumanSolver(newGrid)
        solver.prepareGrid()

        return solver.solveAndCalculateDifficulty()
    }
}
