package org.piepmeyer.gauguin.ui.grid

import org.piepmeyer.gauguin.grid.GridCell

class GridUiInjectionEditModeStrategy : GridUiInjectionStrategy {
    override fun showBadMaths() = false

    override fun selectCell(cell: GridCell) {
        //Nothing to do here as we only draw a preview of thie view.
    }
}
