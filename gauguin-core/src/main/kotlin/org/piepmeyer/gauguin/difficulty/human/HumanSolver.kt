package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class HumanSolver(
    private val grid: Grid,
) {
    private val humanSolverStrategy =
        listOf(
            HumanSolverStrategySingleCage(),
            HumanSolverStrategySinglePossibleInCage(),
            HumanSolverStrategySinglePossibleInCell(),
            HumanSolverStrategySinglePossibleInLineUnknown(),
            HumanSolverStrategyRemoveImpossibleValue(),
            HumanSolverStrategySinglePossibleInLine(),
            HumanSolverStrategyNakedPair(),
            HumanSolverStrategyRemoveImpossibleCombination(),
        )

    fun solve() {
        var progress: Boolean

        do {
            progress = doProgress()

            println(grid.toString())
        } while (progress && !grid.isSolved())
    }

    private fun doProgress(): Boolean {
        humanSolverStrategy.forEach {
            val progress = it.fillCells(grid)

            if (progress) {
                return true
            }
        }

        return false
    }
}
