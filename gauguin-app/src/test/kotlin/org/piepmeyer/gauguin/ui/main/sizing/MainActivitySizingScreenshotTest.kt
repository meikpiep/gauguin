package org.piepmeyer.gauguin.ui.main.sizing

import androidx.lifecycle.Lifecycle
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.piepmeyer.gauguin.MainApplication
import org.piepmeyer.gauguin.ScreenshotTest
import org.piepmeyer.gauguin.ScreenshotTestUtils
import org.piepmeyer.gauguin.creation.GridCreatorIgnoringDifficulty
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.main.MainActivity
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
class MainActivitySizingScreenshotTest : KoinTest {
    @Before
    fun before() {
        MainApplication.Companion.avoidNightModeConfigurationForTest = true
    }

    @After
    fun after() {
        stopKoin()

        MainApplication.Companion.avoidNightModeConfigurationForTest = false
        // MainApplication.overrideTestModule = null
    }

    val grid =
        createGrid(
            GameVariant(GridSize(7, 7), GameOptionsVariant.Companion.createClassic()),
        )

    @Config(sdk = [30])
    @Test
    fun screenshotTest() {
        listOf(200, 300, 400, 600, 800).forEach { widthInDp ->
            listOf(200, 300, 400, 600, 800).forEach { heightInDp ->
                val activityScenario =
                    RobolectricActivityScenarioConfigurator
                        .ForActivity()
                        .setDeviceScreen(DeviceScreen.Phone.PIXEL_4A.copy(widthInDp, heightInDp))
                        .setUiMode(UiMode.NIGHT)
                        .setFontSize(FontSize.NORMAL)
                        .setOrientation(if (widthInDp >= heightInDp) Orientation.LANDSCAPE else Orientation.PORTRAIT)
                        .launch(MainActivity::class.java)

                activityScenario.onActivity {
                    val game = get<Game>()
                    val gameLifecycle = get<GameLifecycle>()
                    val preferences = get<ApplicationPreferences>()

                    preferences.clear()

                    onActivityViaUiState(preferences, game)

                    gameLifecycle.stoppGameTimerAndResetGameTime()
                }

                activityScenario
                    .rootView
                    .captureRoboImage(
                        ScreenshotTestUtils.filePath(
                            this::class,
                            "${widthInDp}x${heightInDp}dp",
                        ),
                    )

                activityScenario.moveToState(Lifecycle.State.DESTROYED)
            }
        }
    }

    private fun onActivityViaUiState(
        preferences: ApplicationPreferences,
        game: Game,
    ) {
        preferences.gridTakesRemainingSpaceIfNecessary = false
        game.updateGrid(grid)
    }

    private fun createGrid(variant: GameVariant): Grid {
        val randomizer = SeedRandomizerMock(0)

        return GridCreatorIgnoringDifficulty(
            variant,
            randomizer,
            RandomPossibleDigitsShuffler(randomizer.random),
        ).createRandomizedGridWithCages()
    }
}
