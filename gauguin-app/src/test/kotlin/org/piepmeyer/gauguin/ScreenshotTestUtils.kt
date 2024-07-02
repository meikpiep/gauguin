package org.piepmeyer.gauguin

import sergio.sastre.uitesting.robolectric.utils.activity.TestDataForActivity
import kotlin.reflect.KClass

object ScreenshotTestUtils {
    fun <T : Enum<T>> filePath(
        kClass: KClass<out Any>,
        testData: TestDataForActivity<T>,
    ): String {
        val packageDirectory = kClass.java.packageName.replace(".", "/")

        return "src/test/resources/$packageDirectory/${kClass.simpleName}_${testData.screenshotId}.png"
    }
}
