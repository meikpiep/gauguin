package org.piepmeyer.gauguin.game.save

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.InjectedParam
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.GridDifficultyCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

class MigrateOldSavedGamesService(
    private val filesDir: File,
    @InjectedParam private val applicationPreferences: ApplicationPreferences,
) {
    private fun restore(saveGameFile: File): Grid? {
        return if (saveGameFile.length() == 0L) {
            null
        } else {
            logger.info { "test " + saveGameFile.absolutePath + " - " + saveGameFile.length() }
            logger.info { "savefile " + saveGameFile.readText() }

            try {
                FileInputStream(saveGameFile).use { ins ->

                    BufferedReader(InputStreamReader(ins), 8192).use { br ->
                        val creationDate = br.readLine().toLong()
                        val gridSizeString = br.readLine()
                        val gridSize: GridSize = parseGridSize(gridSizeString)
                        val playTime = br.readLine().toLong()

                        val variant =
                            GameVariant(
                                gridSize,
                                applicationPreferences.gameVariant,
                            )
                        val grid = Grid(variant, creationDate)
                        grid.isActive = br.readLine() == "true"
                        grid.playTime = playTime.milliseconds

                        var line = readCells(br, grid)

                        if (line.startsWith("SELECTED:")) {
                            val selected =
                                line.split(":").dropLastWhile { it.isEmpty() }
                                    .toTypedArray()[1].toInt()
                            grid.selectedCell = grid.getCell(selected)
                            grid.getCell(selected).isSelected = true
                            line = br.readLine()
                        }
                        if (line.startsWith("INVALID:")) {
                            val invalidlist =
                                line.split(":").dropLastWhile { it.isEmpty() }
                                    .toTypedArray()[1]
                            for (cellId in invalidlist.split(",").dropLastWhile { it.isEmpty() }
                                .toTypedArray()) {
                                val cellNum = cellId.toInt()
                                val c = grid.getCell(cellNum)
                                c.isSelected = true
                            }
                            line = br.readLine()
                        }
                        if (line.startsWith("CHEATED")) {
                            val cheatedlist =
                                line.split(":").dropLastWhile { it.isEmpty() }
                                    .toTypedArray()[1]
                            for (cellId in cheatedlist.split(",").dropLastWhile { it.isEmpty() }
                                .toTypedArray()) {
                                val cellNum = cellId.toInt()
                                val c = grid.getCell(cellNum)
                                c.isCheated = true
                            }
                            line = br.readLine()
                        }

                        readCages(line, grid, br)

                        /*
                         * Heuristic: If no value and no possible is filled, the grid has not been
                         * played yet.
                         */
                        grid.startedToBePlayed =
                            grid.cells.any {
                                it.possibles.isNotEmpty() || it.isUserValueSet
                            }

                        return grid
                    }
                }
            } catch (e: IOException) {
                logger.info(e) { e.message }
                return null
            } catch (e: Exception) {
                logger.error(e) { e.message }
                return null
            }
        }
    }

    private fun parseGridSize(gridSizeString: String): GridSize {
        return try {
            val size = gridSizeString.toInt()
            GridSize(size, size)
        } catch (e: NumberFormatException) {
            val parts = gridSizeString.split("x")
            val width = parts[0].toInt()
            val height = parts[1].toInt()
            GridSize(width, height)
        }
    }

    private fun readCages(
        line: String,
        grid: Grid,
        br: BufferedReader,
    ) {
        var rawLine: String? = line

        do {
            val currentLine = rawLine as String

            val cageParts = currentLine.split(":").dropLastWhile { it.isEmpty() }.toTypedArray()
            val cage =
                GridCage(
                    cageParts[1].toInt(),
                    grid,
                    GridCageAction.valueOf(cageParts[2]),
                    GridCageType.valueOf(cageParts[3]),
                )
            cage.result = cageParts[4].toInt()
            for (cellId in cageParts[5].split(",").dropLastWhile { it.isEmpty() }
                .toTypedArray()) {
                val cellNum = cellId.toInt()
                val c = grid.getCell(cellNum)
                c.cage = cage
                cage.addCell(c)
            }
            grid.cages = grid.cages + cage
        } while (br.readLine().also { rawLine = it } != null)
    }

    private fun readCells(
        br: BufferedReader,
        grid: Grid,
    ): String {
        var rawLine: String?

        while (br.readLine().also { rawLine = it } != null) {
            val line = rawLine as String

            if (!line.startsWith("CELL:")) {
                return line
            }
            val cellParts = line.split(":").dropLastWhile { it.isEmpty() }.toTypedArray()
            val cellNum = cellParts[1].toInt()
            val cell = grid.getCell(cellNum)
            cell.value = cellParts[4].toInt()
            cell.userValue = cellParts[5].toInt()
            if (cellParts.size == 7) {
                for (possible in cellParts[6].split(",")
                    .dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    cell.addPossible(possible.toInt())
                }
            }
        }

        return ""
    }

    fun migrateFiles() {
        val oldAutosaveFile = File(filesDir, OLD_SAVEGAME_AUTO_NAME)

        if (oldAutosaveFile.exists()) {
            migrateFile(oldAutosaveFile, File(filesDir, SaveGame.SAVEGAME_AUTO_NAME))
        }

        filesDir.listFiles { _: File?, name: String -> name.startsWith(OLD_SAVEGAME_NAME_PREFIX) }
            ?.forEach {
                val numberFromOldFileName = it.name.substringAfter("_")

                val newFile = File(filesDir, SaveGame.SAVEGAME_NAME_PREFIX + numberFromOldFileName + SaveGame.SAVEGAME_NAME_SUFFIX)

                migrateFile(it, newFile)
            }
    }

    private fun migrateFile(
        oldXmlFile: File,
        newYamlFile: File,
    ) {
        restore(oldXmlFile)?.let { grid ->
            if (GridDifficultyCalculator(grid).calculate() != Double.NEGATIVE_INFINITY) {
                val newSaveGame = SaveGame.createWithFile(newYamlFile)

                newSaveGame.save(grid)
            }
        }

        if (!oldXmlFile.delete()) {
            logger.warn { "Old saved game could not be deleted: ${oldXmlFile.name}" }
        }
    }

    companion object {
        private const val OLD_SAVEGAME_AUTO_NAME = "autosave"
        private const val OLD_SAVEGAME_NAME_PREFIX = "savegame_"
    }
}
