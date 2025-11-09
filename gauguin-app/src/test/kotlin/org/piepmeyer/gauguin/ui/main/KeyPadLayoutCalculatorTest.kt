package org.piepmeyer.gauguin.ui.main

import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.ui.DeviceOrientation
import org.piepmeyer.gauguin.ui.DeviceOrientation.Landscape
import org.piepmeyer.gauguin.ui.DeviceOrientation.Portrait
import org.piepmeyer.gauguin.ui.WindowClassCalculator

class KeyPadLayoutCalculatorTest :
    FunSpec({

        context("test") {
            withData(
                KeyPadLayoutCalculatorTestData(
                    Portrait,
                    WindowWidthSizeClass.EXPANDED,
                    WindowHeightSizeClass.EXPANDED,
                    5,
                    R.layout.fragment_key_pad,
                ),
                KeyPadLayoutCalculatorTestData(
                    Landscape,
                    WindowWidthSizeClass.EXPANDED,
                    WindowHeightSizeClass.EXPANDED,
                    5,
                    R.layout.fragment_key_pad,
                ),
                KeyPadLayoutCalculatorTestData(
                    Portrait,
                    WindowWidthSizeClass.COMPACT,
                    WindowHeightSizeClass.MEDIUM,
                    10,
                    R.layout.fragment_key_pad_compact_portrait,
                ),
                KeyPadLayoutCalculatorTestData(
                    Portrait,
                    WindowWidthSizeClass.COMPACT,
                    WindowHeightSizeClass.COMPACT,
                    7,
                    R.layout.fragment_key_pad_compact_portrait,
                ),
                KeyPadLayoutCalculatorTestData(
                    Landscape,
                    WindowWidthSizeClass.EXPANDED,
                    WindowHeightSizeClass.COMPACT,
                    10,
                    R.layout.fragment_key_pad_compact_landscape,
                ),
                KeyPadLayoutCalculatorTestData(
                    Portrait,
                    WindowWidthSizeClass.MEDIUM,
                    WindowHeightSizeClass.MEDIUM,
                    9,
                    R.layout.fragment_key_pad,
                ),
                KeyPadLayoutCalculatorTestData(
                    Portrait,
                    WindowWidthSizeClass.MEDIUM,
                    WindowHeightSizeClass.MEDIUM,
                    11,
                    R.layout.fragment_key_pad,
                ),
                KeyPadLayoutCalculatorTestData(
                    Portrait,
                    WindowWidthSizeClass.EXPANDED,
                    WindowHeightSizeClass.EXPANDED,
                    11,
                    R.layout.fragment_key_pad,
                ),
            ) { (orientation, widthWindowSizeClass, heightWindowSizeClass, largestSide, expectedLayoutId) ->
                val sizeCalculator = mockk<WindowClassCalculator>()

                every { sizeCalculator.computeValues() } just runs
                every { sizeCalculator.orientation } returns orientation
                every { sizeCalculator.width } returns widthWindowSizeClass
                every { sizeCalculator.height } returns heightWindowSizeClass

                val grid =
                    mockk<Grid> {
                        every { gridSize.largestSide() } returns largestSide
                    }

                withClue("$orientation $widthWindowSizeClass $heightWindowSizeClass $largestSide") {
                    KeyPadLayoutCalculator(sizeCalculator).calculateLayoutId(grid) shouldBe expectedLayoutId
                }
            }
        }
    })

data class KeyPadLayoutCalculatorTestData(
    val orientation: DeviceOrientation,
    val widthWindowSizeClass: WindowWidthSizeClass,
    val heightWindowSizeClass: WindowHeightSizeClass,
    val largestSide: Int,
    val expectedLayoutId: Int,
)
