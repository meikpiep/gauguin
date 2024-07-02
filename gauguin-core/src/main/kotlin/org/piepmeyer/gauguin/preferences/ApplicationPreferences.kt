package org.piepmeyer.gauguin.preferences

import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage

interface ApplicationPreferences {
    var theme: Theme

    fun maximumCellSizeInDP(): Int

    var gridTakesRemainingSpaceIfNecessary: Boolean

    fun showDupedDigits(): Boolean

    fun setShowOperators(showOperators: Boolean)

    fun addPencilsAtStart(): Boolean

    fun fillSingleCagesAtStart(): Boolean

    fun removePencils(): Boolean

    var operations: GridCageOperation
    var singleCageUsage: SingleCageUsage
    var difficultySetting: DifficultySetting
    var digitSetting: DigitSetting
    var numeralSystem: NumeralSystem

    var show3x3Pencils: Boolean

    fun newUserCheck(): Boolean

    fun deactivateNewUserCheck()

    var gridWidth: Int
    var gridHeigth: Int
    var squareOnlyGrid: Boolean

    fun showBadMaths(): Boolean

    var useFastFinishingMode: Boolean

    fun showOperators(): Boolean

    val gameVariant: GameOptionsVariant

    fun showFullscreen(): Boolean

    fun keepScreenOn(): Boolean

    fun showTimer(): Boolean

    fun clear()
}
