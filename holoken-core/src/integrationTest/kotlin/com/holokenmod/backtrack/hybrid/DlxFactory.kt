package com.holokenmod.backtrack.hybrid

import com.holokenmod.GridSolver
import com.holokenmod.creation.dlx.MathDokuDLXSolver

class DlxFactory {
    fun createSolver(): GridSolver {
        return MathDokuDLXSolver()
    }
}
