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
        var total_nodes = 0
        val creators: MutableCollection<GridSingleCageCreator> = ArrayList()
        for (cage in grid.cages) {
            creators.add(GridSingleCageCreator(grid, cage))
        }
        for (creator in creators) {
            total_nodes += creator.possibleNums.size * (2 * creator.numberOfCells + 1)
        }

        dlx = DLX(2 * grid.gridSize.surfaceArea + creators.size, total_nodes)

        var currentCombination = 0
        val digitSetting = grid.options.digitSetting
        for (creator in creators) {
            for (possibleCageCombination in creator.possibleNums) {
                // LOGGER.info("cage " + creator.getCage() + " - " + Arrays.toString(onemove));
                for (i in possibleCageCombination.indices) {
                    val indexOfDigit = digitSetting.indexOf(possibleCageCombination[i])

                    // Column constraint
                    dlx.addNode(
                        grid.gridSize.width * indexOfDigit + creator.getCell(i).column + 1,
                        currentCombination
                    )

                    // Row constraint
                    dlx.addNode(
                        grid.gridSize.surfaceArea + grid.gridSize.width * indexOfDigit + creator.getCell(
                            i
                        ).row + 1,
                        currentCombination
                    )
                }

                // Cage constraint
                dlx.addNode(
                    2 * grid.gridSize.surfaceArea + creator.id + 1,
                    currentCombination
                )
                currentCombination++
            }
        }
    }

    fun Solve(type: DLX.SolveType): Int {
        return dlx.Solve(type)
    }
}
