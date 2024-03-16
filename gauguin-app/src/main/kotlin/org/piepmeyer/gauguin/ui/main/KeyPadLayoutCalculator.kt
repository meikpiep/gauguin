package org.piepmeyer.gauguin.ui.main

import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.grid.Grid

class KeyPadLayoutCalculator(
    private val sizeCalculator: WindowClassCalculator,
) : KoinComponent {
    fun calculateLayoutId(grid: Grid): Int {
        sizeCalculator.computeValues()

        return if (grid.gridSize.largestSide() > 9 &&
            sizeCalculator.orientation == WindowClassCalculator.DeviceOrientation.Portrait &&
            sizeCalculator.widthWindowSizeClass != WindowWidthSizeClass.EXPANDED
        ) {
            R.layout.fragment_key_pad_compact_portrait
        } else if (grid.gridSize.largestSide() > 9 &&
            sizeCalculator.orientation == WindowClassCalculator.DeviceOrientation.Landscape &&
            sizeCalculator.heightWindowSizeClass == WindowHeightSizeClass.COMPACT
        ) {
            R.layout.fragment_key_pad_compact_landscape
        } else {
            R.layout.fragment_key_pad
        }
    }
}
