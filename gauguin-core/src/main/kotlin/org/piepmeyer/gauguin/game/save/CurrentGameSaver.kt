package org.piepmeyer.gauguin.game.save

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.InjectedParam
import org.piepmeyer.gauguin.game.Game
import java.io.File
import java.io.IOException

private val logger = KotlinLogging.logger {}

class CurrentGameSaver(
    private val saveGameDirectory: File,
    @InjectedParam private val game: Game,
    @InjectedParam private val savedGamesService: SavedGamesService,
) {
    fun save() {
        saveWithComment(null)
    }

    fun saveWithComment(comment: String?) {
        val saver = SaveGame.autosaveByDirectory(saveGameDirectory)

        game.grid.description = comment
        saver.save(game.grid)

        val existingSaveGameNames =
            saveGameDirectory
                .listFiles { _, filename ->
                    filename.startsWith(SaveGame.SAVEGAME_NAME_PREFIX) &&
                        filename.endsWith(
                            SaveGame.SAVEGAME_NAME_SUFFIX,
                        )
                }?.map { it.name } ?: emptyList()

        var filePrefix: String
        var fileIndex = 0

        while (true) {
            filePrefix = SaveGame.SAVEGAME_NAME_PREFIX + fileIndex

            if (existingSaveGameNames.none { it.startsWith(filePrefix) }) {
                break
            }
            fileIndex++
        }

        try {
            val filename = File(saveGameDirectory, filePrefix + SaveGame.SAVEGAME_NAME_SUFFIX)

            val source = File(saveGameDirectory, SaveGame.SAVEGAME_AUTO_NAME)
            source.copyTo(filename, true)
        } catch (e: IOException) {
            logger.error(e) { "Error while saving a grid: ${e.message}" }
        }

        savedGamesService.informSavedGamesChanged()
    }
}
