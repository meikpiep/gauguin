package org.piepmeyer.gauguin.ui.newgame.sizing

import androidx.lifecycle.Lifecycle
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.piepmeyer.gauguin.MainApplication
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.ScreenshotTest
import org.piepmeyer.gauguin.ScreenshotTestUtils
import org.piepmeyer.gauguin.creation.GridCreator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.ui.grid.GridUI
import org.piepmeyer.gauguin.ui.newgame.NewGameActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.experimental.LazyApplication
import sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioConfigurator
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.utils.rootView

@Category(ScreenshotTest::class)
@RunWith(RobolectricTestRunner::class)
@LazyApplication(LazyApplication.LazyLoad.ON)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class NewGameActivitySizingScreenshotTest : KoinTest {
    @Before
    fun before() {
        MainApplication.avoidNightModeConfigurationForTest = true
    }

    @After
    fun after() {
        stopKoin()

        MainApplication.avoidNightModeConfigurationForTest = false
    }

    val grid =
        createGrid(
            GameVariant(GridSize(7, 7), GameOptionsVariant.createClassic()),
        )

    @Config(sdk = [30])
    @Test
    fun screenshotTest() {
        listOf(200, 300, 400, 600, 800).forEach { widthInDp ->
            listOf(200, 300, 400, 600, 800).forEach { heightInDp ->
                val activityScenario =
                    RobolectricActivityScenarioConfigurator
                        .ForActivity()
                        .setDeviceScreen(
                            DeviceScreen.Phone.PIXEL_4A.copy(
                                widthDp = widthInDp,
                                heightDp = heightInDp,
                            ),
                        ).setUiMode(UiMode.NIGHT)
                        .setFontSize(FontSize.NORMAL)
                        .setOrientation(if (widthInDp >= heightInDp) Orientation.LANDSCAPE else Orientation.PORTRAIT)
                        .launch(NewGameActivity::class.java)

                activityScenario.onActivity {
                    it.findViewById<GridUI>(R.id.newGridPreview).grid = grid
                }

                activityScenario
                    .rootView
                    .captureRoboImage(
                        ScreenshotTestUtils.filePath(
                            this::class,
                            "${widthInDp}x${heightInDp}dp",
                        ),
                        RoborazziOptions(),
                    )

                activityScenario.moveToState(Lifecycle.State.DESTROYED)
            }
        }
    }

    private fun createGrid(variant: GameVariant): Grid {
        val randomizer = SeedRandomizerMock(0)

        return GridCreator(
            variant,
            randomizer,
            RandomPossibleDigitsShuffler(randomizer.random),
        ).createRandomizedGridWithCages()
    }
}
