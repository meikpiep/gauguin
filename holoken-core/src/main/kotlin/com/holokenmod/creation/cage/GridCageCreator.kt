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
                val cageType: GridCageType
                if (validCages.size == 1) {
                    // Only possible cage is a single
                    if (grid.options.singleCageUsage != SingleCageUsage.DYNAMIC) {
                        grid.clearAllCages()
                        restart = true
                        break
                    } else {
                        cageType = GridCageType.SINGLE
                    }
                } else {
                    cageType = validCages[randomizer.nextInt(validCages.size - 1) + 1]
                }
                val cage: GridCage = calculateCageArithmetic(cageId++, cell, cageType, operationSet)
                grid.addCage(cage)
            }
        } while (restart)
        grid.updateBorders()

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

    private fun getValidCages(grid: Grid, origin: GridCell): List<GridCageType> {
        val valid = mutableListOf<GridCageType>()

        for (cageType in GridCageType.values()) {
            var validCage = true
            for (cellCoordinate in cageType.coordinates) {
                val col = origin.column + cellCoordinate.first
                val row = origin.row + cellCoordinate.second
                val c = grid.getCellAt(row, col)

                if (c == null || c.cellInAnyCage()) {
                    validCage = false
                    break
                }
            }
            if (validCage) {
                valid.add(cageType)
            }
        }
        return valid
    }

    private fun cellsFromCoordinates(origin: GridCell, cageType: GridCageType): List<GridCell> {
        return cageType.coordinates.toList().map {
            val col = origin.column + it.first
            val row = origin.row + it.second

            grid.getValidCellAt(row, col)
        }
    }

    private fun calculateCageArithmetic(
        id: Int,
        origin: GridCell,
        cageType: GridCageType,
        operationSet: GridCageOperation
    ): GridCage {
        val cells = cellsFromCoordinates(origin, cageType)

        val decider = GridCageOperationDecider(randomizer, cells, operationSet)
        val operation = decider.decideOperation()

        return if (operation != null) {
            val cage = GridCage.createWithCells(id, grid, operation, origin, cageType)

            cage.calculateResultFromAction()

            cage
        } else {
            GridCage.createWithSingleCellArithmetic(id, grid, origin)
        }
    }
}
