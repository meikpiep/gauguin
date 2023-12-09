package org.piepmeyer.gauguin.game

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.IOException

class CurrentGameSaver(
    private val saveGameDirectory: File,
) : KoinComponent {
    private val game: Game by inject()
    private val savedGamesService: SavedGamesService by inject()

    fun save() {
        val saver = SaveGame.autosaveByDirectory(saveGameDirectory)

        saver.save(game.grid)

        var filename: File
        var fileIndex = 0
        while (true) {
            filename = File(saveGameDirectory, SaveGame.SAVEGAME_NAME_PREFIX + fileIndex)
            if (!filename.exists()) {
                break
            }
            fileIndex++
        }
        try {
            copy(File(saveGameDirectory, SaveGame.SAVEGAME_AUTO_NAME), filename)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
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
