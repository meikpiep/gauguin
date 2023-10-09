package org.piepmeyer.gauguin.creation

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

class TestGridCalculator : FunSpec({
    test("bruteForce") {
        val creator = GridCalculator(
            GameVariant(
                GridSize(4, 4),
                GameOptionsVariant.createClassic()
            )
        )
        val grid = creator.calculate()
        solveBruteForce(grid, 0)
    }
})

private fun solveBruteForce(grid: Grid, cellNumber: Int) {
    if (cellNumber == grid.gridSize.surfaceArea) {
        if (isValidSolution(grid)) {
            println("Found valid solution.")
            for (cell in grid.cells) {
                withClue("Found differing solution. $grid") {
                    cell.userValue shouldBe cell.value
                }
            }
        }
        return
    }
    val cell = grid.getCell(cellNumber)
    for (value in grid.variant.possibleDigits) {
        if (!grid.isUserValueUsedInSameColumn(cellNumber, value) &&
            !grid.isUserValueUsedInSameRow(cellNumber, value)
        ) {
            cell.setUserValueIntern(value)
            solveBruteForce(grid, cellNumber + 1)
        }
    }
    cell.setUserValueIntern(GridCell.NO_VALUE_SET)
}

private fun isValidSolution(grid: Grid): Boolean {
    var validSolution = true
    for (cell in grid.cells) {
        validSolution =
            validSolution and !grid.isUserValueUsedInSameColumn(cell.cellNumber, cell.userValue)
        validSolution =
            validSolution and !grid.isUserValueUsedInSameRow(cell.cellNumber, cell.userValue)
    }
    for (cage in grid.cages) {
        validSolution = validSolution and cage.isMathsCorrect()
    }
    return validSolution
}
