package org.piepmeyer.gauguin.options

import org.piepmeyer.gauguin.Theme

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
    var gridWidth: Int
    var gridHeigth: Int
    var squareOnlyGrid: Boolean
}
