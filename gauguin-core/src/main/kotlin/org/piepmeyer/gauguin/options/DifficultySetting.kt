package org.piepmeyer.gauguin.options

enum class DifficultySetting {
    VERY_EASY,
    EASY,
    MEDIUM,
    HARD,
    EXTREME, ;

    companion object {
        fun all(): Set<DifficultySetting> = entries.toSet()
    }
}
