package org.piepmeyer.gauguin.creation.cage

import org.piepmeyer.gauguin.creation.cage.operation.AdditionCreator
import org.piepmeyer.gauguin.creation.cage.operation.DivideCreator
import org.piepmeyer.gauguin.creation.cage.operation.MultiplicationCreator
import org.piepmeyer.gauguin.creation.cage.operation.SubtractionCreator
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.options.GameVariant

class GridSingleCageCreator(
    private val variant: GameVariant,
    val cage: GridCage,
) {
    val id = cage.id

    val possibleCombinations: Set<IntArray> by lazy {
        if (variant.options.showOperators) {
            possibleCombinations()
        } else {
            possibleCombinationsNoOperator()
        }
    }

    private fun possibleCombinationsNoOperator(): Set<IntArray> {
        if (cage.numberOfCells == 1) {
            val number = intArrayOf(cage.result)
            return setOf(number)
        }
        if (cage.numberOfCells == 2) {
            return possibleNumsNoOperatorTwoCells()
        }

        val allResults = mutableListOf<IntArray>()

        // ACTION_ADD:
        allResults += getalladdcombos(cage.result, cage.numberOfCells)

        // ACTION_MULTIPLY:
        val multResults = getallmultcombos(cage.result, cage.numberOfCells)

        // Combine Add & Multiply result sets
        for (possibleset in multResults) {
            var foundset = false
            for (currentset in allResults) {
                if (possibleset.contentEquals(currentset)) {
                    foundset = true
                    break
                }
            }
            if (!foundset) {
                allResults.add(possibleset)
            }
        }
        return allResults.toSet()
    }

    private fun possibleNumsNoOperatorTwoCells(): Set<IntArray> {
        val allResults = mutableListOf<IntArray>()

        for (i1 in variant.possibleDigits) {
            for (i2 in i1 + 1..variant.maximumDigit) {
                if (variant.possibleDigits.contains(i2) &&
                    (
                        i2 - i1 == cage.result ||
                            i1 - i2 == cage.result ||
                            cage.result * i1 == i2 ||
                            cage.result * i2 == i1 ||
                            i1 + i2 == cage.result ||
                            i1 * i2 == cage.result
                    )
                ) {
                    allResults.add(intArrayOf(i1, i2))
                    allResults.add(intArrayOf(i2, i1))
                }
            }
        }

        return allResults.toSet()
    }

    private fun possibleCombinations(): Set<IntArray> =
        when (cage.action) {
            GridCageAction.ACTION_NONE -> setOf(intArrayOf(cage.result))
            GridCageAction.ACTION_SUBTRACT -> SubtractionCreator(variant, cage.result).create()
            GridCageAction.ACTION_DIVIDE -> DivideCreator(variant, cage.result).create()
            GridCageAction.ACTION_ADD -> getalladdcombos(cage.result, cage.numberOfCells)
            GridCageAction.ACTION_MULTIPLY ->
                getallmultcombos(
                    cage.result,
                    cage.numberOfCells,
                )
        }

    private fun getalladdcombos(
        targetSum: Int,
        numberOfCells: Int,
    ): Set<IntArray> = AdditionCreator(cage, variant, targetSum, numberOfCells).create()

    private fun getallmultcombos(
        targetSum: Int,
        numberOfCells: Int,
    ): Set<IntArray> = MultiplicationCreator(cage, variant, targetSum, numberOfCells).create()

    val numberOfCells: Int
        get() = cage.numberOfCells

    fun getCell(i: Int): GridCell = cage.getCell(i)
}
