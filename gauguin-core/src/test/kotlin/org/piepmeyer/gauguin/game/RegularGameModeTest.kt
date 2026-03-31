package org.piepmeyer.gauguin.game

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class RegularGameModeTest :
    FunSpec({
        test("long pressing single cage cells counts toward fast finish mode trigger") {
            val preferences =
                mockk<ApplicationPreferences> {
                    every { useFastFinishingMode } returns true
                    every { removePencils() } returns false
                    every { showDupedDigits() } returns false
                }

            val grid =
                GridBuilder(2)
                    .addCageSingle(1)
                    .addCageSingle(2)
                    .addCageSingle(3)
                    .addCageSingle(4)
                    .createGrid()

            grid.getCell(0).value = 1
            grid.getCell(1).value = 2
            grid.getCell(2).value = 3
            grid.getCell(3).value = 4
            // cell 3 has one possible so hasCellsWithSinglePossibles() returns true
            grid.getCell(3).addPossible(4)
            grid.isActive = true

            val game =
                Game(
                    initalGrid = grid,
                    gridUI = mockk(relaxed = true),
                    statisticsManager = mockk(relaxed = true),
                    applicationPreferences = preferences,
                )

            grid.selectedCell = grid.getCell(0)
            game.longClickOnSelectedCell()

            grid.selectedCell = grid.getCell(1)
            game.longClickOnSelectedCell()

            game.isInFastFinishingMode() shouldBe false

            grid.selectedCell = grid.getCell(2)
            game.longClickOnSelectedCell()

            game.isInFastFinishingMode() shouldBe true
        }
    })
