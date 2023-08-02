package com.holokenmod.creation.dlx

import com.holokenmod.creation.cage.GridSingleCageCreator
import com.holokenmod.grid.Grid
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class MathDokuDLX(
    private val grid: Grid
) {
    private val digitSetting = grid.options.digitSetting
    private val possibleDigits = digitSetting.getPossibleDigits(grid.gridSize)
    private val cartesianProductOfRectingularPossibles = if (grid.gridSize.isSquare) {
        emptyList()
    } else {
        cartesianProduct(
            possibleDigits.indices.toList(),
            grid.gridSize.largestSide() - grid.gridSize.smallestSide()
        )
    }

    var dlx: DLX

    private var numberOfCages = 0

    init {

        // Number of columns = number of constraints =
        // 		BOARD * BOARD (for columns) +
        // 		BOARD * BOARD (for rows)	+
        // 		Num cages (each cage has to be filled once and only once)
        // Number of rows = number of "moves" =
        // 		Sum of all the possible cage combinations
        // Number of nodes = sum of each move:
        //      num_cells column constraints +
        //      num_cells row constraints +
        //      1 (cage constraint)
        var numberOfNodes = 0

        val creators = grid.cages.map {
            GridSingleCageCreator(grid.variant, it)
        }

        for (creator in creators) {
            numberOfNodes += creator.possibleNums.size * (2 * creator.numberOfCells + 1)
        }

        if (!grid.gridSize.isSquare) {
            numberOfNodes += grid.gridSize.largestSide() * cartesianProductOfRectingularPossibles.size * 200
        }

        val extraRectingularCages = if (grid.gridSize.isSquare) {
            0
        } else {
            grid.gridSize.largestSide()
        }

        numberOfCages = creators.size + extraRectingularCages

        dlx = DLX(
            possibleDigits.size * (grid.gridSize.width + grid.gridSize.height) + numberOfCages,
            numberOfNodes
        )

        if (logger.isInfoEnabled) {
            val headerCellId = StringBuilder()
            val headerValue = StringBuilder()

            for (value in possibleDigits) {
                for (column in 0 until grid.gridSize.width) {
                    headerCellId.append("c$column".padEnd(4))
                    headerValue.append("$value".padEnd(4))
                }
            }

            for (value in possibleDigits) {
                for (row in 0 until grid.gridSize.height) {
                    headerCellId.append("r$row".padEnd(4))
                    headerValue.append("$value".padEnd(4))
                }
            }

            for (cage in 0 until numberOfCages) {
                headerValue.append("$cage".padEnd(4))
            }

            logger.info { headerValue.toString() }
            logger.info { headerCellId.toString() }
        }

        val constraints = constraintsFromCages(creators) + constraintsFromRectangular(creators)

        for ((currentCombination, constraint) in constraints.withIndex()) {
            if (logger.isInfoEnabled) {
                val constraintInfo = StringBuilder()

                constraint.forEach { constraintInfo.append(if (it) { "*" } else { "-" }.padEnd(4)) }

                logger.info { constraintInfo.toString() }
            }

            for (constraintIndex in constraint.indices) {
                if (constraint[constraintIndex]) {
                    dlx.addNode(constraintIndex, currentCombination)
                }
            }
        }
    }

    private fun cartesianProduct(values: List<Int>, numberOfCopies: Int): Set<Set<Int>> {
        return UniqueIndexSetsOfGivenLength(values, numberOfCopies).calculateProduct()
    }

    private fun constraintsFromRectangular(creators: List<GridSingleCageCreator>): List<BooleanArray> {
        if (grid.gridSize.isSquare) {
            return emptyList()
        }

        var cageId = creators.size

        val contraints = mutableListOf<BooleanArray>()

        for (rowOrColumn in 0 until grid.gridSize.largestSide()) {
            for (indexesOfDigits in cartesianProductOfRectingularPossibles) {
                val constraint = BooleanArray(
                    possibleDigits.size * (grid.gridSize.width + grid.gridSize.height) +
                        numberOfCages
                )

                for (indexOfDigit in indexesOfDigits) {
                    val (columnConstraint, rowConstraint) = columnAndRowConstraints(
                        indexOfDigit,
                        rowOrColumn,
                        rowOrColumn
                    )

                    if (grid.gridSize.width < grid.gridSize.height) {
                        constraint[rowConstraint] = true
                    } else {
                        constraint[columnConstraint] = true
                    }

                    val cageConstraint = cageConstraint(cageId)
                    constraint[cageConstraint] = true
                }

                contraints += constraint
            }
            cageId++
        }

        return contraints
    }

    private fun constraintsFromCages(creators: List<GridSingleCageCreator>): List<BooleanArray> {
        val contraints = mutableListOf<BooleanArray>()

        for (creator in creators) {
            for (possibleCageCombination in creator.possibleNums) {
                val constraint = BooleanArray(
                    possibleDigits.size * (grid.gridSize.width + grid.gridSize.height) +
                        numberOfCages
                )

                for (i in possibleCageCombination.indices) {
                    val indexOfDigit = digitSetting.indexOf(possibleCageCombination[i])

                    val (columnConstraint, rowConstraint) = columnAndRowConstraints(
                        indexOfDigit,
                        creator,
                        i
                    )

                    constraint[columnConstraint] = true
                    constraint[rowConstraint] = true
                }

                val cageConstraint = cageConstraint(creator.id)

                constraint[cageConstraint] = true

                contraints += constraint
            }
        }

        return contraints
    }

    private fun cageConstraint(cageId: Int): Int {
        return possibleDigits.size * (grid.gridSize.width + grid.gridSize.height) + cageId
    }

    private fun columnAndRowConstraints(
        indexOfDigit: Int,
        creator: GridSingleCageCreator,
        cellOfCage: Int
    ): Pair<Int, Int> {
        return columnAndRowConstraints(
            indexOfDigit,
            creator.getCell(cellOfCage).column,
            creator.getCell(cellOfCage).row
        )
    }

    private fun columnAndRowConstraints(
        indexOfDigit: Int,
        column: Int,
        row: Int
    ): Pair<Int, Int> {
        val columnConstraint = grid.gridSize.width * indexOfDigit + column
        val rowConstraint = (
            grid.gridSize.width * possibleDigits.size +
                grid.gridSize.height * indexOfDigit + row
            )

        return Pair(columnConstraint, rowConstraint)
    }

    fun solve(type: DLX.SolveType): Int {
        return dlx.solve(type)
    }
}
