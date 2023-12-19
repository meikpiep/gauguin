package org.piepmeyer.gauguin.game

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.runs
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManager

class GameTest : FunSpec(), KoinTest {
    init {
        MockProvider.register { mockkClass(it) }

        test("restart game clears all values and all possible values") {
            startKoin { }

            declareMock<ApplicationPreferences> {
                every { showDupedDigits() } returns true
            }

            val smallGrid =
                GridBuilder(2)
                    .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
                    .addSingleCage(2, 3)
                    .createGrid()

            smallGrid.cells[0].userValue = 2
            smallGrid.cells[1].addPossible(1)
            smallGrid.cells[1].addPossible(2)

            val game =
                Game(
                    grid = smallGrid,
                    undoManager = mockk(relaxed = true),
                    gridUI = mockk(relaxed = true),
                )

            game.restartGame()

            smallGrid.cells.forEach { it.userValue shouldBe GridCell.NO_VALUE_SET }
            smallGrid.cells.forEach { it.possibles.shouldBeEmpty() }

            stopKoin()
        }

        test("revealing a cell sets the correct user value and clears possible numbers") {

            startKoin {}

            declareMock<ApplicationPreferences> {
                every { showDupedDigits() } returns true
                every { removePencils() } returns true
            }
            declareMock<StatisticsManager> {
                every { puzzleStartedToBePlayed() } just runs
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
                    grid = smallGrid,
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                )

            game.revealCell(cellToReveal)

            cellToReveal.possibles.shouldBeEmpty()
            cellToReveal.userValue shouldBe 2

            stopKoin()
        }
    }
}
