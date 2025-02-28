package org.piepmeyer.gauguin.game.save

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage

@Serializable
data class SavedGameOptionsVariant(
    var showOperators: Boolean,
    var cageOperation: GridCageOperation,
    var digitSetting: DigitSetting,
    var difficultiesSetting: Set<DifficultySetting> = emptySet(),
    var singleCageUsage: SingleCageUsage,
    var numeralSystem: NumeralSystem,
) {
    fun toOptionsVariant(): GameOptionsVariant =
        GameOptionsVariant(
            showOperators = showOperators,
            cageOperation = cageOperation,
            digitSetting = digitSetting,
            difficultiesSetting = difficultiesSetting,
            singleCageUsage = singleCageUsage,
            numeralSystem = numeralSystem,
        )

    companion object {
        fun fromOptionsVariant(options: GameOptionsVariant): SavedGameOptionsVariant =
            SavedGameOptionsVariant(
                showOperators = options.showOperators,
                cageOperation = options.cageOperation,
                digitSetting = options.digitSetting,
                difficultiesSetting = options.difficultiesSetting,
                singleCageUsage = options.singleCageUsage,
                numeralSystem = options.numeralSystem,
            )
    }
}
