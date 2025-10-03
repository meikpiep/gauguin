package org.piepmeyer.gauguin.ui.statistics

import androidx.lifecycle.Lifecycle
import com.github.takahirom.roborazzi.captureRoboImage
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.piepmeyer.gauguin.MainApplication
import org.piepmeyer.gauguin.ScreenshotTest
import org.piepmeyer.gauguin.ScreenshotTestUtils
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
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
class StatisticsActivityScreenshotTest(
    private val testItem: TestDataForActivity<UiStateEnum>,
) : KoinTest {
    enum class UiStateEnum {
        NoStatistics,
        FilledStatistics,
        MoreThanThousandGamesPlayed,
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
                ).forConfigs(
                    ActivityConfigItem(uiMode = UiMode.DAY, orientation = Orientation.PORTRAIT),
                    ActivityConfigItem(uiMode = UiMode.NIGHT, orientation = Orientation.LANDSCAPE),
                ).combineAll()
    }

    @Before
    fun before() {
        MainApplication.avoidNightModeConfigurationForTest = true
    }

    @After
    fun after() {
        stopKoin()

        MainApplication.avoidNightModeConfigurationForTest = false
        MainApplication.overrideTestModule = null
    }

    @Config(sdk = [34])
    @Test
    fun screenshotTest() {
        MainApplication.overrideTestModule = createOverrideModuleWithStatisticsData()

        val configurator = ScreenshotTestUtils.createActivityConfigurator(testItem)

        val activityScenario =
            configurator.launch(StatisticsActivity::class.java)

        activityScenario
            .rootView
            .captureRoboImage(ScreenshotTestUtils.filePath(this::class, testItem))

        activityScenario.moveToState(Lifecycle.State.DESTROYED)
    }

    private fun createOverrideModuleWithStatisticsData(): Module? =
        when (testItem.uiState) {
            UiStateEnum.NoStatistics -> null

            UiStateEnum.MoreThanThousandGamesPlayed -> {
                val statistics =
                    mockk<StatisticsManagerReading>(relaxed = true) {
                        every { totalStarted() } returns 22_239
                        every { totalSolved() } returns 10_738
                        every { longestStreak() } returns 11_475
                        every { currentStreak() } returns 10_932
                    }

                moduleWithStatistics(statistics)
            }

            UiStateEnum.FilledStatistics -> {
                val statistics =
                    mockk<StatisticsManagerReading>(relaxed = true) {
                        every { totalStarted() } returns 3
                        every { totalSolved() } returns 3
                        every { longestStreak() } returns 3
                        every { currentStreak() } returns 3
                        every { statistics().overall.gamesStarted } returns 3
                        every { statistics().overall.gamesSolved } returns 3
                        every { statistics().overall.solvedDuration } returns mutableListOf(25, 2, 90)
                        every { statistics().overall.streakSequence } returns mutableListOf(0, 1, 2)
                        every { statistics().overall.solvedDifficulty } returns mutableListOf(0.6, 1.4, 3.5)
                        every { statistics().overall.solvedDifficultyMinimum } returns 0.6
                        every { statistics().overall.solvedDifficultySum } returns 0.6 + 1.4 + 3.5
                        every { statistics().overall.solvedDifficultyMaximum } returns 3.5
                    }

                moduleWithStatistics(statistics)
            }
        }

    private fun moduleWithStatistics(statistics: StatisticsManagerReading): Module =
        module {
            single {
                statistics
            } withOptions {
                binds(listOf(StatisticsManagerReading::class))
            }
        }
}
