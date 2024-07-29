package org.piepmeyer.gauguin.difficulty.human

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

enum class HumanSolverStrategies(
    val difficulty: Int,
    val solver: HumanSolverStrategy,
) {
    SinglePossibleInCell(2, SinglePossibleInCell()),
    SinglePossibleInCage(5, SinglePossibleInCage()),
    SinglePossibleInLine(10, SinglePossibleInLine()),
    RemovePossibleWithoutCombination(10, RemovePossibleWithoutCombination()),
    NakedPair(25, NakedPair()),
    NakedTriple(50, NakedTriple()),
    RemoveImpossibleCombination(20, RemoveImpossibleCombination()),
    RemoveImpossibleCombinationInLineBecauseOfSingleCell(25, RemoveImpossibleCombinationInLineBecauseOfSingleCell()),
    PossibleMustBeContainedInSingleCageInLine(35, PossibleMustBeContainedInSingleCageInLine()),
    PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages(38, PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages()),
    RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage(25, RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage()),
}
