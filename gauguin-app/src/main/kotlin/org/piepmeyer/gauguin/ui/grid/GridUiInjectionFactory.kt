package org.piepmeyer.gauguin.ui.grid

class GridUiInjectionFactory {
    companion object {
        fun createStreategy(gridView: GridUI): GridUiInjectionStrategy =
            if (gridView.isInEditMode) {
                GridUiInjectionEditModeStrategy()
            } else {
                GridUiInjectionDefaultStrategy(gridView)
            }
    }
}
