package org.piepmeyer.gauguin.game

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.CurrentGameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

class SaveGame private constructor(private val filename: File) {
    fun save(grid: Grid) {
        try {
            BufferedWriter(FileWriter(filename)).use { writer ->
                writer.write("${System.currentTimeMillis()}\n")
                writer.write("${grid.gridSize}\n")
                writer.write("${grid.playTime.inWholeMilliseconds}\n")
                writer.write("${grid.isActive}\n")
                for (cell in grid.cells) {
                    writer.write("CELL:")
                    writer.write(cell.cellNumber.toString() + ":")
                    writer.write(cell.row.toString() + ":")
                    writer.write(cell.column.toString() + ":")
                    writer.write(cell.value.toString() + ":")
                    writer.write(cell.userValue.toString() + ":")
                    for (possible in cell.possibles) {
                        writer.write("$possible,")
                    }
                    writer.write("\n")
                }
                grid.selectedCell?.let {
                    writer.write("SELECTED:${it.cellNumber}\n")
                }
                val invalidchoices = grid.invalidsHighlighted()
                if (invalidchoices.isNotEmpty()) {
                    writer.write("INVALID:")
                    invalidchoices.forEach {
                        writer.write("${it.cellNumber},")
                    }
                    writer.write("\n")
                }
                val cheatedcells = grid.cheatedHighlighted()
                if (cheatedcells.isNotEmpty()) {
                    writer.write("CHEATED:")
                    cheatedcells.forEach {
                        writer.write("${it.cellNumber},")
                    }
                    writer.write("\n")
                }
                grid.cages.forEach {
                    writer.write("CAGE:${it.id}:${it.action.name}:${it.cageType.name}:${it.result}:${it.cellNumbers}\n")
                }
            }
        } catch (e: IOException) {
            logger.error { "Error saving game: " + e.message }
            return
        }
        logger.debug { "Saved game." }
    }

    fun readDate(): Long {
        try {
            FileInputStream(filename).use { ins ->
                BufferedReader(
                    InputStreamReader(ins),
                    8192
                ).use { br -> return br.readLine().toLong() }
            }
        } catch (e: NumberFormatException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0
    }

    fun restore(): Grid? {
        return if (filename.length() == 0L) {
            null
        } else {
            logger.info { "test " + filename.absolutePath + " - " + filename.length() }
            logger.info { "savefile " + filename.readText() }

            try {
                FileInputStream(filename).use { ins ->

                    BufferedReader(InputStreamReader(ins), 8192).use { br ->
                        val creationDate = br.readLine().toLong()
                        val gridSizeString = br.readLine()
                        val gridSize: GridSize = GridSize.create(gridSizeString)
                        val playTime = br.readLine().toLong()

                        // TODO: Load and Save correct GameOptionsVariant
                        val variant = GameVariant(
                            gridSize,
                            CurrentGameOptionsVariant.instance
                        )
                        val grid = Grid(variant, creationDate)
                        grid.isActive = br.readLine() == "true"
                        grid.playTime = playTime.milliseconds

                        var line = readCells(br, grid)

                        if (line.startsWith("SELECTED:")) {
                            val selected = line.split(":").dropLastWhile { it.isEmpty() }
                                .toTypedArray()[1].toInt()
                            grid.selectedCell = grid.getCell(selected)
                            grid.getCell(selected).isSelected = true
                            line = br.readLine()
                        }
                        if (line.startsWith("INVALID:")) {
                            val invalidlist = line.split(":").dropLastWhile { it.isEmpty() }
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
                            val cheatedlist = line.split(":").dropLastWhile { it.isEmpty() }
                                .toTypedArray()[1]
                            for (cellId in cheatedlist.split(",").dropLastWhile { it.isEmpty() }
                                .toTypedArray()) {
                                val cellNum = cellId.toInt()
                                val c = grid.getCell(cellNum)
                                c.isCheated = true
                            }
                            line = br.readLine()
                        }

                        var rawLine: String? = line

                        do {
                            val currentLine = rawLine as String

                            val cageParts = currentLine.split(":").dropLastWhile { it.isEmpty() }.toTypedArray()
                            val cage = GridCage(
                                cageParts[1].toInt(),
                                grid,
                                GridCageAction.valueOf(cageParts[2]),
                                GridCageType.valueOf(cageParts[3])
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

                        grid.setCageTexts()

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

    private fun readCells(
        br: BufferedReader,
        grid: Grid
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

    companion object {
        const val SAVEGAME_AUTO_NAME = "autosave"
        const val SAVEGAME_NAME_PREFIX = "savegame_"

        @JvmStatic
        fun autosaveByDirectory(directory: File): SaveGame {
            return SaveGame(getAutosave(directory))
        }

        @JvmStatic
        fun createWithFile(filename: File): SaveGame {
            return SaveGame(filename)
        }

        private fun getAutosave(directory: File): File {
            return File(directory, SAVEGAME_AUTO_NAME)
        }
    }
}
