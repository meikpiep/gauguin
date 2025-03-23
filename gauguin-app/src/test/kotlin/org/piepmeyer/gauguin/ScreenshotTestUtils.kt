package org.piepmeyer.gauguin

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
