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
        val saver = SaveGame.autosaveByDirectory(saveGameDirectory)

        saver.save(game.grid)

        var filename: File
        var fileIndex = 0
        while (true) {
            filename = File(saveGameDirectory, SaveGame.SAVEGAME_NAME_PREFIX + fileIndex + SaveGame.SAVEGAME_NAME_SUFFIX)
            if (!filename.exists()) {
                break
            }
            fileIndex++
        }
        try {
            copy(File(saveGameDirectory, SaveGame.SAVEGAME_AUTO_NAME), filename)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            logger.error(e) { "Error while saving a grid: ${e.message}" }
        }

        savedGamesService.informSavedGamesChanged()
    }

    @Throws(IOException::class)
    fun copy(
        src: File,
        dst: File,
    ) {
        src.copyTo(dst, true)
    }
}
