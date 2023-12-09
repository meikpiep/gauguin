package org.piepmeyer.gauguin.options

import kotlinx.serialization.Serializable

@Serializable
data class GameOptionsVariant(
    var showOperators: Boolean,
    var cageOperation: GridCageOperation,
    var digitSetting: DigitSetting,
    var difficultySetting: DifficultySetting,
    var singleCageUsage: SingleCageUsage,
) {
    companion object {
        fun createClassic(digitSetting: DigitSetting = DigitSetting.FIRST_DIGIT_ONE): GameOptionsVariant {
            return GameOptionsVariant(
                cageOperation = GridCageOperation.OPERATIONS_ALL,
                showOperators = true,
                digitSetting = digitSetting,
                singleCageUsage = SingleCageUsage.FIXED_NUMBER,
                difficultySetting = DifficultySetting.ANY,
            )
        }
    }
}
