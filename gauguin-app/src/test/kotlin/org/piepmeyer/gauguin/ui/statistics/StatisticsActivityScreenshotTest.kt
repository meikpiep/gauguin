package org.piepmeyer.gauguin.ui.statistics

import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.core.KoinApplication
import org.koin.core.component.inject
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.piepmeyer.gauguin.MainApplication
import org.piepmeyer.gauguin.ScreenshotTest
import org.piepmeyer.gauguin.ScreenshotTestUtils
import org.piepmeyer.gauguin.creation.GridCreator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManagerImpl
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
import org.piepmeyer.gauguin.preferences.StatisticsManagerWriting
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import sergio.sastre.uitesting.robolectric.activityscenario.robolectricActivityScenarioForActivityRule
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivity
import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivityCombinator
import sergio.sastre.uitesting.utils.activityscenario.ActivityConfigItem
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import kotlin.time.Duration.Companion.seconds

@Category(ScreenshotTest::class)
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class StatisticsActivityScreenshotTest(
    private val testItem: TestDataForActivity<UiStateEnum>,
) : KoinTest {
    enum class UiStateEnum {
        NoStatistics,
        FilledStatistics,
        MoreThanThousandGamesPlayed,
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            MainApplication.avoidNightModeConfigurationForTest = true
        }

        @AfterClass
        @JvmStatic
        fun afterAll() {
            MainApplication.avoidNightModeConfigurationForTest = false
        }

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
                ).forConfigs(
                    ActivityConfigItem(uiMode = UiMode.DAY, orientation = Orientation.PORTRAIT),
                    ActivityConfigItem(uiMode = UiMode.NIGHT, orientation = Orientation.LANDSCAPE),
                ).combineAll()
    }

    @get:Rule
    val robolectricScreenshotRule =
        robolectricActivityScenarioForActivityRule<StatisticsActivity>(
            config = testItem.config,
            deviceScreen = testItem.device,
        )

    private val statisticsManager: StatisticsManagerReading by inject()
    private val statisticsManagerWriting: StatisticsManagerWriting by inject()
    private val preferences: ApplicationPreferences by inject()
    private val activityUtils: ActivityUtils by inject()

    @Before
    fun before() {
        KoinApplication.init()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Config(sdk = [34]) // Do not use qualifiers if using `DeviceScreen` in the Rule
    @Test
    fun screenshotTest() {
        robolectricScreenshotRule.activityScenario.onActivity {
            preferences.clear()
            preferences.nightMode = ScreenshotTestUtils.nightMode(testItem.config)

            onActivityViaUiState()

            it.recreate()
        }

        robolectricScreenshotRule
            .rootView
            .captureRoboImage(ScreenshotTestUtils.filePath(this::class, testItem))
    }

    private fun onActivityViaUiState() {
        when (testItem.uiState) {
            UiStateEnum.NoStatistics -> {
            }

            UiStateEnum.MoreThanThousandGamesPlayed -> {
                statisticsManager.statistics().overall.gamesStarted = 22_239
                statisticsManager.statistics().overall.gamesSolved = 10_738
                statisticsManager.statistics().overall.longestStreak = 11_475
                statisticsManager.statistics().overall.streakSequence += 10_932
            }

            UiStateEnum.FilledStatistics -> {
                val gridOne = createGrid(0)
                val gridTwo = createGrid(1)
                val gridThree = createGrid(2)

                gridOne.playTime = 25.seconds
                gridTwo.playTime = 2.seconds
                gridThree.playTime = 90.seconds

                statisticsManagerWriting.puzzleStartedToBePlayed()
                statisticsManagerWriting.puzzleStartedToBePlayed()
                statisticsManagerWriting.puzzleStartedToBePlayed()

                statisticsManagerWriting.storeStreak(false)
                statisticsManagerWriting.storeStreak(true)
                statisticsManagerWriting.storeStreak(true)

                statisticsManagerWriting.puzzleSolved(gridOne)
                statisticsManagerWriting.puzzleSolved(gridTwo)
                statisticsManagerWriting.puzzleSolved(gridThree)

                statisticsManager.statistics().overall.gamesStarted = 3

                val impl = statisticsManager as StatisticsManagerImpl
                impl.storeStreak(true)
            }
        }
    }

    private fun createGrid(seed: Int): Grid {
        val randomizer = SeedRandomizerMock(seed)

        val variant = GameVariant(GridSize(3, 3), GameOptionsVariant.createClassic())

        return GridCreator(
            variant,
            randomizer,
            RandomPossibleDigitsShuffler(randomizer.random),
        ).createRandomizedGridWithCages()
    }
}
