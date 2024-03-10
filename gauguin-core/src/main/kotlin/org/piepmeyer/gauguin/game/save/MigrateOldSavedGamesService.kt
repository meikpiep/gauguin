package org.piepmeyer.gauguin.game.save

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

class MigrateOldSavedGamesService(
    private val filesDir: File,
) {
    fun deleteOldSaveGameFiles() {
        val oldAutosaveFile = File(filesDir, OLD_SAVEGAME_AUTO_NAME)

        if (oldAutosaveFile.exists()) {
            deleteSaveGame(oldAutosaveFile)
        }

        filesDir.listFiles { _: File?, name: String -> name.startsWith(OLD_SAVEGAME_NAME_PREFIX) }
            ?.forEach {
                deleteSaveGame(it)
            }
    }

    private fun deleteSaveGame(file: File) {
        if (!file.delete()) {
            logger.warn { "Could not delete legacy save game $file" }
        }
    }

    companion object {
        private const val OLD_SAVEGAME_AUTO_NAME = "autosave"
        private const val OLD_SAVEGAME_NAME_PREFIX = "savegame_"
    }
}
