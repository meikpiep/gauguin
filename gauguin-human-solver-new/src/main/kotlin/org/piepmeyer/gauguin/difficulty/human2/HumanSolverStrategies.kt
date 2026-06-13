package org.piepmeyer.gauguin.difficulty.human2

import org.piepmeyer.gauguin.difficulty.human2.strategy.DetectPossibleUsedInLinesByOtherCagesDualLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.DetectPossiblesBreakingOtherCagesPossiblesDualLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.GridEachCageWithPossibleMustIncludePossibleOnce
import org.piepmeyer.gauguin.difficulty.human2.strategy.GridNumberOfCagesWithPossibleForcesPossibleInCage
import org.piepmeyer.gauguin.difficulty.human2.strategy.GridSumEnforcesCageSum
import org.piepmeyer.gauguin.difficulty.human2.strategy.HiddenPair
import org.piepmeyer.gauguin.difficulty.human2.strategy.LineSingleCagePossiblesSumSingle
import org.piepmeyer.gauguin.difficulty.human2.strategy.LinesSingleCagePossiblesSumDual
import org.piepmeyer.gauguin.difficulty.human2.strategy.LinesSingleCagePossiblesSumTriple
import org.piepmeyer.gauguin.difficulty.human2.strategy.MinMaxSumOneLine
import org.piepmeyer.gauguin.difficulty.human2.strategy.MinMaxSumThreeLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.MinMaxSumTwoLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.NakedPair
import org.piepmeyer.gauguin.difficulty.human2.strategy.NakedTriple
import org.piepmeyer.gauguin.difficulty.human2.strategy.OddEvenCheckGridSum
import org.piepmeyer.gauguin.difficulty.human2.strategy.OddEvenCheckSumDual
import org.piepmeyer.gauguin.difficulty.human2.strategy.OddEvenCheckSumSingle
import org.piepmeyer.gauguin.difficulty.human2.strategy.OddEvenCheckSumTriple
import org.piepmeyer.gauguin.difficulty.human2.strategy.PairOfPossiblesExhaustingTwoLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.PossibleMustBeContainedInSingleCageInLine
import org.piepmeyer.gauguin.difficulty.human2.strategy.PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages
import org.piepmeyer.gauguin.difficulty.human2.strategy.RemoveImpossibleCombinationInLine
import org.piepmeyer.gauguin.difficulty.human2.strategy.RemovePossibleWithoutCombination
import org.piepmeyer.gauguin.difficulty.human2.strategy.SinglePossibleExhaustingTwoLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.SinglePossibleInCell
import org.piepmeyer.gauguin.difficulty.human2.strategy.SinglePossibleInLine
import org.piepmeyer.gauguin.difficulty.human2.strategy.TwoCagesTakeAllPossiblesThreeLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.TwoCagesTakeAllPossiblesTwoLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.TwoCellsPossiblesSumSingleLine
import org.piepmeyer.gauguin.difficulty.human2.strategy.TwoCellsPossiblesSumThreeLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.TwoCellsPossiblesSumTwoLines
import org.piepmeyer.gauguin.difficulty.human2.strategy.XWing
import org.piepmeyer.gauguin.difficulty.human2.strategy.XWingSameCage
import org.piepmeyer.gauguin.difficulty.human2.strategy.YWing
import org.piepmeyer.gauguin.difficulty.human2.strategy.nishio.AdvancedNishioWithPairs
import org.piepmeyer.gauguin.difficulty.human2.strategy.nishio.NishioWithPairs

enum class HumanSolverStrategies(
    val difficulty: Int,
    val solver: HumanSolverStrategy,
    val isNishio: Boolean = false,
) {
    ASinglePossibleInCell(2, SinglePossibleInCell()),

    ARemovePossibleWithoutCombination(4, RemovePossibleWithoutCombination()),
    ASinglePossibleInLine(5, SinglePossibleInLine()),

    ARemoveImpossibleCombinationInLineBecauseOfSingleCell(25, RemoveImpossibleCombinationInLine()),
    ANakedPair(25, NakedPair()),
    APossibleMustBeContainedInSingleCageInLine(35, PossibleMustBeContainedInSingleCageInLine()),
    APossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages(38, PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages()),
    ANakedTriple(50, NakedTriple()),
    AHiddenPair(70, HiddenPair()),

    ASinglePossibleExhaustingTwoLines(75, SinglePossibleExhaustingTwoLines()),

    ATwoCagesTakeAllPossiblesTwoLines(78, TwoCagesTakeAllPossiblesTwoLines()),
    ATwoCagesTakeAllPossiblesThreeLines(79, TwoCagesTakeAllPossiblesThreeLines()),

    ASingleLinePossiblesSum(80, LineSingleCagePossiblesSumSingle()),
    ATwoCellsPossiblesSumSingleLine(85, TwoCellsPossiblesSumSingleLine()),
    ATwoCellsPossiblesSumTwoLines(86, TwoCellsPossiblesSumTwoLines()),
    ATwoCellsPossiblesSumThreeLines(87, TwoCellsPossiblesSumThreeLines()),

    AGridNumberOfCagesWithPossibleForcesPossibleInCage(89, GridNumberOfCagesWithPossibleForcesPossibleInCage()),
    AMinMaxSumOneLine(89, MinMaxSumOneLine()),
    AOddEvenCheckSumSingle(90, OddEvenCheckSumSingle()),
    ADetectPossiblesBreakingOtherCagesPossiblesDualLines(95, DetectPossiblesBreakingOtherCagesPossiblesDualLines()),
    ADetectPossibleUsedInLinesByOtherCagesDualLines(98, DetectPossibleUsedInLinesByOtherCagesDualLines()),
    AGridEachCageWithPossibleMustIncludePossibleOnce(
        99,
        GridEachCageWithPossibleMustIncludePossibleOnce(),
    ),
    ADualLinesPossiblesSum(100, LinesSingleCagePossiblesSumDual()),
    AOddEvenCheckSumDual(110, OddEvenCheckSumDual()),
    AXWing(120, XWing()),
    AXWingSameCage(120, XWingSameCage()),
    AYWing(124, YWing()),

    APairOfPossiblesExhaustingTwoLines(125, PairOfPossiblesExhaustingTwoLines()),

    AMinMaxSumTwoLines(130, MinMaxSumTwoLines()),
    ATripleLinesPossiblesSum(140, LinesSingleCagePossiblesSumTriple()),
    AOddEvenCheckSumTriple(150, OddEvenCheckSumTriple()),
    AGridSumEnforcesCageSum(160, GridSumEnforcesCageSum()),
    AGridSumOddEvenCheck(200, OddEvenCheckGridSum()),
    AMinMaxSumThreeLines(210, MinMaxSumThreeLines()),

    ANishioWithPairs(250, NishioWithPairs(), true),
    AAdvancedNishioWithPairs(350, AdvancedNishioWithPairs(), true),
}
