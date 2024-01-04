package org.piepmeyer.gauguin.challenge

import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.game.save.SavedGrid
import org.piepmeyer.gauguin.grid.Grid

class Challenges {
    fun zenChallenge(): Grid {
        val calculatedDifficulties = this::class.java.getResource("/org/piepmeyer/gauguin/challenge/most-difficult-4x4.yml")!!.readText()

        val safedGrid = Json.decodeFromString<SavedGrid>(calculatedDifficulties)

        return safedGrid.toGrid()
    }

    fun chruncherChallenge(): Grid {
        val calculatedDifficulties = this::class.java.getResource("/org/piepmeyer/gauguin/challenge/most-difficult-5x5.yml")!!.readText()

        val safedGrid = Json.decodeFromString<SavedGrid>(calculatedDifficulties)

        return safedGrid.toGrid()
    }
}
