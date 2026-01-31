package org.piepmeyer.gauguin.ui.grid

import android.graphics.Color
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.piepmeyer.gauguin.ScreenshotTest
import org.piepmeyer.gauguin.ScreenshotTestUtils
import org.piepmeyer.gauguin.creation.GridCreator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.game.FastFinishingModeState
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.Theme
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.experimental.LazyApplication
import sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioConfigurator
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.robolectric.config.screen.DpiDensity
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.utils.waitForActivity
import sergio.sastre.uitesting.utils.utils.waitForView

@Category(ScreenshotTest::class)
@RunWith(RobolectricTestRunner::class)
@LazyApplication(LazyApplication.LazyLoad.ON)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GridViewScreenshotTest : KoinTest {
    @After
    fun after() {
        stopKoin()
    }

    val grid =
        createGrid(
            GameVariant(GridSize(7, 7), GameOptionsVariant.createClassic()),
        )

    @Config(sdk = [30])
    @Test
    fun screenshotTest() {
        UiMode.entries.forEach { uiMode ->
            Theme.entries.forEach { theme ->
                FastFinishingModeState.entries.forEach { fastFinishingMode ->
                    val activityScenario =
                        RobolectricActivityScenarioConfigurator
                            .ForView()
                            .setDeviceScreen(
                                DeviceScreen(
                                    400,
                                    400,
                                    density = DpiDensity.Value(240),
                                ),
                            ).setUiMode(uiMode)
                            .setFontSize(FontSize.NORMAL)
                            .launchConfiguredActivity(Color.GRAY)

                    val activity = activityScenario.waitForActivity()

                    get<ApplicationPreferences>().theme = theme
                    ActivityUtils().configureTheme(activity)

                    val viewHolder =
                        waitForView {
                            GridUI(activity)
                        }

                    viewHolder.grid = grid

                    viewHolder.updateTheme(false)
                    viewHolder.reCreate()

                    val game = get<Game>()
                    game.useGrid(grid)

                    val cell = grid.cells[17]
                    cell.userValue = 5

                    val cellWithPossibles = grid.cells[18]
                    cellWithPossibles.possibles = grid.variant.possibleDigits

                    grid.cells[15].userValue = 1
                    grid.cells[22].userValue = 1
                    grid.cells[20].userValue = 2
                    grid.cells[35].userValue = 6
                    grid.cells[42].userValue = 1

                    game.selectCell(cell)

                    if (fastFinishingMode == FastFinishingModeState.Fast) {
                        game.enterFastFinishingMode()
                    } else {
                        game.exitFastFinishingMode()
                    }

                    viewHolder
                        .captureRoboImage(
                            ScreenshotTestUtils.filePath(
                                this::class,
                                "${theme.name}_${uiMode.name}_${fastFinishingMode.name}",
                            ),
                        )

                    activityScenario.close()
                }
            }
        }
    }

    private fun createGrid(variant: GameVariant): Grid {
        val randomizer = SeedRandomizerMock(0)

        val grid =
            GridCreator(
                variant,
                randomizer,
                RandomPossibleDigitsShuffler(randomizer.random),
            ).createRandomizedGridWithCages()

        grid.isActive = true

        return grid
    }
}
