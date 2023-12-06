package org.piepmeyer.gauguin.ui.grid

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class GridUiInjectionDefaultStrategy : GridUiInjectionStrategy, KoinComponent {
    private val game: Game by inject()
    private val applicationPreferences: ApplicationPreferences by inject()

    override fun showBadMaths() = applicationPreferences.showBadMaths()

    override fun cellClicked(cell: GridCell) {
        game.cellClicked(cell)
    }

    override fun isInFastFinishingMode() = game.isInFastFinishingMode()

    override fun showOperators() = applicationPreferences.showOperators()

    override fun numeralSystem() = game.grid.variant.options.numeralSystem
}
