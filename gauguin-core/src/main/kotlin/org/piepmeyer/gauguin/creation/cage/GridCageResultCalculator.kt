package org.piepmeyer.gauguin.creation.cage

import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import kotlin.math.max
import kotlin.math.min

class GridCageResultCalculator(private val cage: GridCage) {
    fun calculateResultFromAction(): Int {
        if (cage.action == GridCageAction.ACTION_DIVIDE || cage.action == GridCageAction.ACTION_SUBTRACT) {
            check(cage.cells.size == 2) {
                "Each cage with action ${cage.action} is required to have exactly 2 cells, but ${cage.cells.size} were found."
            }
        }

        if (cage.action == GridCageAction.ACTION_ADD) {
            var total = 0
            for (cell in cage.cells) {
                total += cell.value
            }
            return total
        }

        if (cage.action == GridCageAction.ACTION_MULTIPLY) {
            var total = 1
            for (cell in cage.cells) {
                total *= cell.value
            }
            return total
        }

        val higher = max(cage.cells[0].value, cage.cells[1].value)
        val lower = min(cage.cells[0].value, cage.cells[1].value)

        return if (cage.action == GridCageAction.ACTION_DIVIDE) {
            if (lower == 0) {
                return 0
            }
            higher / lower
        } else {
            higher - lower
        }
    }
}
