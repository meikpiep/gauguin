package com.holokenmod.game

import com.holokenmod.creation.cage.GridCageType
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridCell
import com.holokenmod.grid.GridSize
import com.holokenmod.options.CurrentGameOptionsVariant
import com.holokenmod.options.GameVariant
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

private val logger = KotlinLogging.logger {}

class SaveGame private constructor(private val filename: File) {
    fun save(grid: Grid) {
        try {
            BufferedWriter(FileWriter(filename)).use { writer ->
                val now = System.currentTimeMillis()
                writer.write(
                    """
    $now
    
                    """.trimIndent()
                )
                writer.write(
                    """
    ${grid.gridSize}
    
                    """.trimIndent()
                )
                writer.write(
                    """
    ${grid.playTime}
    
                    """.trimIndent()
                )
                writer.write(
                    """
    ${grid.isActive}
    
                    """.trimIndent()
                )
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
                    writer.write(
                        "SELECTED:" + it.cellNumber + "\n"
                    )
                }
                val invalidchoices = grid.invalidsHighlighted()
                if (invalidchoices.isNotEmpty()) {
                    writer.write("INVALID:")
                    invalidchoices.forEach {
                        writer.write(it.cellNumber.toString() + ",")
                    }
                    writer.write("\n")
                }
                val cheatedcells = grid.cheatedHighlighted()
                if (cheatedcells.isNotEmpty()) {
                    writer.write("CHEATED:")
                    cheatedcells.forEach {
                        writer.write(it.cellNumber.toString() + ",")
                    }
                    writer.write("\n")
                }
                grid.cages.forEach {
                    writer.write("CAGE:")
                    writer.write(it.id.toString() + ":")
                    writer.write(it.action.name + ":")
                    writer.write(it.cageType.name + ":")
                    writer.write(it.result.toString() + ":")
                    writer.write(it.cellNumbers)
                    // writer.write(":" + cage.isOperatorHidden());
                    writer.write("\n")
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
        var br: BufferedReader? = null
        var ins: InputStream? = null
        var cageParts: Array<String>
        return if (filename.length() == 0L) {
            null
        } else {
            try {
                logger.info { "test " + filename.absolutePath + " - " + filename.length() }
                logger.info { "savefile " + filename.readText() }
                ins = FileInputStream(filename)
                br = BufferedReader(InputStreamReader(ins), 8192)
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
                grid.playTime = playTime

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

                    cageParts = currentLine.split(":").dropLastWhile { it.isEmpty() }.toTypedArray()
                    val cage = GridCage(cageParts[1].toInt(), grid, GridCageAction.valueOf(cageParts[2]), GridCageType.valueOf(cageParts[3]))
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
            } catch (e: IOException) {
                logger.info(e.message, e)
                return null
            } catch (e: Exception) {
                logger.error(e.message, e)
                return null
            } finally {
                try {
                    ins?.close()
                    br?.close()
                } catch (ignored: Exception) {
                }
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
            val row = cellParts[2].toInt()
            val column = cellParts[3].toInt()
            val cell = GridCell(grid, cellNum, row, column)
            cell.value = cellParts[4].toInt()
            cell.userValue = cellParts[5].toInt()
            if (cellParts.size == 7) {
                for (possible in cellParts[6].split(",")
                    .dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    cell.addPossible(possible.toInt())
                }
            }
            grid.addCell(cell)
        }

        return ""
    }

    companion object {
        const val SAVEGAME_AUTO_NAME = "autosave"
        const val SAVEGAME_NAME_PREFIX_ = "savegame_"

        @JvmStatic
        fun createWithDirectory(directory: File): SaveGame {
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
