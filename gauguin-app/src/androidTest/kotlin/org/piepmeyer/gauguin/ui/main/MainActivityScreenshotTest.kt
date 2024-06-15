package org.piepmeyer.gauguin.ui.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.creation.GridCreator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

@RunWith(AndroidJUnit4::class)
class MainActivityScreenshotTest : KoinComponent {
    @get:Rule
    val rule =
        ScreenshotRule(MainActivity::class.java)
            .configure {
                focusTargetId = R.id.hint
                // orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

    private val game: Game by inject()
    private val preferences: ApplicationPreferences by inject()

    @ScreenshotInstrumentation
    @Test
    fun newGameUntouched() {
        rule.setViewModifications {
            preferences.clear()
            preferences.gridTakesRemainingSpaceIfNecessary = false
            game.updateGrid(createDefaultGrid())
        }

        rule.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun newGameWith() {
        rule.setViewModifications {
            preferences.clear()
            preferences.gridTakesRemainingSpaceIfNecessary = false
            game.updateGrid(createDefaultGrid())

            game.selectCell(game.grid.getCell(0))
            game.enterNumber(1)
            game.grid.getCell(20).possibles = game.grid.variant.possibleDigits
            game.gridUI.invalidate()
        }

        rule.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun gameWith6x6GridFromZeroOnPossibleIn3x3() {
        rule.setViewModifications {
            preferences.clear()
            preferences.show3x3Pencils = true
            game.updateGrid(
                createGrid(
                    GameVariant(
                        GridSize(6, 6),
                        GameOptionsVariant.createClassic(DigitSetting.FIRST_DIGIT_ZERO),
                    ),
                ),
            )

            game.selectCell(game.grid.getCell(0))
            game.grid.getCell(25).possibles = game.grid.variant.possibleDigits
            game.gridUI.invalidate()
        }

        rule.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun gameWith7x7GridFromZeroOnPossibleIn3x3() {
        rule.setViewModifications {
            preferences.clear()
            preferences.show3x3Pencils = true
            game.updateGrid(
                createGrid(
                    GameVariant(
                        GridSize(7, 7),
                        GameOptionsVariant.createClassic(DigitSetting.FIRST_DIGIT_ZERO),
                    ),
                ),
            )

            game.selectCell(game.grid.getCell(0))
            game.grid.getCell(22).possibles = game.grid.variant.possibleDigits
            game.gridUI.invalidate()
        }

        rule.assertSame()
    }

    @ScreenshotInstrumentation
    @Test
    fun newGameWithRectangularGrid() {
        rule.setViewModifications {
            preferences.clear()
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
            preferences.clear()
            preferences.gridTakesRemainingSpaceIfNecessary = true
            preferences.useFastFinishingMode = true
            game.updateGrid(createGrid(11, 11))

            game.selectCell(game.grid.getCell(40))
            game.enterFastFinishingMode()

            game.gridUI.invalidate()
        }

        rule.assertSame()
    }

    private fun createDefaultGrid(): Grid = createGrid(9, 9)

    private fun createGrid(
        width: Int,
        height: Int,
    ): Grid =
        createGrid(
            GameVariant(GridSize(width, height), GameOptionsVariant.createClassic()),
        )

    private fun createGrid(variant: GameVariant): Grid {
        val randomizer = SeedRandomizerMock(0)

        return GridCreator(
            variant,
            randomizer,
            RandomPossibleDigitsShuffler(randomizer.random),
        ).createRandomizedGridWithCages()
    }
}
