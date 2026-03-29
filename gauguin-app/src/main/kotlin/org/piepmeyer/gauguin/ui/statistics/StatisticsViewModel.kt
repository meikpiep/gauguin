package org.piepmeyer.gauguin.ui.statistics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.history.History
import org.piepmeyer.gauguin.history.HistoryService
import org.piepmeyer.gauguin.history.HistoryView

sealed class HistoryState {
    class HistoryLoading : HistoryState()

    class HistoryEmpty : HistoryState()

    class HistoryLoaded(
        val history: History,
        val view: HistoryView,
    ) : HistoryState()
}

class StatisticsViewModel(
    @InjectedParam val historyService: HistoryService,
) : ViewModel(),
    KoinComponent {
    private val mutableHistoryState = MutableStateFlow<HistoryState>(HistoryState.HistoryLoading())

    val historyState: StateFlow<HistoryState> = mutableHistoryState.asStateFlow()

    init {
        val history = historyService.history()

        mutableHistoryState.value =
            if (history.events.isEmpty()) {
                HistoryState.HistoryEmpty()
            } else {
                HistoryState.HistoryLoaded(history, history)
            }
    }

    fun viewOnlyOneGridSize(size: GridSize) {
        val historyState = mutableHistoryState.value

        if (historyState is HistoryState.HistoryLoaded) {
            mutableHistoryState.value = HistoryState.HistoryLoaded(historyState.history, historyState.history.view(size))
        }
    }

    fun viewAllSizes() {
        val historyState = mutableHistoryState.value

        if (historyState is HistoryState.HistoryLoaded) {
            mutableHistoryState.value = HistoryState.HistoryLoaded(historyState.history, historyState.history)
        }
    }
}
