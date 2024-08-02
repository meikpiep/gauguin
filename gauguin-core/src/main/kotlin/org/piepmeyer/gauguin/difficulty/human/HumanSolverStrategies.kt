package org.piepmeyer.gauguin.difficulty.human

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
    SinglePossibleInCell(2, SinglePossibleInCell()),
    SinglePossibleInCage(3, SinglePossibleInCage()),
    RemovePossibleWithoutCombination(4, RemovePossibleWithoutCombination()),
    SinglePossibleInLine(10, SinglePossibleInLine()),
    RemoveImpossibleCombination(20, RemoveImpossibleCageCombinations()),
    RemoveImpossibleCombinationInLineBecauseOfSingleCell(25, RemoveImpossibleCombinationInLineBecauseOfSingleCell()),
    RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage(25, RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage()),
    NakedPair(25, NakedPair()),
    PossibleMustBeContainedInSingleCageInLine(35, PossibleMustBeContainedInSingleCageInLine()),
    PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages(38, PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages()),
    NakedTriple(50, NakedTriple()),
}
