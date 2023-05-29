package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridCell
import mu.KotlinLogging
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class GridSingleCageCreator(
    private val grid: Grid,
    private val cage: GridCage
) {
    val id = cage.id

    val possibleNums: List<IntArray> by lazy {
        if (grid.options.showOperators) {
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
            for (i1 in grid.possibleDigits) {
                for (i2 in i1 + 1..grid.maximumDigit) {
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
            GridCageAction.ACTION_NONE -> {
                val number = intArrayOf(cage.result)
                listOf(number)
            }

            GridCageAction.ACTION_SUBTRACT -> SubtractionCreator(grid, cage.result).create()
            GridCageAction.ACTION_DIVIDE -> DivideCreator(grid, cage.result).create()
            GridCageAction.ACTION_ADD -> getalladdcombos(cage.result, cage.numberOfCells)
            GridCageAction.ACTION_MULTIPLY -> getallmultcombos(
                cage.result,
                cage.numberOfCells
            )
        }
    }

    private fun getalladdcombos(targetSum: Int, numberOfCells: Int): List<IntArray> {
        return AdditionCreator(this, grid, targetSum, numberOfCells).create()
    }

    private fun getallmultcombos(targetSum: Int, numberOfCells: Int): ArrayList<IntArray> {
        return MultiplicationCreator(this, grid, targetSum, numberOfCells).create()
    }

    /*
	 * Check whether the set of numbers satisfies all constraints
	 * Looking for cases where a digit appears more than once in a column/row
	 * Constraints:
	 * 0 -> (getGrid().getGridSize() * getGrid().getGridSize())-1 = column constraints
	 * (each column must contain each digit)
	 * getGrid().getGridSize() * getGrid().getGridSize() -> 2*(getGrid().getGridSize() * getGrid().getGridSize())-1 = row constraints
	 * (each row must contain each digit)
	 */
    fun satisfiesConstraints(numbers: IntArray): Boolean {
        val squareOfNumbers =
            grid.gridSize.amountOfNumbers.toDouble().pow(2.0).roundToLong().toInt()
        val constraints = BooleanArray(squareOfNumbers * 2 * 10)
        var constraintNumber: Int
        for (i in 0 until cage.numberOfCells) {
            val numberToTestIndex = grid.options.digitSetting.indexOf(numbers[i])
            if (numberToTestIndex == -1) {
                logger.error { "No index of number " + numbers[i] + " of cage " + cage.toString() }
                exitProcess(0)
            }
            constraintNumber = grid.gridSize.width * numberToTestIndex + cage.getCell(i).column
            if (constraints[constraintNumber]) {
                return false
            }
            constraints[constraintNumber] = true
            constraintNumber = squareOfNumbers + grid.gridSize.width * numberToTestIndex + cage.getCell(
                i
            ).row
            if (constraints[constraintNumber]) {
                return false
            }
            constraints[constraintNumber] = true
        }
        return true
    }

    val numberOfCells: Int
        get() = cage.numberOfCells

    fun getCell(i: Int): GridCell {
        return cage.getCell(i)
    }
}
