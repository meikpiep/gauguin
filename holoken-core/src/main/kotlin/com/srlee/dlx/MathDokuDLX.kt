package com.srlee.dlx

import com.holokenmod.creation.cage.GridSingleCageCreator
import com.holokenmod.grid.Grid

class MathDokuDLX(grid: Grid) {
    private var dlx: DLX

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
        val creators: MutableCollection<GridSingleCageCreator> = ArrayList()
        for (cage in grid.cages) {
            creators.add(GridSingleCageCreator(grid.variant, cage))
        }
        for (creator in creators) {
            numberOfNodes += creator.possibleNums.size * (2 * creator.numberOfCells + 1)
        }

        dlx = DLX(2 * grid.gridSize.surfaceArea + creators.size, numberOfNodes)

        var currentCombination = 0
        val digitSetting = grid.options.digitSetting
        for (creator in creators) {
            for (possibleCageCombination in creator.possibleNums) {
                for (i in possibleCageCombination.indices) {
                    val indexOfDigit = digitSetting.indexOf(possibleCageCombination[i])

                    val columnConstraint =
                        grid.gridSize.width * indexOfDigit + creator.getCell(i).column
                    val rowConstraint =
                        grid.gridSize.surfaceArea + grid.gridSize.width * indexOfDigit + creator.getCell(
                            i
                        ).row

                    dlx.addNode(columnConstraint, currentCombination)
                    dlx.addNode(rowConstraint, currentCombination)
                }

                val cageConstraint = 2 * grid.gridSize.surfaceArea + creator.id

                dlx.addNode(cageConstraint, currentCombination)

                currentCombination++
            }
        }
    }

    fun solve(type: DLX.SolveType): Int {
        return dlx.solve(type)
    }
}
