package org.piepmeyer.gauguin.creation.cage

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.SingleCageUsage
import kotlin.math.sqrt

private val logger = KotlinLogging.logger {}

class GridCageCreator(
    private val randomizer: Randomizer,
    private val grid: Grid,
) {
    fun createCages() {
        var restart: Boolean
        do {
            restart = createOneCage()
        } while (restart)
    }

    private fun createOneCage(): Boolean {
        var cageId = 0
        if (grid.options.singleCageUsage == SingleCageUsage.FIXED_NUMBER) {
            cageId = createSingleCages()
        }

        for (cell in grid.cells) {
            if (cell.cellInAnyCage()) {
                continue
            }

            logger.trace { "Calculating cage type..." }
            var cageType: GridCageType? = calculateCageType(cell)
            logger.trace { "Calculating cage type finished." }

            if (cageType == null) {
                // Only possible cage is a single
                if (grid.options.singleCageUsage == SingleCageUsage.DYNAMIC) {
                    cageType = GridCageType.SINGLE
                } else {
                    grid.clearAllCages()
                    logger.trace { "Clearing cages." }
                    return true
                }
            }

            logger.trace { "Calculating cage arithmetic..." }
            val cage = calculateCageArithmetic(cageId++, cell, cageType, grid.options.cageOperation)
            grid.addCage(cage)
            logger.trace { "Calculated cage arithmetic." }
        }

        return false
    }

    private fun calculateCageType(cell: GridCell): GridCageType? {
        val cagesToTry = mutableListOf<GridCageType>()
        cagesToTry += GridCageType.classicCageTypes()
        cagesToTry -= GridCageType.SINGLE

        while (cagesToTry.isNotEmpty()) {
            val cageType = cagesToTry.random(randomizer.random())

            if (isValidCageType(cageType, cell, grid)) {
                return cageType
            }

            cagesToTry -= cageType
        }

        return null
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
                cell =
                    grid.getCell(
                        randomizer.nextInt(grid.gridSize.surfaceArea),
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

    private fun isValidCageType(
        cageType: GridCageType,
        origin: GridCell,
        grid: Grid,
    ) = cageType.coordinates
        .any {
            val col = origin.column + it.first
            val row = origin.row + it.second
            val c = grid.getCellAt(row, col)

            c == null || c.cellInAnyCage()
        }.not()

    private fun cellsFromCoordinates(
        origin: GridCell,
        cageType: GridCageType,
    ): List<GridCell> =
        cageType.coordinates.toList().map {
            val col = origin.column + it.first
            val row = origin.row + it.second

            grid.getValidCellAt(row, col)
        }

    private fun calculateCageArithmetic(
        id: Int,
        origin: GridCell,
        cageType: GridCageType,
        operationSet: GridCageOperation,
    ): GridCage {
        val cells = cellsFromCoordinates(origin, cageType)

        val decider = GridCageOperationDecider(randomizer, cells, operationSet)
        val operation = decider.decideOperation()

        return if (operation == GridCageAction.ACTION_NONE) {
            GridCage.createWithSingleCellArithmetic(id, grid, origin)
        } else {
            val cage = GridCage.createWithCells(id, grid, operation, origin, cageType)

            cage.result = GridCageResultCalculator(cage).calculateResultFromAction()

            cage
        }
    }
}
