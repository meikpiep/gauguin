package org.piepmeyer.gauguin.options

enum class DifficultySetting {
    VERY_EASY,
    EASY,
    MEDIUM,
    HARD,
    EXTREME, ;

    companion object {
        fun all(): Set<DifficultySetting> = entries.toSet()

        fun isApplicableToSingleSelection(difficultiesSetting: Set<DifficultySetting>): Boolean =
            difficultiesSetting == all() || difficultiesSetting.size == 1
    }
}
