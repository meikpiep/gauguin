package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.difficulty.human.strategy.DualLinesPossiblesSum
import org.piepmeyer.gauguin.difficulty.human.strategy.GridSumEnforcesCageSum
import org.piepmeyer.gauguin.difficulty.human.strategy.NakedPair
import org.piepmeyer.gauguin.difficulty.human.strategy.NakedTriple
import org.piepmeyer.gauguin.difficulty.human.strategy.PossibleMustBeContainedInSingleCageInLine
import org.piepmeyer.gauguin.difficulty.human.strategy.PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCageCombinations
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCombinationInLineBecauseOfSingleCell
import org.piepmeyer.gauguin.difficulty.human.strategy.RemovePossibleWithoutCombination
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInCage
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInCell
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInLine

enum class HumanSolverStrategies(
    val difficulty: Int,
    val solver: HumanSolverStrategy,
) {
    ASinglePossibleInCell(2, SinglePossibleInCell()),
    ASinglePossibleInCage(3, SinglePossibleInCage()),
    ARemovePossibleWithoutCombination(4, RemovePossibleWithoutCombination()),
    ASinglePossibleInLine(5, SinglePossibleInLine()),
    ARemoveImpossibleCombination(20, RemoveImpossibleCageCombinations()),
    ARemoveImpossibleCombinationInLineBecauseOfSingleCell(25, RemoveImpossibleCombinationInLineBecauseOfSingleCell()),
    ARemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage(25, RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage()),
    ANakedPair(25, NakedPair()),
    APossibleMustBeContainedInSingleCageInLine(35, PossibleMustBeContainedInSingleCageInLine()),
    APossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages(38, PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages()),
    ANakedTriple(50, NakedTriple()),

    ADualLinesPossiblesSum(100, DualLinesPossiblesSum()),
    AGridSumEnforcesCageSum(150, GridSumEnforcesCageSum()),
}
