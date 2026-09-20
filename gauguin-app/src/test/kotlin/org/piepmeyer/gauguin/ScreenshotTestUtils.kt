package org.piepmeyer.gauguin

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import sergio.sastre.uitesting.robolectric.activityscenario.RobolectricActivityScenarioConfigurator
import sergio.sastre.uitesting.robolectric.config.screen.DeviceScreen
import sergio.sastre.uitesting.robolectric.config.screen.RoundScreen
import sergio.sastre.uitesting.robolectric.config.screen.ScreenAspect
import sergio.sastre.uitesting.robolectric.config.screen.ScreenDensity
import sergio.sastre.uitesting.robolectric.config.screen.ScreenOrientation
import sergio.sastre.uitesting.robolectric.config.screen.ScreenSize
import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivity
import kotlin.reflect.KClass

object ScreenshotTestUtils {
    fun <T : Enum<T>> filePath(
        kClass: KClass<out Any>,
        testData: TestDataForActivity<T>,
    ): String {
        val packageDirectory = kClass.java.packageName.replace(".", "/")

        val screenshotId =
            listOfNotNull(
                kClass.simpleName,
                testData.device?.name,
                testData.config?.id,
                testData.uiState.name,
            ).filter { it.isNotBlank() }
                .joinToString(separator = "_")

        return "src/test/resources/$packageDirectory/$screenshotId.png"
    }

    fun filePath(
        kClass: KClass<out Any>,
        testInfos: String,
    ): String {
        val packageDirectory = kClass.java.packageName.replace(".", "/")

        val screenshotId =
            listOfNotNull(
                kClass.simpleName,
                testInfos,
            ).filter { it.isNotBlank() }
                .joinToString(separator = "_")

        return "src/test/resources/$packageDirectory/$screenshotId.png"
    }

    fun createActivityConfigurator(testItem: TestDataForActivity<out Enum<*>>): RobolectricActivityScenarioConfigurator.ForActivity {
        val configurator =
            RobolectricActivityScenarioConfigurator
                .ForActivity()
                .setDeviceScreen(testItem.device!!)

        testItem.config?.fontSize?.let { configurator.setFontSize(it) }
        testItem.config?.systemLocale?.let { configurator.setSystemLocale(it) }
        testItem.config?.uiMode?.let { configurator.setUiMode(it) }
        testItem.config?.orientation?.let { configurator.setOrientation(it) }
        testItem.config?.displaySize?.let { configurator.setDisplaySize(it) }

        return configurator
    }

    fun dispatchSystemBarInsets(
        view: View,
        width: Int,
        height: Int,
    ) {
        dispatchSystemBarInsets(
            view,
            top = height / 10,
            bottom = height / 10,
            left = width / 10,
            right = width / 10,
        )
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

    /*
     * These configurations were measured on an Pixel 7a, running Android 15, with split screen.
     * Gauguin covered the top half of the screen. The detailed values were gathered by logging
     * them, so they differ from e.g. the official density and may depend on the OS version, system
     * settings and so on.
     */
    @JvmField
    val PIXEL_7A_SPLIT_SCREEN_HALF_HEIGHT =
        DeviceScreen(
            widthDp = 411,
            heightDp = 383,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.DPI_420,
            round = RoundScreen.NOTROUND,
            defaultOrientation = ScreenOrientation.LANDSCAPE,
            name = "PIXEL_7A_SPLIT_SCREEN_HALF_HEIGHT",
        )

    @JvmField
    val PIXEL_7A_SPLIT_SCREEN_FORTH_HEIGHT =
        DeviceScreen(
            widthDp = 411,
            heightDp = 190,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.DPI_420,
            round = RoundScreen.NOTROUND,
            defaultOrientation = ScreenOrientation.LANDSCAPE,
            name = "PIXEL_7A_SPLIT_SCREEN_FORTH_HEIGHT",
        )

    @JvmField
    val PIXEL_7A_SPLIT_SCREEN_HALF_WIDTH =
        DeviceScreen(
            widthDp = 430,
            heightDp = 387,
            size = ScreenSize.SMALL,
            aspect = ScreenAspect.NOTLONG,
            density = ScreenDensity.DPI_420,
            round = RoundScreen.NOTROUND,
            defaultOrientation = ScreenOrientation.LANDSCAPE,
            name = "PIXEL_7A_SPLIT_SCREEN_HALF_WIDTH",
        )
}
