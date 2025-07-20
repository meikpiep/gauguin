package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.difficulty.human.strategy.DetectPossibleUsedInLinesByOtherCagesDualLines
import org.piepmeyer.gauguin.difficulty.human.strategy.DetectPossiblesBreakingOtherCagesPossiblesDualLines
import org.piepmeyer.gauguin.difficulty.human.strategy.GridSumEnforcesCageSum
import org.piepmeyer.gauguin.difficulty.human.strategy.HiddenPair
import org.piepmeyer.gauguin.difficulty.human.strategy.LineSingleCagePossiblesSumSingle
import org.piepmeyer.gauguin.difficulty.human.strategy.LinesSingleCagePossiblesSumDual
import org.piepmeyer.gauguin.difficulty.human.strategy.LinesSingleCagePossiblesSumTriple
import org.piepmeyer.gauguin.difficulty.human.strategy.MinMaxSumOneLine
import org.piepmeyer.gauguin.difficulty.human.strategy.MinMaxSumThreeLines
import org.piepmeyer.gauguin.difficulty.human.strategy.MinMaxSumTwoLines
import org.piepmeyer.gauguin.difficulty.human.strategy.NakedPair
import org.piepmeyer.gauguin.difficulty.human.strategy.NakedTriple
import org.piepmeyer.gauguin.difficulty.human.strategy.NumberOfCagesWithPossibleForcesPossibleInCage
import org.piepmeyer.gauguin.difficulty.human.strategy.OddEvenCheckGridSum
import org.piepmeyer.gauguin.difficulty.human.strategy.OddEvenCheckSumDual
import org.piepmeyer.gauguin.difficulty.human.strategy.OddEvenCheckSumSingle
import org.piepmeyer.gauguin.difficulty.human.strategy.OddEvenCheckSumTriple
import org.piepmeyer.gauguin.difficulty.human.strategy.PossibleMustBeContainedInSingleCageInLine
import org.piepmeyer.gauguin.difficulty.human.strategy.PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCageCombinations
import org.piepmeyer.gauguin.difficulty.human.strategy.RemoveImpossibleCombinationInLine
import org.piepmeyer.gauguin.difficulty.human.strategy.RemovePossibleWithoutCombination
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInCage
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInCell
import org.piepmeyer.gauguin.difficulty.human.strategy.SinglePossibleInLine
import org.piepmeyer.gauguin.difficulty.human.strategy.TwoCellsPossiblesSumSingleLine
import org.piepmeyer.gauguin.difficulty.human.strategy.TwoCellsPossiblesSumThreeLines
import org.piepmeyer.gauguin.difficulty.human.strategy.TwoCellsPossiblesSumTwoLines
import org.piepmeyer.gauguin.difficulty.human.strategy.XWing

enum class HumanSolverStrategies(
    val difficulty: Int,
    val solver: HumanSolverStrategy,
) {
    ASinglePossibleInCell(2, SinglePossibleInCell()),
    ASinglePossibleInCage(3, SinglePossibleInCage()),
    ARemovePossibleWithoutCombination(4, RemovePossibleWithoutCombination()),
    ASinglePossibleInLine(5, SinglePossibleInLine()),
    ARemoveImpossibleCombination(20, RemoveImpossibleCageCombinations()),
    ARemoveImpossibleCombinationInLineBecauseOfSingleCell(25, RemoveImpossibleCombinationInLine()),
    ANakedPair(25, NakedPair()),
    APossibleMustBeContainedInSingleCageInLine(35, PossibleMustBeContainedInSingleCageInLine()),
    APossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages(38, PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages()),
    ANakedTriple(50, NakedTriple()),
    AHiddenPair(70, HiddenPair()),

    ASingleLinePossiblesSum(80, LineSingleCagePossiblesSumSingle()),
    ATwoCellsPossiblesSumSingleLine(85, TwoCellsPossiblesSumSingleLine()),
    ATwoCellsPossiblesSumTwoLines(86, TwoCellsPossiblesSumTwoLines()),
    ATwoCellsPossiblesSumThreeLines(87, TwoCellsPossiblesSumThreeLines()),

    ANumberOfCagesWithPossibleForcesPossibleInCage(89, NumberOfCagesWithPossibleForcesPossibleInCage()),
    AMinMaxSumOneLine(89, MinMaxSumOneLine()),
    AOddEvenCheckSumSingle(90, OddEvenCheckSumSingle()),
    ADetectPossiblesBreakingOtherCagesPossiblesDualLines(95, DetectPossiblesBreakingOtherCagesPossiblesDualLines()),
    ADetectPossibleUsedInLinesByOtherCagesDualLines(98, DetectPossibleUsedInLinesByOtherCagesDualLines()),
    ADualLinesPossiblesSum(100, LinesSingleCagePossiblesSumDual()),
    AOddEvenCheckSumDual(110, OddEvenCheckSumDual()),
    AXWing(120, XWing()),
    AMinMaxSumTwoLines(130, MinMaxSumTwoLines()),
    ATripleLinesPossiblesSum(140, LinesSingleCagePossiblesSumTriple()),
    AOddEvenCheckSumTriple(150, OddEvenCheckSumTriple()),
    AGridSumEnforcesCageSum(160, GridSumEnforcesCageSum()),
    AGridSumOddEvenCheck(200, OddEvenCheckGridSum()),
    AMinMaxSumThreeLines(210, MinMaxSumThreeLines()),
}
