package com.holokenmod.backtrack.hybrid

import com.holokenmod.creation.cage.GridSingleCageCreator
import com.holokenmod.grid.Grid

class BackTrackThread(
    r: Runnable?,
    val grid: Grid?,
    val cageCreators: List<GridSingleCageCreator>,
    val isPreSolved: Boolean,
    val solutionListener: BackTrackSolutionListener
) : Thread(r)