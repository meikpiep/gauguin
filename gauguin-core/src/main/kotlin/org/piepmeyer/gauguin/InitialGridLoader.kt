package org.piepmeyer.gauguin

import kotlinx.coroutines.runBlocking
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
    fun initialGrid(): Grid {
        SaveGame.autosaveByDirectory(this.filesDir).restore()?.let {
            return it
        }

        return runBlocking {
            val grid = GridCalculatorFactory().createCalculator(initialGameVariant()).calculate()
            grid.isActive = true

            grid
        }
    }

    private fun initialGameVariant(): GameVariant =
        GameVariant(
            GridSize(6, 6),
            GameOptionsVariant.createClassic(),
        )
}
