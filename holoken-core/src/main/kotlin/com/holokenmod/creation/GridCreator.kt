package com.holokenmod.creation

import com.holokenmod.RandomSingleton
import com.holokenmod.Randomizer
import com.holokenmod.creation.cage.GridCageCreator
import com.holokenmod.grid.Grid
import com.holokenmod.options.DifficultySetting
import com.holokenmod.options.GameVariant

class GridCreator(
    private val variant: GameVariant,
    private val randomizer: Randomizer = RandomSingleton.instance,
    private val shuffler: PossibleDigitsShuffler = RandomPossibleDigitsShuffler()
) {
    fun createRandomizedGridWithCages(): Grid {
        randomizer.discard()
        var newGrid: Grid
        do {
            newGrid = Grid(variant)
            newGrid.addAllCells()
            randomiseGrid(newGrid)
            createCages(newGrid)
        } while (!isWantedDifficulty(newGrid))

        return newGrid
    }

    private fun isWantedDifficulty(grid: Grid): Boolean {
        if (variant.options.difficultySetting == DifficultySetting.ANY) {
            return true
        }
        val calculator = GridDifficultyCalculator(grid)
        return if (!calculator.isGridVariantSupported) {
            true
        } else {
            calculator.difficulty == variant.options.difficultySetting.gameDifficulty
        }
    }

    private fun createCages(grid: Grid) {
        val creator = GridCageCreator(randomizer, grid)
        creator.createCages()
    }

    private fun randomiseGrid(grid: Grid) {
        val randomizer = GridRandomizer(shuffler, grid)
        randomizer.createGrid()
    }
}
