package com.holokenmod.backtrack.hybrid

import com.holokenmod.creation.cage.GridSingleCageCreator
import com.holokenmod.grid.Grid
import java.util.concurrent.ThreadFactory

class BackTrackThreadFactory(
    private val grid: Grid,
    private val cageCreators: List<GridSingleCageCreator>,
    private val isPreSolved: Boolean,
    private val solutionListener: BackTrackSolutionListener
) : ThreadFactory {
    override fun newThread(r: Runnable): Thread {
        return BackTrackThread(r, createGrid(), cageCreators, isPreSolved, solutionListener)
    }

    private fun createGrid(): Grid {
        return grid.copyEmpty()
    }
}