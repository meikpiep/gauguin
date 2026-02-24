package org.piepmeyer.gauguin.ui.grid

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class GridUiInjectionDefaultStrategy(
    private val gridView: GridUI,
) : GridUiInjectionStrategy,
    KoinComponent {
    private val game: Game by inject()
    private val applicationPreferences: ApplicationPreferences by inject()

    override fun showBadMaths() = applicationPreferences.showBadMaths()

    override fun cellClicked(cell: GridCell) {
        game.cellClicked(cell)
    }

    override fun isInFastFinishingMode() = game.isInFastFinishingMode()

    override fun showOperators() = gridView.grid.variant.options.showOperators

    override fun numeralSystem() = gridView.grid.variant.options.numeralSystem

    override fun markDuplicatedInRowOrColumn() = applicationPreferences.showDupedDigits()

    override fun maximumCellSizeInDP(): Int = applicationPreferences.maximumCellSizeInDP()

    override fun useBroaderCageFrames(): Boolean = applicationPreferences.broaderCageFrames
}
