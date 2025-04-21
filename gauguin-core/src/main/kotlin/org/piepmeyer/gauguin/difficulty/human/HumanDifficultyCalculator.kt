package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class HumanDifficultyCalculator(
    private val grid: Grid,
) {
    fun ensureDifficultyCalculated() {
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
