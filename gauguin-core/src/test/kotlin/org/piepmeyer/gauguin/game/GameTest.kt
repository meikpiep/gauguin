package org.piepmeyer.gauguin.game

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManager

class GameTest :
    FunSpec(
        {
            test("restart game clears all values and all possible values") {
                val preferences =
                    mockk<ApplicationPreferences> {
                        every { showDupedDigits() } returns true
                    }

                val game = gameWithSmallGrid(preferences)

                game.restartGame()

                game.grid.cells.forEach { it.userValue shouldBe GridCell.NO_VALUE_SET }
                game.grid.cells.forEach { it.possibles.shouldBeEmpty() }

                stopKoin()
            }

            test("restart game clears last modified flags of all cells") {
                startKoin { }

                val preferences =
                    mockk<ApplicationPreferences> {
                        every { showDupedDigits() } returns true
                    }

                val game = gameWithSmallGrid(preferences)

                game.restartGame()

                game.grid.cells.forEach { it.isLastModified shouldBe false }
            }

            test("revealing a cell sets the correct user value and clears possible numbers") {
                val preferences =
                    mockk<ApplicationPreferences> {
                        every { showDupedDigits() } returns true
                        every { removePencils() } returns true
                    }
                val statisticsManager =
                    mockk<StatisticsManager> {
                        every { puzzleStartedToBePlayed() } just runs
                        every { storeStreak(false) } just runs
                    }

                val smallGrid =
                    GridBuilder(2)
                        .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
                        .addSingleCage(2, 3)
                        .createGrid()
                smallGrid.isActive = true

                val cellToReveal = smallGrid.cells[0]

                cellToReveal.addPossible(1)
                cellToReveal.addPossible(2)
                cellToReveal.value = 2
                cellToReveal.userValue = 1
                smallGrid.selectedCell = cellToReveal

                val game =
                    Game(
                        initalGrid = smallGrid,
                        mockk(relaxed = true),
                        statisticsManager,
                        preferences,
                    )

                game.revealCell(cellToReveal)

                cellToReveal.possibles.shouldBeEmpty()
                cellToReveal.userValue shouldBe 2
            }

            test("fillSingleCagesInNewGrid fills userValues of single cages") {
                val preferences =
                    mockk<ApplicationPreferences> {
                        every { removePencils() } returns false
                    }

                val grid =
                    GridBuilder(3)
                        .addCage(1, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 0)
                        .addSingleCage(2, 3)
                        .addSingleCage(3, 4)
                        .addSingleCage(4, 5)
                        .addCage(1, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 6)
                        .createGrid()

                grid.getCell(3).value = 2
                grid.getCell(4).value = 3
                grid.getCell(5).value = 4

                val game =
                    Game(
                        initalGrid = grid,
                        mockk(relaxed = true),
                        mockk(relaxed = true),
                        preferences,
                    )

                game.fillSingleCagesInNewGrid()

                grid.getCell(0).userValue shouldBe GridCell.NO_VALUE_SET
                grid.getCell(1).userValue shouldBe GridCell.NO_VALUE_SET
                grid.getCell(2).userValue shouldBe GridCell.NO_VALUE_SET
                grid.getCell(3).userValue shouldBe 2
                grid.getCell(4).userValue shouldBe 3
                grid.getCell(5).userValue shouldBe 4
                grid.getCell(6).userValue shouldBe GridCell.NO_VALUE_SET
                grid.getCell(7).userValue shouldBe GridCell.NO_VALUE_SET
                grid.getCell(8).userValue shouldBe GridCell.NO_VALUE_SET
            }

            test("fillSingleCagesInNewGrid with remove pencils deletes pencils marks") {
                val preferences =
                    mockk<ApplicationPreferences> {
                        every { removePencils() } returns true
                    }

                val grid =
                    GridBuilder(3)
                        .addCage(1, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 0)
                        .addSingleCage(2, 3)
                        .addSingleCage(3, 4)
                        .addSingleCage(4, 5)
                        .addCage(1, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 6)
                        .createGrid()

                grid.addPossiblesAtNewGame()

                grid.getCell(3).value = 1
                grid.getCell(4).value = 2
                grid.getCell(5).value = 3

                val game =
                    Game(
                        initalGrid = grid,
                        mockk(relaxed = true),
                        mockk(relaxed = true),
                        preferences,
                    )

                game.fillSingleCagesInNewGrid()

                grid.getCell(0).possibles shouldBe setOf(2, 3)
                grid.getCell(1).possibles shouldBe setOf(1, 3)
                grid.getCell(2).possibles shouldBe setOf(1, 2)
                grid.getCell(3).possibles shouldBe emptySet()
                grid.getCell(4).possibles shouldBe emptySet()
                grid.getCell(5).possibles shouldBe emptySet()
                grid.getCell(6).possibles shouldBe setOf(2, 3)
                grid.getCell(7).possibles shouldBe setOf(1, 3)
                grid.getCell(8).possibles shouldBe setOf(1, 2)
            }
        },
    )

private fun gameWithSmallGrid(preferences: ApplicationPreferences): Game {
    val smallGrid =
        GridBuilder(2)
            .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
            .addSingleCage(2, 3)
            .createGrid()

    smallGrid.cells[0].userValue = 2
    smallGrid.cells[1].addPossible(1)
    smallGrid.cells[1].addPossible(2)

    return Game(
        initalGrid = smallGrid,
        gridUI = mockk(relaxed = true),
        statisticsManager = mockk(relaxed = true),
        applicationPreferences = preferences,
    )
}
