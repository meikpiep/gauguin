package org.piepmeyer.gauguin.ui.main

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
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.creation.GridCreatorIgnoringDifficulty
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.experimental.LazyApplication
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivity
import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivityCombinator
import sergio.sastre.uitesting.utils.activityscenario.ActivityConfigItem
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.utils.rootView

@Category(ScreenshotTest::class)
@RunWith(ParameterizedRobolectricTestRunner::class)
@LazyApplication(LazyApplication.LazyLoad.ON)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MainActivityScreenshotTest(
    private val testItem: TestDataForActivity<UiStateEnum>,
) : KoinTest {
    enum class UiStateEnum {
        NewGame,
        NewGameWithCellDetails,
        GameWith6x6GridFromZeroOnPossibleIn3x3,
        GameWith7x7GridFromZeroOnPossibleIn3x3,
        NewGameWithRectangularGrid,
        NewGameWithRectangularGridAndFastFinishingMode,
        GameSolved,
        CalculatingGrid,
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun testItemProvider(): Array<out TestDataForActivity<out Enum<*>>> =
            TestDataForActivityCombinator(uiStates = UiStateEnum.entries.toTypedArray())
                .forDevices(
                    DeviceScreen.Phone.NEXUS_ONE,
                    DeviceScreen.Phone.SMALL_PHONE,
                    DeviceScreen.Phone.PIXEL_4A,
                    DeviceScreen.Tablet.MEDIUM_TABLET,
                    DeviceScreen.Desktop.LARGE_DESKTOP,
                    ScreenshotTestUtils.PIXEL_7A_SPLIT_SCREEN_HALF_HEIGHT,
                    ScreenshotTestUtils.PIXEL_7A_SPLIT_SCREEN_FORTH_HEIGHT,
                    ScreenshotTestUtils.PIXEL_7A_SPLIT_SCREEN_HALF_WIDTH,
                ).forConfigs(
                    ActivityConfigItem(uiMode = UiMode.DAY, orientation = Orientation.PORTRAIT),
                    ActivityConfigItem(uiMode = UiMode.NIGHT, orientation = Orientation.LANDSCAPE),
                ).combineAll()
                .filterNot {
                    it.config!!.orientation == Orientation.LANDSCAPE &&
                        it.device in
                        listOf(
                            DeviceScreen.Phone.NEXUS_ONE,
                            DeviceScreen.Phone.SMALL_PHONE,
                            DeviceScreen.Phone.PIXEL_4A,
                        )
                }.filterNot {
                    it.config!!.orientation == Orientation.PORTRAIT &&
                        it.device in
                        listOf(
                            ScreenshotTestUtils.PIXEL_7A_SPLIT_SCREEN_HALF_HEIGHT,
                            ScreenshotTestUtils.PIXEL_7A_SPLIT_SCREEN_FORTH_HEIGHT,
                            ScreenshotTestUtils.PIXEL_7A_SPLIT_SCREEN_HALF_WIDTH,
                        )
                }.toTypedArray()
    }

    @Before
    fun before() {
        MainApplication.avoidNightModeConfigurationForTest = true
    }

    @After
    fun after() {
        stopKoin()

        MainApplication.avoidNightModeConfigurationForTest = false
        // MainApplication.overrideTestModule = null
    }

    @Config(sdk = [30])
    @Test
    fun screenshotTest() {
        val configurator = ScreenshotTestUtils.createActivityConfigurator(testItem)
        val activityScenario = configurator.launch(MainActivity::class.java)

        activityScenario.onActivity {
            val game = get<Game>()
            val gameLifecycle = get<GameLifecycle>()
            val calculationService = get<GridCalculationService>()
            val preferences = get<ApplicationPreferences>()

            preferences.clear()

            onActivityViaUiState(preferences, game, calculationService)

            gameLifecycle.stoppGameTimerAndResetGameTime()
        }

        activityScenario
            .rootView
            .captureRoboImage(ScreenshotTestUtils.filePath(this::class, testItem))

        activityScenario.moveToState(Lifecycle.State.DESTROYED)
    }

    private fun onActivityViaUiState(
        preferences: ApplicationPreferences,
        game: Game,
        calculationService: GridCalculationService,
    ) {
        when (testItem.uiState) {
            UiStateEnum.NewGame -> {
                preferences.gridTakesRemainingSpaceIfNecessary = false
                game.updateGrid(createDefaultGrid())
            }

            UiStateEnum.NewGameWithCellDetails -> {
                preferences.gridTakesRemainingSpaceIfNecessary = false
                game.updateGrid(createDefaultGrid())

                game.selectCell(game.grid.getCell(0))
                game.enterNumber(1)
                game.grid.getCell(20).possibles = game.grid.variant.possibleDigits
                game.gridUI.invalidate()
            }

            UiStateEnum.GameWith6x6GridFromZeroOnPossibleIn3x3 -> {
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

            UiStateEnum.GameWith7x7GridFromZeroOnPossibleIn3x3 -> {
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

            UiStateEnum.NewGameWithRectangularGrid -> {
                preferences.gridTakesRemainingSpaceIfNecessary = true
                game.updateGrid(createGrid(11, 11))

                game.gridUI.invalidate()
            }

            UiStateEnum.NewGameWithRectangularGridAndFastFinishingMode -> {
                preferences.gridTakesRemainingSpaceIfNecessary = true
                preferences.useFastFinishingMode = true
                game.updateGrid(createGrid(11, 11))

                game.selectCell(game.grid.getCell(40))
                game.enterFastFinishingMode()

                game.gridUI.invalidate()
            }

            UiStateEnum.GameSolved -> {
                preferences.gridTakesRemainingSpaceIfNecessary = false

                game.exitFastFinishingMode()
                game.updateGrid(createGrid(11, 11))
                game.selectCell(game.grid.getCell(40))

                game.solveAllMissingCells()
            }

            UiStateEnum.CalculatingGrid -> {
                preferences.gridTakesRemainingSpaceIfNecessary = false

                game.exitFastFinishingMode()
                game.updateGrid(createDefaultGrid())

                calculationService.setCurrentGridCalculatingForTest()
            }
        }
    }

    private fun createDefaultGrid(): Grid = createGrid(9, 9)

    private fun createGrid(
        width: Int,
        height: Int,
    ): Grid {
        val grid =
            createGrid(
                GameVariant(GridSize(width, height), GameOptionsVariant.createClassic()),
            )

        grid.isActive = true

        return grid
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
