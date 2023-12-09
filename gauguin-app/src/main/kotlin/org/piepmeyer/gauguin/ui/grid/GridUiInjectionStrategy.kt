package org.piepmeyer.gauguin.ui.grid

import org.piepmeyer.gauguin.grid.GridCell

interface GridUiInjectionStrategy {
    fun showBadMaths(): Boolean

    fun cellClicked(cell: GridCell)

    fun isInFastFinishingMode(): Boolean

    fun showOperators(): Boolean
}
