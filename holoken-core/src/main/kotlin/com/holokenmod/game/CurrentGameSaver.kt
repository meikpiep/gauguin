package com.holokenmod.game

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

class CurrentGameSaver(private val saveGameDirectory: File) {
    fun save() {
        var filename: File
        var fileIndex = 0
        while (true) {
            filename = File(saveGameDirectory, SaveGame.SAVEGAME_NAME_PREFIX_ + fileIndex)
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
    }

    @Throws(IOException::class)
    fun copy(src: File, dst: File) {
        FileUtils.copyFile(src, dst)
    }
}
