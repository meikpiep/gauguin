package org.piepmeyer.gauguin.grid

class GridNishioLogic(
    private val grid: Grid,
) : GridSolutionLogic {
    override fun isValidSolution(): Boolean =
        grid.cells.all { cell ->
            when {
                cell.isUserValueSet -> {
                    cell.isUserValueCorrect
                }

                cell.possibles.size == 1 -> {
                    cell.possibles.first() == cell.value
                }

                else -> {
                    false
                }
            }
        }

    override fun isSolutionCheckable(): Boolean =
        grid.cells.all { cell ->
            cell.isUserValueSet ||
                cell.possibles.size == 1
        } &&
            !grid.isSolved()

    override fun solveViaSolution() {
        grid.cells
            .filter { !it.isUserValueSet && it.possibles.size == 1 }
            .forEach { cell ->
                cell.setUserValueExtern(cell.possibles.first())
            }
    }
}
