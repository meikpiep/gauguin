package com.holokenmod.creation

import com.holokenmod.creation.cage.GridCageType
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridSize
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameOptionsVariant
import com.holokenmod.options.GameVariant

class GridBuilder constructor(
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

    fun addCage(result: Int, action: GridCageAction, cageType: GridCageType, firstCellId: Int): GridBuilder {
        val firstCell = grid.getCell(firstCellId)

        val cage = GridCage.createWithCells(cageId++, grid, action, firstCell, cageType)
        cage.result = result

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
