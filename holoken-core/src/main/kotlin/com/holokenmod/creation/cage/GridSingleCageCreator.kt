package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridCell
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.system.exitProcess

class GridSingleCageCreator(
    private val grid: Grid,
    private val cage: GridCage
) {
    val id = cage.id

    // The following two variables are required by the recursive methods below.
    // They could be passed as parameters of the recursive methods, but this
    // reduces performance.
    private var numbers: IntArray = IntArray(1)
    private var possibleCombinations: ArrayList<IntArray> = ArrayList()
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
            GridCageAction.ACTION_SUBTRACT -> {
                val possibles = mutableListOf<IntArray>()

                for (digit in grid.possibleDigits) {
                    for (otherDigit in grid.possibleDigits) {
                        if (abs(digit - otherDigit) == cage.result) {
                            possibles += intArrayOf(digit, otherDigit)
                        }
                    }
                }

                possibles
            }
            GridCageAction.ACTION_DIVIDE -> allDivideResults()
            GridCageAction.ACTION_ADD -> getalladdcombos(cage.result, cage.numberOfCells)
            GridCageAction.ACTION_MULTIPLY -> getallmultcombos(
                cage.result,
                cage.numberOfCells
            )
        }
    }

    fun allDivideResults(): List<IntArray> {
        val results = mutableListOf<IntArray>()

        for (digit in grid.possibleDigits) {
            if (cage.result == 0 || digit % cage.result == 0) {
                val otherDigit: Int = if (cage.result == 0) {
                    0
                } else {
                    digit / cage.result
                }
                if (digit != otherDigit && grid.possibleDigits.contains(otherDigit)) {
                    results += intArrayOf(digit, otherDigit)
                    results += intArrayOf(otherDigit, digit)
                }
            }
        }

        return results
    }

    private fun getalladdcombos(target_sum: Int, n_cells: Int): List<IntArray> {
        numbers = IntArray(n_cells)
        possibleCombinations = ArrayList()

        getaddcombos(target_sum, n_cells)

        return possibleCombinations
    }

    private fun getaddcombos(target_sum: Int, n_cells: Int) {
        if (n_cells == 1) {
            if (grid.possibleDigits.contains(target_sum)) {
                numbers[0] = target_sum
                if (satisfiesConstraints(numbers)) {
                    possibleCombinations.add(numbers.clone())
                }
            }
            return
        }
        for (n in grid.possibleDigits) {
            numbers[n_cells - 1] = n
            getaddcombos(target_sum - n, n_cells - 1)
        }
    }

    private fun getallmultcombos(target_sum: Int, n_cells: Int): ArrayList<IntArray> {
        val multipleCreator = MultiplicationCreator(this, grid, target_sum, n_cells)
        return multipleCreator.create()
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
    fun satisfiesConstraints(test_nums: IntArray): Boolean {
        val squareOfNumbers =
            grid.gridSize.amountOfNumbers.toDouble().pow(2.0).roundToLong().toInt()
        val constraints = BooleanArray(squareOfNumbers * 2 * 10)
        var constraint_num: Int
        for (i in 0 until cage.numberOfCells) {
            val numberToTestIndex = grid.options.digitSetting.indexOf(test_nums[i])
            if (numberToTestIndex == -1) {
                LOGGER.error("No index of number " + test_nums[i] + " of cage " + cage.toString())
                exitProcess(0)
            }
            constraint_num = grid.gridSize.width * numberToTestIndex + cage.getCell(i).column
            if (constraints[constraint_num]) {
                return false
            }
            constraints[constraint_num] = true
            constraint_num = squareOfNumbers + grid.gridSize.width * numberToTestIndex + cage.getCell(
                i
            ).row
            if (constraints[constraint_num]) {
                return false
            }
            constraints[constraint_num] = true
        }
        return true
    }

    val numberOfCells: Int
        get() = cage.numberOfCells

    fun getCell(i: Int): GridCell {
        return cage.getCell(i)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(
            GridSingleCageCreator::class.java
        )
    }
}