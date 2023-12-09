package org.piepmeyer.gauguin.preferences

import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.SingleCageUsage

interface ApplicationPreferences {

    val theme: Theme
    fun showDupedDigits(): Boolean
    fun setShowOperators(showOperators: Boolean)

    fun addPencilsAtStart(): Boolean
    fun removePencils(): Boolean
    var operations: GridCageOperation
    var singleCageUsage: SingleCageUsage
    var difficultySetting: DifficultySetting
    var digitSetting: DigitSetting
    fun show3x3Pencils(): Boolean
    fun newUserCheck(): Boolean
    fun deactivateNewUserCheck()
    var gridWidth: Int
    var gridHeigth: Int
    var squareOnlyGrid: Boolean
    fun loadGameVariant()
    fun showBadMaths(): Boolean
    fun useFastFinishingMode(): Boolean
    fun showOperators(): Boolean
}
