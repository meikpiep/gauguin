package org.piepmeyer.gauguin.difficulty

enum class GameDifficulty {
    VERY_EASY,
    EASY,
    MEDIUM,
    HARD,
    EXTREME, ;

    companion object {
        fun all(): Set<GameDifficulty> = entries.toSet()
    }
}
