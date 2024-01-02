package org.piepmeyer.gauguin.game.save

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.grid.Grid
import java.io.File
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}

class SaveGame private constructor(private val filename: File) {
    fun save(grid: Grid) {
        try {
            val savedGrid = SavedGrid.fromGrid(grid)

            val result = Json.encodeToString(savedGrid)

            filename.writeText(result)
        } catch (e: Exception) {
            logger.error { "Error saving game: " + e.message }
            return
        }
        logger.debug { "Saved game." }
    }

    fun restore(): Grid? {
        if (filename.length() == 0L) {
            return null
        }

        val fileData = filename.readText(StandardCharsets.UTF_8)

        val savedGrid = Json.decodeFromString<SavedGrid>(fileData)

        return savedGrid.toGrid()
    }

    companion object {
        const val SAVEGAME_AUTO_NAME = "autosave.yml"
        const val SAVEGAME_NAME_PREFIX = "game_"
        const val SAVEGAME_NAME_SUFFIX = ".yml"

        fun autosaveByDirectory(directory: File): SaveGame {
            return SaveGame(getAutosave(directory))
        }

        fun createWithFile(filename: File): SaveGame {
            return SaveGame(filename)
        }

        private fun getAutosave(directory: File): File {
            return File(directory, SAVEGAME_AUTO_NAME)
        }
    }
}
