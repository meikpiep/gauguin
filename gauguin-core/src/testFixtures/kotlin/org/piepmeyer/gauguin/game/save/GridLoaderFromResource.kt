package org.piepmeyer.gauguin.game.save

import org.piepmeyer.gauguin.grid.Grid
import java.io.File
import java.net.URL

class GridLoaderFromResource(
    private val resource: URL?,
    private val tempFile: File,
) {
    fun loadGrid(): Grid {
        val saveGameContent = resource!!.readText()

        tempFile.writeText(saveGameContent)

        return SaveGame.createWithFile(tempFile).restore()!!
    }
}
