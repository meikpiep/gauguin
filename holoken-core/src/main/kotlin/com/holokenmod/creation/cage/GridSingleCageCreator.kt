package com.holokenmod.creation.cage

import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridCell
import com.holokenmod.options.GameVariant
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class GridSingleCageCreator(
    private val variant: GameVariant,
    val cage: GridCage
) {
    val id = cage.id

    val possibleNums: List<IntArray> by lazy {
        if (variant.options.showOperators) {
            setPossibleNums()
        } else {
            setPossibleNumsNoOperator()
        }
    }

    private fun setPossibleNumsNoOperator(): List<IntArray> {
        val allResults = mutableListOf<IntArray>()

        if (cage.action == GridCageAction.ACTION_NONE) {
            val number = intArrayOf(cage.result)
            allResults.add(number)
            return allResults
        }
        if (cage.numberOfCells == 2) {
            for (i1 in variant.possibleDigits) {
                for (i2 in i1 + 1..variant.maximumDigit) {
                    if (i2 - i1 == cage.result || i1 - i2 == cage.result || cage.result * i1 == i2 || cage.result * i2 == i1 || i1 + i2 == cage.result || i1 * i2 == cage.result) {
                        allResults.add(intArrayOf(i1, i2))
                        allResults.add(intArrayOf(i2, i1))
                    }
                }
            }
            return allResults
        }

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
        return allResults
    }

    private fun setPossibleNums(): List<IntArray> {
        return when (cage.action) {
            GridCageAction.ACTION_NONE -> listOf(intArrayOf(cage.result))
            GridCageAction.ACTION_SUBTRACT -> SubtractionCreator(variant, cage.result).create()
            GridCageAction.ACTION_DIVIDE -> DivideCreator(variant, cage.result).create()
            GridCageAction.ACTION_ADD -> getalladdcombos(cage.result, cage.numberOfCells)
            GridCageAction.ACTION_MULTIPLY -> getallmultcombos(
                cage.result,
                cage.numberOfCells
            )
        }
    }

    private fun getalladdcombos(targetSum: Int, numberOfCells: Int): List<IntArray> {
        return AdditionCreator(this, variant, targetSum, numberOfCells).create()
    }

    private fun getallmultcombos(targetSum: Int, numberOfCells: Int): ArrayList<IntArray> {
        return MultiplicationCreator(this, variant, targetSum, numberOfCells).create()
    }

    val numberOfCells: Int
        get() = cage.numberOfCells

    fun getCell(i: Int): GridCell {
        return cage.getCell(i)
    }
}
