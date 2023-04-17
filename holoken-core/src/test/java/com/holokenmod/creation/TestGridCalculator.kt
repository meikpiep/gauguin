package com.holokenmod.creation

import com.holokenmod.backtrack.hybrid.MathDokuCage2BackTrack
import com.holokenmod.grid.*
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.RepeatedTest

class TestGridCalculator {
    @RepeatedTest(1)
    fun test3x3Grid() {
        val creator = GridCalculator(
            GameVariant(
                GridSize(9, 9),
                createClassic()
            )
        )
        val grid = creator.calculate()
        val backTrack = MathDokuCage2BackTrack(grid, false)
        val solutions = backTrack.solve()
        MatcherAssert.assertThat(
            "Found $solutions solutions, but there should be exactly one. $grid",
            solutions,
            CoreMatchers.`is`(1)
        )
    }

    @RepeatedTest(1)
    fun bruteForce() {
        val creator = GridCalculator(
            GameVariant(
                GridSize(4, 4),
                createClassic()
            )
        )
        val grid = creator.calculate()
        solveBruteForce(grid, 0)
    }

    private fun solveBruteForce(grid: Grid, cellNumber: Int) {
        if (cellNumber == grid.gridSize.surfaceArea) {
            if (isValidSolution(grid)) {
                println("Found valid solution.")
                for (cell in grid.cells) {
                    MatcherAssert.assertThat(
                        "Found differing solution. $grid",
                        cell.userValue, CoreMatchers.`is`(cell.value)
                    )
                }
            }
            return
        }
        val cell = grid.getCell(cellNumber)
        for (value in grid.possibleDigits) {
            if (!grid.isUserValueUsedInSameColumn(cellNumber, value)
                && !grid.isUserValueUsedInSameRow(cellNumber, value)
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
}