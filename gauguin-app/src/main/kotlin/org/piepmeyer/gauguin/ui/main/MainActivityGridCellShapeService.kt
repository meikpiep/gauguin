package org.piepmeyer.gauguin.ui.main

import org.piepmeyer.gauguin.grid.GridView
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.grid.GridUI

class MainActivityGridCellShapeService(
    private val gridview: GridView,
    private val applicationPreferences: ApplicationPreferences,
) {
    fun calculateCellShape(): GridUI.CellShape {
        if (!applicationPreferences.gridTakesRemainingSpaceIfNecessary || gridview.grid.gridSize.largestSide() <= 9) {
            return GridUI.CellShape.Square
        }

        return GridUI.CellShape.Rectangular
    }
}
