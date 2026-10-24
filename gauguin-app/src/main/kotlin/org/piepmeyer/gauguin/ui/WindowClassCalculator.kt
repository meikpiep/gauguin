package org.piepmeyer.gauguin.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import androidx.window.layout.WindowMetricsCalculator

class WindowClassCalculator(
    private val activity: Activity,
) {
    var widthInDp: Float = 0f
    var heightInDp: Float = 0f

    lateinit var width: WindowWidthSizeClass
    lateinit var height: WindowHeightSizeClass
    lateinit var orientation: DeviceOrientation

    fun computeValues() {
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)
        val width = metrics.bounds.width()
        val height = metrics.bounds.height()
        val density = activity.resources.displayMetrics.density
        val windowSizeClass =
            WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
                widthDp = width / density,
                heightDp = height / density,
            )

        this.widthInDp = width / density
        this.heightInDp = height / density
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
