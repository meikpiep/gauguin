package com.holokenmod.options

data class GameOptionsVariant (
    var showOperators: Boolean,
    var cageOperation: GridCageOperation,
    var digitSetting: DigitSetting,
    var difficultySetting: DifficultySetting,
    var singleCageUsage: SingleCageUsage,
    val showBadMaths: Boolean = false
){

    companion object {
        @JvmStatic
        @JvmOverloads
        fun createClassic(digitSetting: DigitSetting = DigitSetting.FIRST_DIGIT_ONE): GameOptionsVariant {
            return GameOptionsVariant(
            cageOperation = GridCageOperation.OPERATIONS_ALL,
            showOperators = true,
            digitSetting = digitSetting,
            singleCageUsage = SingleCageUsage.FIXED_NUMBER,
            showBadMaths = true,
            difficultySetting = DifficultySetting.ANY,
            )
        }
    }
}