package org.piepmeyer.gauguin.ui.main

import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.ui.DeviceOrientation
import org.piepmeyer.gauguin.ui.WindowClassCalculator

class KeyPadLayoutCalculator(
    private val sizeCalculator: WindowClassCalculator,
) : KoinComponent {
    fun calculateLayoutId(grid: Grid): Int {
        sizeCalculator.computeValues()

        return when {
            (
                sizeCalculator.width != WindowWidthSizeClass.COMPACT &&
                    sizeCalculator.height != WindowHeightSizeClass.COMPACT
            ) -> R.layout.fragment_key_pad
            (
                grid.gridSize.largestSide() > 9 &&
                    sizeCalculator.orientation == DeviceOrientation.Portrait &&
                    sizeCalculator.width != WindowWidthSizeClass.EXPANDED
            ) -> R.layout.fragment_key_pad_compact_portrait
            (
                sizeCalculator.orientation == DeviceOrientation.Portrait &&
                    sizeCalculator.width == WindowWidthSizeClass.COMPACT &&
                    sizeCalculator.height == WindowHeightSizeClass.COMPACT
            ) -> R.layout.fragment_key_pad_compact_portrait
            (
                grid.gridSize.largestSide() > 9 &&
                    sizeCalculator.orientation == DeviceOrientation.Landscape &&
                    sizeCalculator.height == WindowHeightSizeClass.COMPACT
            ) -> R.layout.fragment_key_pad_compact_landscape
            else -> R.layout.fragment_key_pad
        }
    }

    fun calculateLayoutMarginBottom(grid: Grid): Int {
        sizeCalculator.computeValues()

        return if (grid.gridSize.largestSide() <= 3 &&
            sizeCalculator.orientation == DeviceOrientation.Portrait
        ) {
            48
        } else {
            0
        }
    }
}
