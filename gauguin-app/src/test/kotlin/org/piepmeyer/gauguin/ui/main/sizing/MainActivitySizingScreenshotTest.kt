package org.piepmeyer.gauguin.ui.main.sizing

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import org.koin.test.get
import org.piepmeyer.gauguin.MainApplication
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.ScreenshotTest
import org.piepmeyer.gauguin.ScreenshotTestUtils
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.creation.GridCreator
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
        listOf(200, 300, 400, 600, 800, 900).forEach { widthInDp ->
            listOf(200, 300, 400, 600, 800, 900).forEach { heightInDp ->
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
                        .launch(MainActivity::class.java)

                activityScenario.onActivity {
                    val game = get<Game>()
                    val gameLifecycle = get<GameLifecycle>()
                    val preferences = get<ApplicationPreferences>()
                    val calculationService = get<GridCalculationService>()

                    dispatchSystemBarInsets(
                        view = it!!.findViewById(R.id.container),
                        top = heightInDp / 10,
                        bottom = heightInDp / 10,
                        left = widthInDp / 10,
                        right = widthInDp / 10,
                    )

                    calculationService.simulateNextGridCalculating()

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
                        RoborazziOptions(recordOptions = RoborazziOptions.RecordOptions(resizeScale = 0.5)),
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
        game.useGrid(grid)
    }

    private fun createGrid(variant: GameVariant): Grid {
        val randomizer = SeedRandomizerMock(0)

        return GridCreator(
            variant,
            randomizer,
            RandomPossibleDigitsShuffler(randomizer.random),
        ).createRandomizedGridWithCages()
    }

    private fun dispatchSystemBarInsets(
        view: View,
        left: Int = 0,
        top: Int = 0,
        right: Int = 0,
        bottom: Int = 0,
    ) {
        val insets =
            WindowInsetsCompat
                .Builder()
                .setInsets(
                    WindowInsetsCompat.Type.statusBars(),
                    Insets.of(0, top, 0, 0),
                ).setInsets(
                    WindowInsetsCompat.Type.navigationBars(),
                    Insets.of(0, 0, 0, bottom),
                ).setInsets(
                    WindowInsetsCompat.Type.displayCutout(),
                    Insets.of(left, 0, right, 0),
                ).build()

        ViewCompat.dispatchApplyWindowInsets(view, insets)
    }
}
