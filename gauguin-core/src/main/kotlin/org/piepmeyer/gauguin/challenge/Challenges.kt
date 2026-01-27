package org.piepmeyer.gauguin.challenge

import org.piepmeyer.gauguin.game.save.SaveGame
import org.piepmeyer.gauguin.grid.Grid
import java.io.File

class Challenges {
    fun zenChallenge(gridSize: Int): Grid {
        val resource = this::class.java.getResource("/org/piepmeyer/gauguin/challenge/game_${gridSize}x$gridSize-easiest.yml")!!.readText()

        val tempFile = File.createTempFile("uff", "uff")
        tempFile.writeText(resource)

        val loadedGrid = SaveGame.createWithFile(tempFile).restore()

        return loadedGrid!!.also { it.isActive = false }
    }

    fun chruncherChallenge(gridSize: Int): Grid {
        val resource = this::class.java.getResource("/org/piepmeyer/gauguin/challenge/game_${gridSize}x$gridSize-hardest.yml")!!.readText()

        val tempFile = File.createTempFile("uff", "uff")
        tempFile.writeText(resource)

        val loadedGrid = SaveGame.createWithFile(tempFile).restore()

        return loadedGrid!!.also { it.isActive = false }
    }
}
