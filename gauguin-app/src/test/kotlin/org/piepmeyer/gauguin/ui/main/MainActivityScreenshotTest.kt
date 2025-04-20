package org.piepmeyer.gauguin.ui.main

import android.os.Looper
import com.github.takahirom.roborazzi.captureRoboImage
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.piepmeyer.gauguin.MainApplication
import org.piepmeyer.gauguin.NightMode
import org.piepmeyer.gauguin.ScreenshotTest
import org.piepmeyer.gauguin.ScreenshotTestUtils
import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.calculation.GridPreviewCalculationService
import org.piepmeyer.gauguin.creation.GridCreator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import sergio.sastre.uitesting.robolectric.activityscenario.robolectricActivityScenarioForActivityRule
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivity
import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivityCombinator
import sergio.sastre.uitesting.utils.activityscenario.ActivityConfigItem
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode

@Category(ScreenshotTest::class)
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MainActivityScreenshotTest(
    private val testItem: TestDataForActivity<UiStateEnum>,
) : KoinTest {
    enum class UiStateEnum {
        NewGame,
        NewGameWithCellDetails,
        // GameWith6x6GridFromZeroOnPossibleIn3x3,
        // GameWith7x7GridFromZeroOnPossibleIn3x3,
        // NewGameWithRectangularGrid,
        // NewGameWithRectangularGridAndFastFinishingMode,
        // GameSolved,
        // CalculatingGrid,
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun testItemProvider(): Array<out TestDataForActivity<out Enum<*>>> =
            TestDataForActivityCombinator(uiStates = UiStateEnum.entries.toTypedArray())
                .forDevices(
                    // DeviceScreen.Phone.NEXUS_ONE,
                    // DeviceScreen.Phone.SMALL_PHONE,
                    DeviceScreen.Phone.PIXEL_4A,
                    // DeviceScreen.Tablet.MEDIUM_TABLET,
                    // DeviceScreen.Desktop.LARGE_DESKTOP,
                    // ScreenshotTestUtils.PIXEL_7A_SPLIT_SCREEN_HALF_HEIGHT,
                    // ScreenshotTestUtils.PIXEL_7A_SPLIT_SCREEN_FORTH_HEIGHT,
                    // ScreenshotTestUtils.PIXEL_7A_SPLIT_SCREEN_HALF_WIDTH,
                ).forConfigs(
                    ActivityConfigItem(uiMode = UiMode.DAY, orientation = Orientation.PORTRAIT),
                    ActivityConfigItem(uiMode = UiMode.NIGHT, orientation = Orientation.LANDSCAPE),
                ).combineAll()
                /*.filterNot {
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
                }*/
    }

    @get:Rule
    val robolectricScreenshotRule =
        robolectricActivityScenarioForActivityRule<MainActivity>(
            config = testItem.config,
            deviceScreen = testItem.device,
        )

    @Before
    fun before() {
        MainApplication.testOverideModule =
            module {
                single {
                    mockk<ApplicationPreferences>(relaxed = true) {
                        every { theme } returns Theme.GAUGUIN
                        every { nightMode } returns NightMode.SYSTEM_DEFAULT
                        every { difficultiesSetting } returns DifficultySetting.all()
                        every { digitSetting } returns DigitSetting.FIRST_DIGIT_ONE
                        every { numeralSystem } returns NumeralSystem.Decimal
                        every { operations } returns GridCageOperation.OPERATIONS_ALL
                        every { singleCageUsage } returns SingleCageUsage.FIXED_NUMBER
                        every { gridWidth } returns 6
                        every { gridHeigth } returns 6
                        every { squareOnlyGrid } returns true
                        every { gameOptionsVariant } returns GameOptionsVariant.createClassic()
                    }
                } withOptions { binds(listOf(ApplicationPreferences::class)) }
                single {
                    mockk<GridPreviewCalculationService>(relaxed = true) {
                        every { takeCalculatedGrid(any()) } just runs
                        every { calculateGrid(any(), any()) } just runs
                    }
                } withOptions { binds(listOf(GridPreviewCalculationService::class)) }
                single {
                    mockk<GridCalculationService>(relaxed = true) {
                        every { hasCalculatedNextGrid(any()) } returns true
                        every { consumeNextGrid() } returns createDefaultGrid()
                    }
                } withOptions { binds(listOf(GridCalculationService::class)) }
            }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Config(sdk = [30]) // Do not use qualifiers if using `DeviceScreen` in the Rule
    @Test
    fun screenshotTest() {
        robolectricScreenshotRule.activityScenario.onActivity {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        robolectricScreenshotRule
            .rootView
            .captureRoboImage(ScreenshotTestUtils.filePath(this::class, testItem))
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

        return GridCreator(
            variant,
            randomizer,
            RandomPossibleDigitsShuffler(randomizer.random),
        ).createRandomizedGridWithCages()
    }
}
