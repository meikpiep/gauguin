package com.holokenmod.options

import android.content.SharedPreferences
import com.holokenmod.Theme
import org.apache.commons.lang3.EnumUtils

class ApplicationPreferences {
    var prefereneces: SharedPreferences? = null
        private set
    val theme: Theme
        get() {
            val themePref = prefereneces!!.getString("theme", Theme.LIGHT.name)
            return EnumUtils.getEnum(Theme::class.java, themePref, Theme.LIGHT)
        }

    fun setPreferenceManager(preferences: SharedPreferences?) {
        prefereneces = preferences
    }

    fun showDupedDigits(): Boolean {
        return prefereneces!!.getBoolean("duplicates", true)
    }

    private fun showBadMaths(): Boolean {
        return prefereneces!!.getBoolean("badmaths", true)
    }

    private fun showOperators(): Boolean {
        return prefereneces!!.getBoolean("showOperators", true)
    }

    fun setShowOperators(showOperators: Boolean) {
        prefereneces!!.edit()
            .putBoolean("showOperators", showOperators)
            .commit()
    }

    fun removePencils(): Boolean {
        return prefereneces!!.getBoolean("removepencils", false)
    }

    var operations: GridCageOperation
        get() {
            val operations = prefereneces!!.getString("operations", GridCageOperation.OPERATIONS_ALL.name)
            return EnumUtils.getEnum(
                GridCageOperation::class.java,
                operations,
                GridCageOperation.OPERATIONS_ALL
            )
        }
        set(operations) {
            prefereneces!!.edit()
                .putString("operations", operations.name)
                .commit()
        }
    var singleCageUsage: SingleCageUsage
        get() {
            val usage = prefereneces!!.getString("singlecages", SingleCageUsage.FIXED_NUMBER.name)
            return SingleCageUsage.valueOf(usage!!)
        }
        set(singleCageUsage) {
            prefereneces!!.edit()
                .putString("singlecages", singleCageUsage.name)
                .commit()
        }
    var difficultySetting: DifficultySetting
        get() {
            val usage = prefereneces!!.getString("difficulty", DifficultySetting.ANY.name)
            return EnumUtils.getEnum(DifficultySetting::class.java, usage, DifficultySetting.ANY)
        }
        set(difficultySetting) {
            prefereneces!!.edit()
                .putString("difficulty", difficultySetting.name)
                .commit()
        }
    var digitSetting: DigitSetting
        get() {
            val usage = prefereneces!!.getString("digits", DigitSetting.FIRST_DIGIT_ONE.name)
            return EnumUtils.getEnum(DigitSetting::class.java, usage, DigitSetting.FIRST_DIGIT_ONE)
        }
        set(digitSetting) {
            prefereneces!!.edit()
                .putString("digits", digitSetting.name)
                .commit()
        }

    fun show3x3Pencils(): Boolean {
        return prefereneces!!.getBoolean("pencil3x3", true)
    }

    fun newUserCheck(): Boolean {
        val new_user = prefereneces!!.getBoolean("newuser", true)
        if (new_user) {
            val prefeditor = prefereneces!!.edit()
            prefeditor.putBoolean("newuser", false)
            prefeditor.commit()
        }
        return new_user
    }

    var gridWidth: Int
        get() = prefereneces!!.getInt("gridWidth", 6)
        set(width) {
            prefereneces!!.edit()
                .putInt("gridWidth", width)
                .commit()
        }
    var gridHeigth: Int
        get() = prefereneces!!.getInt("gridHeigth", 6)
        set(heigth) {
            prefereneces!!.edit()
                .putInt("gridHeigth", heigth)
                .commit()
        }
    var squareOnlyGrid: Boolean
        get() = prefereneces!!.getBoolean("squareOnlyGrid", true)
        set(squareOnly) {
            prefereneces!!.edit()
                .putBoolean("squareOnlyGrid", squareOnly)
                .commit()
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

    companion object {
        val instance = ApplicationPreferences()
    }
}