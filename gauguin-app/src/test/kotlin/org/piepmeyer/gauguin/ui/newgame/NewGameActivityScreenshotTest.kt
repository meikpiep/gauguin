package org.piepmeyer.gauguin.ui.newgame

import androidx.lifecycle.Lifecycle
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.material.tabs.TabLayout
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
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.experimental.LazyApplication
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivity
import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivityCombinator
import sergio.sastre.uitesting.utils.activityscenario.ActivityConfigItem
import sergio.sastre.uitesting.utils.common.FontSize
import sergio.sastre.uitesting.utils.common.Orientation
import sergio.sastre.uitesting.utils.common.UiMode
import sergio.sastre.uitesting.utils.utils.rootView

@Category(ScreenshotTest::class)
@RunWith(ParameterizedRobolectricTestRunner::class)
@LazyApplication(LazyApplication.LazyLoad.ON)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class NewGameActivityScreenshotTest(
    private val testItem: TestDataForActivity<UiStateEnum>,
) : KoinTest {
    enum class UiStateEnum {
        TabBasic,
        TabNumbers,
        TabAdvanced,
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
                    ActivityConfigItem(
                        uiMode = UiMode.DAY,
                        fontSize = FontSize.NORMAL,
                        orientation = Orientation.PORTRAIT,
                    ),
                    ActivityConfigItem(
                        uiMode = UiMode.DAY,
                        fontSize = FontSize.NORMAL,
                        orientation = Orientation.LANDSCAPE,
                    ),
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
        // MainApplication.overrideTestModule = null
    }

    @Config(sdk = [34])
    @Test
    fun screenshotTest() {
        val configurator = ScreenshotTestUtils.createActivityConfigurator(testItem)

        val activityScenario = configurator.launch(NewGameActivity::class.java)

        activityScenario.onActivity {
            it.findViewById<GridUI>(R.id.newGridPreview).grid = createDefaultGrid()

            val tabs = it.findViewById<TabLayout>(R.id.new_game_options_tablayout)

            when (testItem.uiState) {
                UiStateEnum.TabBasic -> tabs.selectTab(tabs.getTabAt(0))
                UiStateEnum.TabNumbers -> tabs.selectTab(tabs.getTabAt(1))
                UiStateEnum.TabAdvanced -> tabs.selectTab(tabs.getTabAt(2))
            }
        }

        activityScenario
            .rootView
            .captureRoboImage(ScreenshotTestUtils.filePath(this::class, testItem))

        activityScenario.moveToState(Lifecycle.State.DESTROYED)
    }

    private fun createDefaultGrid(): Grid =
        createGrid(
            GameVariant(GridSize(6, 6), GameOptionsVariant.createClassic()),
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
