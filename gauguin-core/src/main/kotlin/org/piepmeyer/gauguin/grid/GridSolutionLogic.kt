package org.piepmeyer.gauguin.grid

interface GridSolutionLogic {
    fun isSolutionCheckable(): Boolean

    fun isValidSolution(): Boolean

    fun solveViaSolution()
}
