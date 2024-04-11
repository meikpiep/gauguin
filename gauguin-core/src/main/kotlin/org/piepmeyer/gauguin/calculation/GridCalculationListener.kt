package org.piepmeyer.gauguin.calculation

interface GridCalculationListener {
    fun startingCurrentGridCalculation()

    fun startingNextGridCalculation()

    fun currentGridCalculated()

    fun nextGridCalculated()
}
