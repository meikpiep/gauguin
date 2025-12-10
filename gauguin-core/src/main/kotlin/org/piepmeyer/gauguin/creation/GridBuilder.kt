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

    fun addCageSingle(
        result: Int,
        possibleCombinations: Set<IntArray> = emptySet(),
    ): GridBuilder = addCage(result, GridCageAction.ACTION_NONE, GridCageType.SINGLE, possibleCombinations)

    fun addCageAdd(
        result: Int,
        cageType: GridCageType,
        possibleCombinations: Set<IntArray> = emptySet(),
    ): GridBuilder = addCage(result, GridCageAction.ACTION_ADD, cageType, possibleCombinations)

    fun addCageSubtract(
        result: Int,
        cageType: GridCageType,
        possibleCombinations: Set<IntArray> = emptySet(),
    ): GridBuilder = addCage(result, GridCageAction.ACTION_SUBTRACT, cageType, possibleCombinations)

    fun addCageMultiply(
        result: Int,
        cageType: GridCageType,
        possibleCombinations: Set<IntArray> = emptySet(),
    ): GridBuilder = addCage(result, GridCageAction.ACTION_MULTIPLY, cageType, possibleCombinations)

    fun addCageDivide(
        result: Int,
        cageType: GridCageType,
        possibleCombinations: Set<IntArray> = emptySet(),
    ): GridBuilder = addCage(result, GridCageAction.ACTION_DIVIDE, cageType, possibleCombinations)

    fun addCage(
        result: Int,
        action: GridCageAction,
        cageType: GridCageType,
        possibleCombinations: Set<IntArray> = emptySet(),
    ): GridBuilder {
        val firstCellWithoutCage = grid.cells.first { it.cage == null }

        val cage = GridCage.createWithCells(cageId++, grid, action, firstCellWithoutCage, cageType)
        cage.result = result

        grid.addCage(cage)

        cageToPossibles[cage] = possibleCombinations

        return this
    }

    fun addValueRow(vararg values: Int): GridBuilder {
        this.values += values.toList()

        return this
    }

    fun createGrid(): Grid {
        if (values.isNotEmpty()) {
            var cellId = 0
            values.forEach {
                grid.getCell(cellId).value = it
                cellId++
            }
        }

        if (cageToPossibles.isNotEmpty()) {
            cageToPossibles.forEach { (cage, possibleCombinations) ->
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
