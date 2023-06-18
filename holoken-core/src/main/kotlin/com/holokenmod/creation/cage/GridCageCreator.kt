package com.holokenmod.creation.cage

import com.holokenmod.Randomizer
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCell
import com.holokenmod.options.GridCageOperation
import com.holokenmod.options.SingleCageUsage
import kotlin.math.sqrt

class GridCageCreator(
    private val randomizer: Randomizer,
    private val grid: Grid
) {
    fun createCages() {
        val operationSet = grid.options.cageOperation
        var restart: Boolean
        do {
            restart = false
            var cageId = 0
            if (grid.options.singleCageUsage == SingleCageUsage.FIXED_NUMBER) {
                cageId = createSingleCages()
            }
            for (cell in grid.cells) {
                if (cell.cellInAnyCage()) {
                    continue
                }
                val validCages = getValidCages(grid, cell)
                val cageIndex: Int
                if (validCages.size == 1) {
                    // Only possible cage is a single
                    if (grid.options.singleCageUsage != SingleCageUsage.DYNAMIC) {
                        grid.clearAllCages()
                        restart = true
                        break
                    } else {
                        cageIndex = 0
                    }
                } else {
                    cageIndex = validCages[randomizer.nextInt(validCages.size - 1) + 1]
                }
                val cage: GridCage = calculateCageArithmetic(cageId++, cell, CAGE_COORDS[cageIndex], operationSet)
                grid.addCage(cage)
            }
        } while (restart)
        grid.updateBorders()

        println(grid)
        grid.setCageTexts()
    }

    private fun createSingleCages(): Int {
        val singles = (sqrt(grid.gridSize.surfaceArea.toDouble()) / 2).toInt()
        val rowUsed = BooleanArray(grid.gridSize.height)
        val colUsed = BooleanArray(grid.gridSize.width)
        val valUsed = BooleanArray(grid.gridSize.amountOfNumbers)

        for (cageId in 0 until singles) {
            var cell: GridCell
            var cellIndex: Int

            do {
                cell = grid.getCell(
                    randomizer.nextInt(grid.gridSize.surfaceArea)
                )
                cellIndex = grid.options.digitSetting.indexOf(cell.value)
            } while (rowUsed[cell.row] || colUsed[cell.column] || valUsed[cellIndex])

            colUsed[cell.column] = true
            rowUsed[cell.row] = true
            valUsed[cellIndex] = true
            val cage = GridCage.createWithSingleCellArithmetic(cageId, grid, cell)
            grid.addCage(cage)
        }
        return singles
    }

    private fun getValidCages(grid: Grid, origin: GridCell): List<Int> {
        val valid = mutableListOf<Int>()

        for (cage_num in CAGE_COORDS.indices) {
            val cellCoordinates = CAGE_COORDS[cage_num]
            var validCage = true
            for (cellCoordinate in cellCoordinates) {
                val col = origin.column + cellCoordinate.first
                val row = origin.row + cellCoordinate.second
                try {
                    val c = grid.getCellAt(row, col)

                    if (c.cellInAnyCage()) {
                        validCage = false
                        break
                    }
                } catch (e: java.lang.RuntimeException) {
                    validCage = false
                    break
                }
            }
            if (validCage) {
                valid.add(cage_num)
            }
        }
        return valid
    }

    private fun cellsFromCoordinates(origin: GridCell, coordinates: Array<Pair<Int, Int>>): List<GridCell> {
        return coordinates.toList().map {
            val col = origin.column + it.first
            val row = origin.row + it.second

            grid.getCellAt(row, col)
        }
    }

    private fun calculateCageArithmetic(
        id: Int,
        origin: GridCell,
        cellCoordinates: Array<Pair<Int, Int>>,
        operationSet: GridCageOperation
    ): GridCage {
        val cells = cellsFromCoordinates(origin, cellCoordinates)

        val decider = GridCageOperationDecider(randomizer, cells, operationSet)
        val operation = decider.decideOperation()

        return if (operation != null) {
            val cage = GridCage.createWithCells(id, grid, operation, origin, cellCoordinates)

            cage.calculateResultFromAction()

            cage
        } else {
            GridCage.createWithSingleCellArithmetic(id, grid, origin)
        }
    }

    companion object {
        // O = Origin (0,0) - must be the upper leftmost cell
        // X = Other cells used in cage
        private val CAGE_COORDS = arrayOf(
            arrayOf(Pair(0, 0)),
            arrayOf(Pair(0, 0), Pair(0, 1)),
            arrayOf(Pair(0, 0), Pair(1, 0)),
            arrayOf(Pair(0, 0), Pair(0, 1), Pair(0, 2)),
            arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0)),
            arrayOf(Pair(0, 0), Pair(0, 1), Pair(1, 1)),
            arrayOf(Pair(0, 0), Pair(0, 1), Pair(-1, 1)),
            arrayOf(Pair(0, 0), Pair(1, 0), Pair(1, 1)),
            arrayOf(Pair(0, 0), Pair(1, 0), Pair(0, 1)),
            arrayOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1)),
            arrayOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(0, 2)),
            arrayOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(-1, 2)),
            arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(0, 1)),
            arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(2, 1))
        )
    }
}
