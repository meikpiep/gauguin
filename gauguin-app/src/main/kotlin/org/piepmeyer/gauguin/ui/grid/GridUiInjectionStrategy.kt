package org.piepmeyer.gauguin.ui.grid

import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.options.NumeralSystem

interface GridUiInjectionStrategy {
    fun showBadMaths(): Boolean

    fun cellClicked(cell: GridCell)

    fun isInFastFinishingMode(): Boolean

    fun numeralSystem(): NumeralSystem

    fun showOperators(): Boolean

    fun markDuplicatedInRowOrColumn(): Boolean
}
