package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class HumanSolver(
    private val grid: Grid,
) {
    private val humanSolverStrategy =
        listOf(
            HumanSolverStrategySingleCage(),
            HumanSolverStrategySinglePossibleInCell(),
            HumanSolverStrategySinglePossibleInCage(),
            HumanSolverStrategySinglePossibleInLine(),
            HumanSolverStrategyRemoveImpossibleValue(),
            HumanSolverStrategyNakedPair(),
            HumanSolverStrategyRemoveImpossibleCombination(),
            HumanSolverStrategyRemoveImpossibleCombinationInLineBecauseOfSingleCell(),
            HumanSolverStrategyPossibleMustBeContainedInSingleCageInLine(),
            HumanSolverStrategyPossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages(),
            HumanSolverStrategyRemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage(),
        )

    fun solveAndCalculateDifficulty(): HumanSolverResult {
        var progress: HumanSolverStep
        var success = true
        var difficulty = 0

        do {
            progress = doProgress()

            if (progress.success) {
                difficulty += progress.difficulty
            } else if (!grid.isSolved()) {
                success = false

                println("Sad via grid:\n$grid")
            }
        } while (progress.success && !grid.isSolved())

        return HumanSolverResult(success, difficulty)
    }

    private fun doProgress(): HumanSolverStep {
        humanSolverStrategy.forEach {
            val progress = it.fillCells(grid)

            if (progress) {
                return HumanSolverStep(true, it.difficulty())
            }
        }

        return HumanSolverStep(false, 0)
    }
}
