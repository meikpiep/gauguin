package org.piepmeyer.gauguin.ui.challenge

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.challenge.Challenges
import org.piepmeyer.gauguin.grid.Grid

data class ChallengePair(
    val zenGrid: Grid,
    val chruncherGrid: Grid,
)

class ChallengesViewModel :
    ViewModel(),
    KoinComponent {
    var gridSize = 5

    val challenges = Challenges()

    private val mutableGrids =
        MutableStateFlow(
            ChallengePair(
                challenges.zenChallenge(gridSize),
                challenges.chruncherChallenge(gridSize),
            ),
        )

    val grids: StateFlow<ChallengePair> = mutableGrids.asStateFlow()

    fun changeSize(size: Int) {
        if (gridSize == size) {
            return
        }

        gridSize = size

        mutableGrids.value =
            ChallengePair(
                challenges.zenChallenge(gridSize),
                challenges.chruncherChallenge(gridSize),
            )
    }
}
