package org.piepmeyer.gauguin.options

data class GameOptionsVariant(
    var showOperators: Boolean,
    var cageOperation: GridCageOperation,
    var digitSetting: DigitSetting,
    var difficultySetting: DifficultySetting,
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
                difficultySetting = DifficultySetting.ANY,
                numeralSystem = NumeralSystem.Decimal,
            )
    }
}
