package com.holokenmod.backtrack.hybrid

import com.holokenmod.GridSolver
import com.srlee.dlx.MathDokuDLXSolver
import io.kotest.datatest.IsStableType

@IsStableType
class DlxFactory : SolverFactory {
    override fun createSolver(): GridSolver {
        return MathDokuDLXSolver()
    }

    override fun toString(): String {
        return "DLX"
    }
}
