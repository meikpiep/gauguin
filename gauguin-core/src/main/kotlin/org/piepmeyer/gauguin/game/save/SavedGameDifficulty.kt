package org.piepmeyer.gauguin.game.save

import org.piepmeyer.gauguin.difficulty.GameDifficulty

enum class SavedGameDifficulty {
    ANY,
    VERY_EASY,
    EASY,
    MEDIUM,
    HARD,
    EXTREME, ;

    fun toGameDifficulties(): Set<GameDifficulty> =
        when (this) {
            ANY -> GameDifficulty.all()
            VERY_EASY -> setOf(GameDifficulty.VERY_EASY)
            EASY -> setOf(GameDifficulty.EASY)
            MEDIUM -> setOf(GameDifficulty.MEDIUM)
            HARD -> setOf(GameDifficulty.HARD)
            EXTREME -> setOf(GameDifficulty.EXTREME)
        }
}
