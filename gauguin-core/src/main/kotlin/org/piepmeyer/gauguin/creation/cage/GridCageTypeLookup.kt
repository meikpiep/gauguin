package org.piepmeyer.gauguin.creation.cage

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

class GridCageTypeLookup(
    private val grid: Grid,
    private val cageCells: List<GridCell>,
) {
    fun lookupType(): GridCageType? {
        val firstCell = cageCells.minBy { it.cellNumber }

        return GridCageType.entries
            .filter { it.coordinates.size == cageCells.size }
            .firstOrNull {
                it.coordinates.all { coordinate ->
                    val possibleCell =
                        grid.getCellAt(
                            firstCell.row + coordinate.second,
                            firstCell.column + coordinate.first,
                        )

                    possibleCell != null && cageCells.contains(possibleCell)
                }
            }
    }
}
