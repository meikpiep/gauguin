package org.piepmeyer.gauguin.game.save

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.piepmeyer.gauguin.game.save.v1.V1SavedGrid
import org.piepmeyer.gauguin.game.save.v2.V2SavedGrid
import org.piepmeyer.gauguin.grid.Grid
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.time.measureTime

private val logger = KotlinLogging.logger {}

class SaveGame private constructor(
    private val file: File,
) {
    @OptIn(ExperimentalSerializationApi::class)
    fun save(grid: Grid) {
        try {
            val duration =
                measureTime {
                    val savedGrid = SavedGrid.fromGrid(grid)

                    file.outputStream().use {
                        Json.encodeToStream(savedGrid, it)
                    }
                }

            logger.debug { "Saved grid in $duration." }
        } catch (e: Exception) {
            logger.error { "Error saving game: " + e.message }
            return
        }
    }

    fun restore(): Grid? {
        if (file.length() == 0L) {
            return null
        }

        val saveGame = createWithFile(file)

        return saveGame.loadAndMigrateIfNecessary()
    }

    fun migrateOldSavedGridVersion() {
        if (file.length() == 0L) {
            return
        }

        logger.info { "Checking ${file.name} if migration is needed..." }

        val fileData = file.readText(StandardCharsets.UTF_8)

        val gridVersion =
            enrichDecodingException(fileData) {
                jsonIgnoringUnknownKeys.decodeFromString<SavedGridVersion>(fileData)
            }

        if (isCurrentGridVersion(gridVersion)) {
            logger.info { "No migration needed." }
            return
        }

        val migratedGrid = loadAndMigrate(gridVersion, fileData)

        save(migratedGrid)
        logger.info { "Finished migration of '${file.name}" }
    }

    private fun isCurrentGridVersion(gridVersion: SavedGridVersion): Boolean = gridVersion.version == 3

    private fun loadAndMigrateIfNecessary(): Grid? {
        if (file.length() == 0L) {
            return null
        }

        logger.info { "Checking ${file.name} if migration is needed..." }

        val fileData = file.readText(StandardCharsets.UTF_8)

        val gridVersion =
            enrichDecodingException(fileData) {
                jsonIgnoringUnknownKeys.decodeFromString<SavedGridVersion>(fileData)
            }

        if (isCurrentGridVersion(gridVersion)) {
            logger.info { "No migration needed, loading file '${file.name}'." }

            return loadCurrentGridVersion(fileData)
        }

        return loadAndMigrate(gridVersion, fileData)
    }

    private fun loadAndMigrate(
        gridVersion: SavedGridVersion,
        fileData: String,
    ): Grid {
        when (gridVersion.version) {
            1 -> {
                logger.info { "Migrating from version 1..." }
                val savedGrid =
                    enrichDecodingException(fileData) {
                        Json.decodeFromString<V1SavedGrid>(fileData)
                    }

                logger.info { "Finished migration while loading file '${file.name}'" }
                return savedGrid.toGrid()
            }

            2 -> {
                logger.info { "Migrating from version 2..." }
                val savedGrid =
                    enrichDecodingException(fileData) {
                        Json.decodeFromString<V2SavedGrid>(fileData)
                    }

                logger.info { "Finished migration while loading file '${file.name}'" }
                return savedGrid.toGrid()
            }

            else -> {
                error("Unknown version '${gridVersion.version}' of file '${file.name}'")
            }
        }
    }

    private fun loadCurrentGridVersion(fileData: String): Grid {
        val savedGrid =
            enrichDecodingException(fileData) {
                Json.decodeFromString<SavedGrid>(fileData)
            }

        return savedGrid.toGrid()
    }

    private fun <T : Any> enrichDecodingException(
        fileData: String,
        function: () -> T,
    ): T {
        try {
            return function.invoke()
        } catch (e: SerializationException) {
            throw SerializationException(
                "Error decoding version grid info with length " +
                    "${file.length()} and first bytes: '${fileData.take(50)}'.",
                e,
            )
        }
    }

    companion object {
        const val SAVEGAME_AUTO_NAME = "autosave.yml"
        const val SAVEGAME_NAME_PREFIX = "game_"
        const val SAVEGAME_NAME_SUFFIX = ".yml"

        private val jsonIgnoringUnknownKeys: Json by lazy { Json { ignoreUnknownKeys = true } }

        fun autosaveByDirectory(directory: File): SaveGame = SaveGame(autosaveFile(directory))

        fun createWithFile(filename: File): SaveGame = SaveGame(filename)

        fun autosaveFile(directory: File): File = File(directory, SAVEGAME_AUTO_NAME)
    }
}
