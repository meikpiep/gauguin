package org.piepmeyer.gauguin.game.save

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.difficulty.GameDifficulty
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
    var difficultySetting: SavedGameDifficulty?,
    var difficultiesSetting: Set<GameDifficulty> = emptySet(),
    var singleCageUsage: SingleCageUsage,
    var numeralSystem: NumeralSystem,
) {
    fun toOptionsVariant(): GameOptionsVariant =
        GameOptionsVariant(
            showOperators = showOperators,
            cageOperation = cageOperation,
            digitSetting = digitSetting,
            difficultiesSetting = difficultiesSetting.ifEmpty { difficultySetting!!.toGameDifficulties() },
            singleCageUsage = singleCageUsage,
            numeralSystem = numeralSystem,
        )

    companion object {
        fun fromOptionsVariant(options: GameOptionsVariant): SavedGameOptionsVariant =
            SavedGameOptionsVariant(
                showOperators = options.showOperators,
                cageOperation = options.cageOperation,
                digitSetting = options.digitSetting,
                difficultySetting = null,
                difficultiesSetting = options.difficultiesSetting,
                singleCageUsage = options.singleCageUsage,
                numeralSystem = options.numeralSystem,
            )
    }
}
