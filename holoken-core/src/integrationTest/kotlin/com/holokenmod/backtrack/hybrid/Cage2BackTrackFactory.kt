package com.holokenmod.backtrack.hybrid

import com.holokenmod.GridSolver
import io.kotest.datatest.IsStableType

@IsStableType
class Cage2BackTrackFactory : SolverFactory {
    override fun createSolver(): GridSolver {
        return MathDokuCage2BackTrackSolver()
    }

    override fun toString(): String {
        return "cage2BackTrack"
    }
}
