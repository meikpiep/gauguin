package com.holokenmod.options

import com.holokenmod.Theme

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
