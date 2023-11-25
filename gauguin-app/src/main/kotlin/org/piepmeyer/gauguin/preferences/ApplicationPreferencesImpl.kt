package org.piepmeyer.gauguin.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.options.CurrentGameOptionsVariant
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.SingleCageUsage

class ApplicationPreferencesImpl(
    val preferences: SharedPreferences
): ApplicationPreferences {
    override val theme: Theme
        get() {
            val themePref = preferences.getString("theme", Theme.DARK.name)!!
            return enumValueOf(themePref)
        }

    override fun showDupedDigits(): Boolean {
        return preferences.getBoolean("duplicates", true)
    }

    override fun showBadMaths(): Boolean {
        return preferences.getBoolean("badmaths", true)
    }

    private fun showOperators(): Boolean {
        return preferences.getBoolean("showOperators", true)
    }

    override fun setShowOperators(showOperators: Boolean) {
        preferences.edit {
            putBoolean("showOperators", showOperators)
        }
    }

    override fun addPencilsAtStart(): Boolean {
        return preferences.getBoolean("pencilatstart", false)
    }

    override fun removePencils(): Boolean {
        return preferences.getBoolean("removepencils", true)
    }

    override fun useFastFinishingMode(): Boolean {
        return preferences.getBoolean("useFastFinishingMode", false)
    }

    override var operations: GridCageOperation
        get() {
            val operations = preferences.getString("operations", GridCageOperation.OPERATIONS_ALL.name)!!
            return enumValueOf(operations)
        }
        set(operations) {
            preferences.edit {
                putString("operations", operations.name)
            }
        }

    override var singleCageUsage: SingleCageUsage
        get() {
            val usage = preferences.getString("singlecages", SingleCageUsage.FIXED_NUMBER.name)
            return SingleCageUsage.valueOf(usage!!)
        }
        set(singleCageUsage) {
            preferences.edit {
                putString("singlecages", singleCageUsage.name)
            }
        }

    override var difficultySetting: DifficultySetting
        get() {
            val usage = preferences.getString("difficulty", DifficultySetting.ANY.name)!!
            return enumValueOf(usage)
        }
        set(difficultySetting) {
            preferences.edit {
                putString("difficulty", difficultySetting.name)
            }
        }

    override var digitSetting: DigitSetting
        get() {
            val usage = preferences.getString("digits", DigitSetting.FIRST_DIGIT_ONE.name)!!
            return enumValueOf(usage)
        }
        set(digitSetting) {
            preferences.edit {
                putString("digits", digitSetting.name)
            }
        }

    override fun show3x3Pencils(): Boolean {
        return preferences.getBoolean("pencil3x3", false)
    }

    override fun newUserCheck(): Boolean {
        return preferences.getBoolean("newuser", true)
    }

    override fun deactivateNewUserCheck() {
        preferences.edit {
            putBoolean("newuser", false)
        }
    }

    override var gridWidth: Int
        get() = preferences.getInt("gridWidth", 6)
        set(width) {
            preferences.edit {
                putInt("gridWidth", width)
            }
        }

    override var gridHeigth: Int
        get() = preferences.getInt("gridHeigth", 6)
        set(heigth) {
            preferences.edit {
                putInt("gridHeigth", heigth)
            }
        }

    override var squareOnlyGrid: Boolean
        get() = preferences.getBoolean("squareOnlyGrid", true)
        set(squareOnly) {
            preferences.edit {
                putBoolean("squareOnlyGrid", squareOnly)
            }
        }

    override fun loadGameVariant() {
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
            singleCageUsage
        )
    }
}