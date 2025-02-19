package org.piepmeyer.gauguin.game.save

import org.piepmeyer.gauguin.options.DifficultySetting

enum class SavedDifficultySetting {
    ANY,
    VERY_EASY,
    EASY,
    MEDIUM,
    HARD,
    EXTREME, ;

    fun toDifficultySetting(): Set<DifficultySetting> =
        when (this) {
            ANY -> DifficultySetting.all()
            VERY_EASY -> setOf(DifficultySetting.VERY_EASY)
            EASY -> setOf(DifficultySetting.EASY)
            MEDIUM -> setOf(DifficultySetting.MEDIUM)
            HARD -> setOf(DifficultySetting.HARD)
            EXTREME -> setOf(DifficultySetting.EXTREME)
        }
}
