package com.holokenmod.creation

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridSize
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeIn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestGridRandomizer {
    @ParameterizedTest
    @MethodSource("gridSizeParameters")
    fun testDigitsFromOneOn(width: Int, heigth: Int) {
        Assertions.assertTimeoutPreemptively(Duration.of(10, ChronoUnit.SECONDS)) {
            val grid = Grid(GameVariant(GridSize(width, heigth), createClassic()))
            grid.addAllCells()
            grid.clearUserValues()
            val randomizer = GridRandomizer(RandomPossibleDigitsShuffler(), grid)
            randomizer.createGrid()
            for (cell in grid.cells) {
                withClue("Invalid solution of $grid") {
                    cell.value shouldBeIn grid.possibleDigits
                }
            }
        }
    }

    private fun gridSizeParameters(): Stream<Arguments> {
        val parameters = ArrayList<Arguments>()
        for (width in 3..11) {
            for (height in 3..11) {
                parameters.add(Arguments.of(width, height))
            }
        }
        return parameters.stream()
    }
}