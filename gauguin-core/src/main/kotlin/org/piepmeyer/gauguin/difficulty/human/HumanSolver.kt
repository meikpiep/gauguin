package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.difficulty.human.strategy.NakedPair
import org.piepmeyer.gauguin.difficulty.human.strategy.PossibleMustBeContainedInSingleCageInLine
import org.piepmeyer.gauguin.difficulty.human.strategy.PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCombination
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCombinationInLineBecauseOfSingleCell
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleValue
import org.piepmeyer.gauguin.difficulty.human.strategy.SingleCage
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInCage
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInCell
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInLine
import org.piepmeyer.gauguin.grid.Grid

class HumanSolver(
    private val grid: Grid,
) {
    private val humanSolverStrategy =
        listOf(
            SingleCage(),
            SinglePossibleInCell(),
            SinglePossibleInCage(),
            SinglePossibleInLine(),
            RemoveImpossibleValue(),
            NakedPair(),
            RemoveImpossibleCombination(),
            RemoveImpossibleCombinationInLineBecauseOfSingleCell(),
            PossibleMustBeContainedInSingleCageInLine(),
            PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages(),
            RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage(),
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
