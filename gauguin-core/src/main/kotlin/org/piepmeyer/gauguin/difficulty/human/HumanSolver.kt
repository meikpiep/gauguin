package org.piepmeyer.gauguin.difficulty.human

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.difficulty.human.strategy.NakedPair
import org.piepmeyer.gauguin.difficulty.human.strategy.NakedTriple
import org.piepmeyer.gauguin.difficulty.human.strategy.PossibleMustBeContainedInSingleCageInLine
import org.piepmeyer.gauguin.difficulty.human.strategy.PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCombination
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCombinationInLineBecauseOfSingleCell
import org.piepmeyer.gauguin.difficulty.human.strategy.RemovePossibleWithoutCombination
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInCage
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInCell
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInLine
import org.piepmeyer.gauguin.grid.Grid

private val logger = KotlinLogging.logger {}

class HumanSolver(
    private val grid: Grid,
) {
    private val humanSolverStrategy =
        listOf(
            SinglePossibleInCell(),
            SinglePossibleInCage(),
            SinglePossibleInLine(),
            RemovePossibleWithoutCombination(),
            NakedPair(),
            NakedTriple(),
            RemoveImpossibleCombination(),
            RemoveImpossibleCombinationInLineBecauseOfSingleCell(),
            PossibleMustBeContainedInSingleCageInLine(),
            PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages(),
            RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage(),
        )

    fun solveAndCalculateDifficulty(): HumanSolverResult {
        var progress: HumanSolverStep
        var success = true
        var difficulty = FillSingleCage().fillCells(grid) * 1

        do {
            progress = doProgress()

            if (progress.success) {
                difficulty += progress.difficulty
            } else if (!grid.isSolved()) {
                success = false

                logger.info { "Sad about grid:\n$grid" }
            }
        } while (progress.success && !grid.isSolved())

        return HumanSolverResult(success, difficulty)
    }

    private fun doProgress(): HumanSolverStep {
        humanSolverStrategy.forEach {
            val progress = it.fillCells(grid)

            if (progress) {
                logger.info { "Added ${it.difficulty()} from ${it::class.simpleName}" }
                return HumanSolverStep(true, it.difficulty())
            }
        }

        return HumanSolverStep(false, 0)
    }
}
