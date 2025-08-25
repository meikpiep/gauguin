package org.piepmeyer.gauguin.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.WindowMetricsCalculator

class WindowClassCalculator(
    private val activity: Activity,
) {
    lateinit var width: WindowWidthSizeClass
    lateinit var height: WindowHeightSizeClass
    lateinit var orientation: DeviceOrientation

    fun computeValues() {
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
        val width = metrics.bounds.width()
        val height = metrics.bounds.height()
        val density = activity.resources.displayMetrics.density
        val windowSizeClass = WindowSizeClass.compute(width / density, height / density)

        this.width = windowSizeClass.windowWidthSizeClass
        this.height = windowSizeClass.windowHeightSizeClass

        orientation =
            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                DeviceOrientation.Portrait
            } else {
                DeviceOrientation.Landscape
            }
    }
}
