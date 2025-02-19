package org.piepmeyer.gauguin.options

import org.piepmeyer.gauguin.difficulty.GameDifficulty

data class GameOptionsVariant(
    var showOperators: Boolean,
    var cageOperation: GridCageOperation,
    var digitSetting: DigitSetting,
    var difficultiesSetting: Set<GameDifficulty>,
    var singleCageUsage: SingleCageUsage,
    var numeralSystem: NumeralSystem,
) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun createClassic(digitSetting: DigitSetting = DigitSetting.FIRST_DIGIT_ONE): GameOptionsVariant =
            GameOptionsVariant(
                cageOperation = GridCageOperation.OPERATIONS_ALL,
                showOperators = true,
                digitSetting = digitSetting,
                singleCageUsage = SingleCageUsage.FIXED_NUMBER,
                difficultiesSetting = GameDifficulty.all(),
                numeralSystem = NumeralSystem.Decimal,
            )
    }
}
