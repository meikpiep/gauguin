package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class HumanDifficultyCalculator(
    private val grid: Grid,
) {
    fun calculateDifficulty(): HumanSolverResult {
        val newGrid = Grid(grid.variant)

        grid.cages.forEach {
            val newCage = GridCage(it.id, newGrid.options.showOperators, it.action, it.cageType)

            it.cells.forEach { newCage.addCell(newGrid.getCell(it.cellNumber)) }

            newCage.result = it.result

            newGrid.addCage(newCage)
        }

        grid.cells.forEach {
            val newCell = newGrid.getCell(it.cellNumber)

            newCell.value = it.value
        }

        val solver = HumanSolver(newGrid)
        solver.prepareGrid()

        return solver.solveAndCalculateDifficulty()
    }
}
