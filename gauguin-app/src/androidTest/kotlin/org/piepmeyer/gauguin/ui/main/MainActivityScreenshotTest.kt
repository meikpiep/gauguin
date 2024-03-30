package org.piepmeyer.gauguin.ui.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.creation.GridCreator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

@RunWith(AndroidJUnit4::class)
class MainActivityScreenshotTest : KoinComponent {
    @get:Rule
    val rule =
        ScreenshotRule(MainActivity::class.java)

    private val game: Game by inject()
    private val preferences: ApplicationPreferences by inject()

    @ScreenshotInstrumentation
    @Test
    fun newGameUntouched() {
        rule.setViewModifications {
            preferences.gridTakesRemainingSpaceIfNecessary = false
            game.updateGrid(createDefaultGrid())
        }

        rule.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun newGameWith() {
        rule.setViewModifications {
            preferences.gridTakesRemainingSpaceIfNecessary = false
            game.updateGrid(createDefaultGrid())

            game.selectCell(game.grid.getCell(0))
            game.enterNumber(1)
            game.gridUI.invalidate()
        }

        rule.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun newGameWithRectangularGrid() {
        rule.setViewModifications {
            preferences.gridTakesRemainingSpaceIfNecessary = true
            game.updateGrid(createGrid(11, 11))

            game.gridUI.invalidate()
        }

        rule.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun newGameWithRectangularGridAndFastFinishingMode() {
        rule.setViewModifications {
            preferences.gridTakesRemainingSpaceIfNecessary = true
            preferences.useFastFinishingMode = true
            game.updateGrid(createGrid(11, 11))

            game.selectCell(game.grid.getCell(40))
            game.enterFastFinishingMode()

            game.gridUI.invalidate()
        }

        rule.assertSame()
    }

    private fun createDefaultGrid(): Grid {
        return createGrid(9, 9)
    }

    private fun createGrid(
        width: Int,
        height: Int,
    ): Grid {
        val randomizer = SeedRandomizerMock(0)

        return GridCreator(
            GameVariant(GridSize(width, height), GameOptionsVariant.createClassic()),
            randomizer,
            RandomPossibleDigitsShuffler(randomizer.random),
        ).createRandomizedGridWithCages()
    }
}
