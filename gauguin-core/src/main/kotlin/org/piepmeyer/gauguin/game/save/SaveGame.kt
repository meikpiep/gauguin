package org.piepmeyer.gauguin.game.save

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.grid.Grid
import java.io.File
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}

class SaveGame private constructor(
    private val file: File,
) {
    fun save(grid: Grid) {
        try {
            val savedGrid = SavedGrid.fromGrid(grid)

            val result = Json.encodeToString(savedGrid)

            file.writeText(result)
        } catch (e: Exception) {
            logger.error { "Error saving game: " + e.message }
            return
        }
        logger.debug { "Saved game: ${file.name}" }
    }

    fun restore(): Grid? {
        if (file.length() == 0L) {
            return null
        }

        val fileData = file.readText(StandardCharsets.UTF_8)

        try {
            return restore(fileData)
        } catch (e: SerializationException) {
            throw SerializationException(
                "Error decoding grid with length " +
                    "${file.length()} and first bytes: '${fileData.substring(0, 50)}'.",
                e,
            )
        }
    }

    companion object {
        const val SAVEGAME_AUTO_NAME = "autosave.yml"
        const val SAVEGAME_NAME_PREFIX = "game_"
        const val SAVEGAME_NAME_SUFFIX = ".yml"

        fun autosaveByDirectory(directory: File): SaveGame = SaveGame(getAutosave(directory))

        fun createWithFile(filename: File): SaveGame = SaveGame(filename)

        private fun getAutosave(directory: File): File = File(directory, SAVEGAME_AUTO_NAME)

        fun restore(fileData: String): Grid {
            val savedGrid = Json.decodeFromString<SavedGrid>(fileData)

            return savedGrid.toGrid()
        }
    }
}
