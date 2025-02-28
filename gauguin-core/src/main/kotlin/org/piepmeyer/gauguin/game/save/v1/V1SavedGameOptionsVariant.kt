package org.piepmeyer.gauguin.game.save.v1

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.game.save.SavedDifficultySetting
import org.piepmeyer.gauguin.game.save.SavedGameOptionsVariant
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage

@Serializable
data class V1SavedGameOptionsVariant(
    var showOperators: Boolean,
    var cageOperation: GridCageOperation,
    var digitSetting: DigitSetting,
    var difficultySetting: SavedDifficultySetting?,
    var difficultiesSetting: Set<DifficultySetting> = emptySet(),
    var singleCageUsage: SingleCageUsage,
    var numeralSystem: NumeralSystem,
) {
    fun toSavedGameOptionsVariant(): SavedGameOptionsVariant =
        SavedGameOptionsVariant.fromOptionsVariant(
            GameOptionsVariant(
                showOperators = showOperators,
                cageOperation = cageOperation,
                digitSetting = digitSetting,
                difficultiesSetting = difficultySetting!!.toDifficultySetting(),
                singleCageUsage = singleCageUsage,
                numeralSystem = numeralSystem,
            ),
        )
}
