package org.piepmeyer.gauguin.game

import org.koin.core.annotation.InjectedParam
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.StatisticsManagerWriting
import kotlin.time.Duration.Companion.seconds

class GameSolveService(
    @InjectedParam private val game: Game,
    @InjectedParam private val statisticsManager: StatisticsManagerWriting,
) {
    fun revealSelectedCage() {
        val selectedCell = game.grid.selectedCell ?: return

        selectedCell.cage().cells.forEach { cageCell ->
            game.revealCell(cageCell)
        }

        cheatedOnGame()
    }

    fun solveGrid() {
        game.grid.cells.forEach { game.revealCell(it) }

        game.grid.selectedCell?.isSelected = false
        game.grid.selectedCell = null

        cheatedOnGame()
    }

    fun revealSelectedCell() {
        game.grid.selectedCell ?: return

        game.revealCell(game.grid.selectedCell!!)

        cheatedOnGame()
    }

    fun markInvalidChoices() {
        game.markInvalidChoices()

        cheatedOnGame()
    }

    private fun cheatedOnGame() {
        statisticsManager.storeStreak(false)
    }

    fun simulateThousandGames() {
        for (i in 1..1_000) {
            val gridSize = listOf(6, 7, 8, 9).random()
            val gridSolved = Math.random() >= 0.25 // let 75% grids be solved
            val playTime = 20.rangeTo(3_600).random().seconds // from 20 seconds to 1 hour
            val classicDifficulty = 15.rangeTo(90).random().toDouble()
            val humanDifficulty = 120.rangeTo(6_000).random()

            val gridVariant = GameVariant(GridSize(gridSize, gridSize), GameOptionsVariant.createClassic())

            val grid = Grid(gridVariant)
            grid.playTime = playTime
            grid.difficulty = grid.difficulty.copy(classicalRating = classicDifficulty, humanDifficulty = humanDifficulty)

            statisticsManager.puzzleStartedToBePlayed()

            statisticsManager.storeStreak(gridSolved)

            statisticsManager.puzzleSolved(grid)
        }
    }
}
