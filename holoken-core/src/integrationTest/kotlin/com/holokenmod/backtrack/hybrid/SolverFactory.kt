package com.holokenmod.backtrack.hybrid

import com.holokenmod.GridSolver

interface SolverFactory {
    fun createSolver(): GridSolver
}
