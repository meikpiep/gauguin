package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

class GridBuilder(
    width: Int,
    heigth: Int,
    variant: GameOptionsVariant = GameOptionsVariant.createClassic(),
) {
    private var cages = mutableListOf<GridCage>()
    private val grid = Grid(GameVariant(GridSize(width, heigth), variant))
    private var cageId = 0
    private val values = mutableListOf<Int>()
    private val cageToPossibles = mutableMapOf<GridCage, Set<IntArray>>()

    constructor(size: Int) : this(size, size)
    constructor(size: Int, digitSetting: DigitSetting) : this(
        size,
        size,
        GameOptionsVariant.createClassic(digitSetting),
    )

    constructor(width: Int, height: Int, digitSetting: DigitSetting) : this(
        width,
        height,
        GameOptionsVariant.createClassic(digitSetting),
    )

    fun addSingleCage(
        result: Int,
        cellId: Int,
        possibleCombinations: Set<IntArray> = emptySet(),
    ): GridBuilder = addCage(result, GridCageAction.ACTION_NONE, GridCageType.SINGLE, cellId, possibleCombinations)

    fun addCage(
        result: Int,
        action: GridCageAction,
        cageType: GridCageType,
        firstCellId: Int,
        possibleCombinations: Set<IntArray> = emptySet(),
    ): GridBuilder {
        val firstCell = grid.getCell(firstCellId)

        val cage = GridCage.createWithCells(cageId++, grid, action, firstCell, cageType)
        cage.result = result

        cages += cage

        cageToPossibles[cage] = possibleCombinations

        return this
    }

    fun addValueRow(vararg values: Int): GridBuilder {
        this.values += values.toList()

        return this
    }

    fun createGrid(): Grid {
        cages.forEach { grid.addCage(it) }

        if (values.isNotEmpty()) {
            var cellId = 0
            values.forEach {
                grid.getCell(cellId).value = it
                cellId++
            }
        }

        if (cageToPossibles.isNotEmpty()) {
            cageToPossibles.forEach { cage, possibleCombinations ->
                possibleCombinations.forEach { combination ->
                    combination.forEachIndexed { index, possible ->
                        cage.getCell(index).addPossible(possible)
                    }
                }
            }

            grid.cells.forEach { cell ->
                val sortedPossibles = cell.possibles.sorted()

                cell.clearPossibles()

                sortedPossibles.forEach { cell.addPossible(it) }
            }
        }

        return grid
    }

    fun createGridAndCageToPossibles(): Pair<Grid, Map<GridCage, Set<IntArray>>> =
        Pair(
            createGrid(),
            cageToPossibles,
        )
}
