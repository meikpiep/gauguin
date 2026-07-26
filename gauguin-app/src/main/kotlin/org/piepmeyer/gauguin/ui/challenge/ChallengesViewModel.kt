package org.piepmeyer.gauguin.ui.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.challenge.Challenges
import org.piepmeyer.gauguin.grid.Grid

sealed class ChallengeState {
    class ChallengePair(
        val zenGrid: Grid,
        val chruncherGrid: Grid,
    ) : ChallengeState()

    class ChallengeLoading : ChallengeState()
}

class ChallengesViewModel :
    ViewModel(),
    KoinComponent {
    var gridSize = 5

    val challenges = Challenges()

    private val mutableGrids =
        MutableStateFlow<ChallengeState>(ChallengeState.ChallengeLoading())

    val grids: StateFlow<ChallengeState> = mutableGrids.asStateFlow()

    init {
        loadGrids()
    }

    fun changeSize(size: Int) {
        if (gridSize == size) {
            return
        }

        gridSize = size

        loadGrids()
    }

    private fun loadGrids() {
        viewModelScope.launch {
            val zenDeferred = viewModelScope.async { challenges.zenChallenge(gridSize) }
            val chruncherDeferred = viewModelScope.async { challenges.chruncherChallenge(gridSize) }

            mutableGrids.value =
                ChallengeState.ChallengePair(
                    zenDeferred.await(),
                    chruncherDeferred.await(),
                )
        }
    }
}
