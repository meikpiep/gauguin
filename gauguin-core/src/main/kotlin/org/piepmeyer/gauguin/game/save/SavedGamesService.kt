package org.piepmeyer.gauguin.game.save

import org.koin.core.component.KoinComponent
import java.io.File

class SavedGamesService(
    private val filesDir: File,
) : KoinComponent {
    private var listeners = mutableListOf<SavedGamesListener>()

    fun savedGameFiles(): List<File> =
        filesDir
            .listFiles { _: File?, name: String ->
                name.startsWith(SaveGame.SAVEGAME_NAME_PREFIX) &&
                    name.endsWith(SaveGame.SAVEGAME_NAME_SUFFIX)
            }?.toList()
            ?.filterNotNull() ?: emptyList()

    fun countOfSavedGames(): Int = savedGameFiles().count()

    fun informSavedGamesChanged() {
        listeners.forEach { it.savedGamesChanged() }
    }

    fun addSavedGamesListener(listener: SavedGamesListener) {
        listeners += listener
    }

    companion object {
        fun migrateOldSavedGameFilesBeforeKoinStartup(filesDir: File) {
            val service = SavedGamesService(filesDir)

            val savedGames = service.savedGameFiles().toMutableList()

            if (SaveGame.autosaveFile(filesDir).exists()) {
                savedGames.add(SaveGame.autosaveFile(filesDir))
            }

            savedGames.forEach {
                val saveGame = SaveGame.createWithFile(it)
                saveGame.migrateOldSavedGridVersion()
            }
        }
    }
}
