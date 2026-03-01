package org.piepmeyer.gauguin.grid

class GridPossibleSolutionLogic(
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

                cell.possibles.isEmpty() -> {
                    (
                        grid.eachRowContainsEachPossibleValue() &&
                            grid.getCellsAtSameRow(cell).all { it.isUserValueSet }
                    ) ||
                        (
                            grid.eachColumnContainsEachPossibleValue() &&
                                grid.getCellsAtSameColumn(cell).all { it.isUserValueSet }
                        )
                }

                else -> {
                    false
                }
            }
        }

    override fun isSolutionCheckable(): Boolean =
        grid.cells.all { cell ->
            cell.isUserValueSet ||
                cell.possibles.size == 1 ||
                (
                    cell.possibles.isEmpty() &&
                        (
                            (
                                grid.eachRowContainsEachPossibleValue() &&
                                    grid.getCellsAtSameRow(cell).all { it.isUserValueSet }
                            ) ||
                                (
                                    grid.eachColumnContainsEachPossibleValue() &&
                                        grid.getCellsAtSameColumn(cell).all { it.isUserValueSet }
                                )
                        )
                )
        } &&
            !grid.isSolved()

    override fun solveViaSolution() {
        // first set all cells which have no possible set but are the only cell in row or column to have no value
        grid.cells
            .filter { !it.isUserValueSet && it.possibles.isEmpty() }
            .forEach { cell ->
                cell.setUserValueExtern(userValueFromFilledRowOrColumn(cell))
            }

        // the fill all cells with exactly one possible
        grid.cells
            .filter { !it.isUserValueSet && it.possibles.size == 1 }
            .forEach { cell ->
                cell.setUserValueExtern(cell.possibles.first())
            }
    }

    private fun userValueFromFilledRowOrColumn(cell: GridCell): Int? {
        val otherCellsOfRowOrColumn = mutableListOf<List<GridCell>>()

        if (grid.eachColumnContainsEachPossibleValue()) {
            otherCellsOfRowOrColumn += grid.getCellsAtSameColumn(cell)
        }

        if (grid.eachRowContainsEachPossibleValue()) {
            otherCellsOfRowOrColumn += grid.getCellsAtSameRow(cell)
        }

        otherCellsOfRowOrColumn.forEach { otherCells ->
            if (otherCells.all { it.isUserValueSet }) {
                val possiblesLeftOver =
                    grid.variant.possibleDigits - otherCells.map { it.userValue!! }.toSet()

                return possiblesLeftOver.first()
            }
        }

        return null
    }
}
