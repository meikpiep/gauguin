package com.holokenmod.creation.cage

import com.holokenmod.Randomizer
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridCell
import com.holokenmod.options.GridCageOperation
import kotlin.math.max
import kotlin.math.min

internal class GridCageOperationDecider(
    private val randomizer: Randomizer,
    private val cells: List<GridCell>,
    private val operationSet: GridCageOperation
) {
    fun decideOperation(): GridCageAction? {
        if (cells.size == 1) {
            return null
        }
        if (operationSet == GridCageOperation.OPERATIONS_MULT) {
            return GridCageAction.ACTION_MULTIPLY
        }
        return when (getCalculationDecision()) {
            CageCalculationDecision.ADDITION_AND_SUBTRACTION ->
                decideBetweenAdditionAndSubtraction()
            CageCalculationDecision.MULTIPLICATION_AND_DIVISION ->
                decideBetweenMultiplicationAndDivision()
        }
    }

    private fun decideBetweenAdditionAndSubtraction(): GridCageAction {
        if (cells.size > 2 || operationSet == GridCageOperation.OPERATIONS_ADD_MULT) {
            return GridCageAction.ACTION_ADD
        }
        val randomValue = randomizer.nextDouble()
        return if (randomValue >= 0.25) {
            GridCageAction.ACTION_SUBTRACT
        } else {
            GridCageAction.ACTION_ADD
        }
    }

    private fun decideBetweenMultiplicationAndDivision(): GridCageAction {
        if (cells.size > 2 || operationSet == GridCageOperation.OPERATIONS_ADD_MULT) {
            return GridCageAction.ACTION_MULTIPLY
        }
        val randomValue = randomizer.nextDouble()
        return if (randomValue >= 0.25 && canHandleDivide()) {
            GridCageAction.ACTION_DIVIDE
        } else {
            GridCageAction.ACTION_MULTIPLY
        }
    }

    private fun canHandleDivide(): Boolean {
        val cell1Value = cells[0].value
        val cell2Value = cells[1].value

        val higher = max(cell1Value, cell2Value)
        val lower = min(cell1Value, cell2Value)

        if (lower == 0 && higher == 0) {
            return false
        }

        return if (lower == 0 || higher == 0) {
            true
        } else {
            higher % lower == 0
        }
    }

    private fun getCalculationDecision(): CageCalculationDecision {
        if (operationSet == GridCageOperation.OPERATIONS_ADD_SUB) {
            return CageCalculationDecision.ADDITION_AND_SUBTRACTION
        }
        val randomValue = randomizer.nextDouble()
        return if (randomValue >= 0.5) {
            CageCalculationDecision.MULTIPLICATION_AND_DIVISION
        } else {
            CageCalculationDecision.ADDITION_AND_SUBTRACTION
        }
    }
}
