package org.piepmeyer.gauguin.options

import org.piepmeyer.gauguin.difficulty.GameDifficulty

enum class DifficultySetting(val gameDifficulty: GameDifficulty?) {
    ANY(null),
    VERY_EASY(GameDifficulty.VERY_EASY),
    EASY(GameDifficulty.EASY),
    MEDIUM(GameDifficulty.MEDIUM),
    HARD(GameDifficulty.HARD),
    EXTREME(GameDifficulty.EXTREME);
}
