package org.piepmeyer.gauguin

import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.game.save.SaveGame
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import java.io.File

class InitialGridLoader(
    private val filesDir: File,
) {
    suspend fun initialGrid(): Grid {
        SaveGame.autosaveByDirectory(this.filesDir).loadGrid()?.let {
            return it
        }

        val grid = GridCalculatorFactory().createCalculator(initialGameVariant()).calculate()
        grid.isActive = true

        return grid
    }

    private fun initialGameVariant(): GameVariant =
        GameVariant(
            GridSize(6, 6),
            GameOptionsVariant.createClassic(),
        )
}
