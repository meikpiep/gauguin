package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridSize
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test

internal class TestGridSingleCageCreator {
    @Test
    fun allDivideResultsWithoutZero() {
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic()
            )
        )
        val cage = GridCage(0, grid, GridCageAction.ACTION_DIVIDE)
        cage.result = 2
        val creator = GridSingleCageCreator(grid, cage)
        MatcherAssert.assertThat(creator.allDivideResults().size, CoreMatchers.`is`(4))
        MatcherAssert.assertThat(creator.possibleNums[0][0], CoreMatchers.`is`(2))
        MatcherAssert.assertThat(creator.possibleNums[0][1], CoreMatchers.`is`(1))
        MatcherAssert.assertThat(creator.possibleNums[1][0], CoreMatchers.`is`(1))
        MatcherAssert.assertThat(creator.possibleNums[1][1], CoreMatchers.`is`(2))
        MatcherAssert.assertThat(creator.possibleNums[2][0], CoreMatchers.`is`(4))
        MatcherAssert.assertThat(creator.possibleNums[2][1], CoreMatchers.`is`(2))
        MatcherAssert.assertThat(creator.possibleNums[3][0], CoreMatchers.`is`(2))
        MatcherAssert.assertThat(creator.possibleNums[3][1], CoreMatchers.`is`(4))
    }

    @Test
    fun allDivideResultsWithZero() {
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic(DigitSetting.FIRST_DIGIT_ZERO)
            )
        )
        val cage = GridCage(0, grid, GridCageAction.ACTION_DIVIDE)
        cage.result = 0
        val creator = GridSingleCageCreator(grid, cage)
        MatcherAssert.assertThat(creator.allDivideResults().size, CoreMatchers.`is`(6))
        MatcherAssert.assertThat(creator.possibleNums[0][0], CoreMatchers.`is`(1))
        MatcherAssert.assertThat(creator.possibleNums[0][1], CoreMatchers.`is`(0))
        MatcherAssert.assertThat(creator.possibleNums[1][0], CoreMatchers.`is`(0))
        MatcherAssert.assertThat(creator.possibleNums[1][1], CoreMatchers.`is`(1))
        MatcherAssert.assertThat(creator.possibleNums[2][0], CoreMatchers.`is`(2))
        MatcherAssert.assertThat(creator.possibleNums[2][1], CoreMatchers.`is`(0))
        MatcherAssert.assertThat(creator.possibleNums[3][0], CoreMatchers.`is`(0))
        MatcherAssert.assertThat(creator.possibleNums[3][1], CoreMatchers.`is`(2))
        MatcherAssert.assertThat(creator.possibleNums[4][0], CoreMatchers.`is`(3))
        MatcherAssert.assertThat(creator.possibleNums[4][1], CoreMatchers.`is`(0))
        MatcherAssert.assertThat(creator.possibleNums[5][0], CoreMatchers.`is`(0))
        MatcherAssert.assertThat(creator.possibleNums[5][1], CoreMatchers.`is`(3))
    }

    @Test
    fun allSubtractResults() {
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic()
            )
        )
        val cage = GridCage(0, grid, GridCageAction.ACTION_SUBTRACT)
        cage.result = 2
        val creator = GridSingleCageCreator(grid, cage)
        MatcherAssert.assertThat(creator.possibleNums.size, CoreMatchers.`is`(4))
        MatcherAssert.assertThat(creator.possibleNums[0][0], CoreMatchers.`is`(1))
        MatcherAssert.assertThat(creator.possibleNums[0][1], CoreMatchers.`is`(3))
        MatcherAssert.assertThat(creator.possibleNums[1][0], CoreMatchers.`is`(2))
        MatcherAssert.assertThat(creator.possibleNums[1][1], CoreMatchers.`is`(4))
        MatcherAssert.assertThat(creator.possibleNums[2][0], CoreMatchers.`is`(3))
        MatcherAssert.assertThat(creator.possibleNums[2][1], CoreMatchers.`is`(1))
        MatcherAssert.assertThat(creator.possibleNums[3][0], CoreMatchers.`is`(4))
        MatcherAssert.assertThat(creator.possibleNums[3][1], CoreMatchers.`is`(2))
    }
}