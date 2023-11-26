package org.piepmeyer.gauguin.ui.grid

import org.piepmeyer.gauguin.grid.GridCell

interface GridUiInjectionStrategy {
    fun showBadMaths(): Boolean

    fun selectCell(cell: GridCell)
}
