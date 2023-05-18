package com.holokenmod.options

import android.content.SharedPreferences
import androidx.core.content.edit
import com.holokenmod.Theme

class ApplicationPreferences(
    val preferences: SharedPreferences
) {
    val theme: Theme
        get() {
            val themePref = preferences.getString("theme", Theme.LIGHT.name)!!
            return enumValueOf(themePref)
        }

    fun showDupedDigits(): Boolean {
        return preferences.getBoolean("duplicates", true)
    }

    private fun showBadMaths(): Boolean {
        return preferences.getBoolean("badmaths", true)
    }

    private fun showOperators(): Boolean {
        return preferences.getBoolean("showOperators", true)
    }

    fun setShowOperators(showOperators: Boolean) {
        preferences.edit {
            putBoolean("showOperators", showOperators)
        }
    }

    fun removePencils(): Boolean {
        return preferences.getBoolean("removepencils", false)
    }

    var operations: GridCageOperation
        get() {
            val operations = preferences.getString("operations", GridCageOperation.OPERATIONS_ALL.name)!!
            return enumValueOf(operations)
        }
        set(operations) {
            preferences.edit {
                putString("operations", operations.name)
            }
        }

    var singleCageUsage: SingleCageUsage
        get() {
            val usage = preferences.getString("singlecages", SingleCageUsage.FIXED_NUMBER.name)
            return SingleCageUsage.valueOf(usage!!)
        }
        set(singleCageUsage) {
            preferences.edit {
                putString("singlecages", singleCageUsage.name)
            }
        }

    var difficultySetting: DifficultySetting
        get() {
            val usage = preferences.getString("difficulty", DifficultySetting.ANY.name)!!
            return enumValueOf(usage)
        }
        set(difficultySetting) {
            preferences.edit {
                putString("difficulty", difficultySetting.name)
            }
        }

    var digitSetting: DigitSetting
        get() {
            val usage = preferences.getString("digits", DigitSetting.FIRST_DIGIT_ONE.name)!!
            return enumValueOf(usage)
        }
        set(digitSetting) {
            preferences.edit {
                putString("digits", digitSetting.name)
            }
        }

    fun show3x3Pencils(): Boolean {
        return preferences.getBoolean("pencil3x3", true)
    }

    fun newUserCheck(): Boolean {
        val newUser = preferences.getBoolean("newuser", true)
        if (newUser) {
            preferences.edit {
                putBoolean("newuser", false)
            }
        }
        return newUser
    }

    var gridWidth: Int
        get() = preferences.getInt("gridWidth", 6)
        set(width) {
            preferences.edit {
                putInt("gridWidth", width)
            }
        }

    var gridHeigth: Int
        get() = preferences.getInt("gridHeigth", 6)
        set(heigth) {
            preferences.edit {
                putInt("gridHeigth", heigth)
            }
        }

    var squareOnlyGrid: Boolean
        get() = preferences.getBoolean("squareOnlyGrid", true)
        set(squareOnly) {
            preferences.edit {
                putBoolean("squareOnlyGrid", squareOnly)
            }
        }

    fun loadGameVariant() {
        CurrentGameOptionsVariant.instance = loadIntoGameVariant()
    }

    val gameVariant: GameOptionsVariant
        get() = loadIntoGameVariant()

    private fun loadIntoGameVariant(): GameOptionsVariant {
        return GameOptionsVariant(
            showOperators(),
            operations,
            digitSetting,
            difficultySetting,
            singleCageUsage,
            showBadMaths()
        )
    }
}