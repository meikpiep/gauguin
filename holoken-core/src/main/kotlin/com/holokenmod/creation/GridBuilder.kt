package com.holokenmod.creation

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridSize
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameOptionsVariant
import com.holokenmod.options.GameVariant

class GridBuilder @JvmOverloads constructor(
    width: Int,
    heigth: Int,
    variant: GameOptionsVariant = GameOptionsVariant.createClassic()
) {
    private val grid: Grid
    private var cageId = 0
    private val values = mutableListOf<Int>()

    constructor(size: Int) : this(size, size)
    constructor(size: Int, digitSetting: DigitSetting) : this(
        size,
        size,
        GameOptionsVariant.createClassic(digitSetting)
    )

    init {
        grid = Grid(GameVariant(GridSize(width, heigth), variant))
        grid.addAllCells()
    }

    fun addCage(result: Int, action: GridCageAction, vararg cellIds: Int): GridBuilder {
        if (cellIds.isEmpty()) {
            throw RuntimeException("No cell ids given.")
        }
        val cage = GridCage(cageId++, grid, action)
        cage.result = result
        for (cellId in cellIds) {
            cage.addCell(grid.getCell(cellId))
        }
        grid.addCage(cage)
        return this
    }

    fun addValueRow(vararg values: Int) {
        this.values += values.toList()
    }

    fun createGrid(): Grid {
        grid.setCageTexts()
        if (values.isNotEmpty()) {
            var cellId = 0
            values.forEach {
                grid.getCell(cellId).value = it
                cellId++
            }
        }
        return grid
    }
}
