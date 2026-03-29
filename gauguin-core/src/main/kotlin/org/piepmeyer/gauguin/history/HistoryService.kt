package org.piepmeyer.gauguin.history

import org.piepmeyer.gauguin.grid.Grid

class HistoryService(
    private val historyEvents: MutableList<HistoryEvent> = mutableListOf(),
) {
    fun history(): History = History(historyEvents)

    fun gridHasBeenSolved(grid: Grid) {
        historyEvents +=
            HistoryEvent.GridSolved(GridHistoryInfo.of(grid))
    }

    fun gridHasBeenEndedUnsolved(grid: Grid) {
        historyEvents +=
            HistoryEvent.GridUnsolved(GridHistoryInfo.of(grid))
    }
}
