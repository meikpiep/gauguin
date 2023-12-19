package org.piepmeyer.gauguin.game

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction

class GridSolveServiceTest : FunSpec(), KoinTest {
    init {
        test("revealing a cell sets the correct user value and clears possible numbers") {
            MockProvider.register { mockkClass(it) }

            startKoin { }

            val smallGrid =
                GridBuilder(2)
                    .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
                    .addSingleCage(2, 3)
                    .createGrid()

            smallGrid.cells[0].addPossible(1)
            smallGrid.cells[0].addPossible(2)
            smallGrid.cells[0].value = 2
            smallGrid.selectedCell = smallGrid.cells[0]

            declareMock<Game> {
                every { grid } returns smallGrid
            }

            val solveService = GameSolveService()

            solveService.revealSelectedCell()

            smallGrid.cells[0].possibles.shouldBeEmpty()
            smallGrid.cells[0].userValue shouldBe 2
        }
    }
}
