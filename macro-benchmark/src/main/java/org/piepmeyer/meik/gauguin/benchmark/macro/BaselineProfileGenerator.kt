package org.piepmeyer.meik.gauguin.benchmark.macro

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test

class BaselineProfileGenerator {
    @RequiresApi(Build.VERSION_CODES.P)
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    fun startup() =
        baselineProfileRule.collect(
            packageName = "org.piepmeyer.gauguin",
            profileBlock = {
                startActivityAndWait()
            },
        )
}
