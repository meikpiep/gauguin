package org.piepmeyer.gauguin

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
}
