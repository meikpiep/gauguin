package org.piepmeyer.gauguin.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage

class ApplicationPreferencesImpl(
    private val androidContext: Context,
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(androidContext),
) : KoinComponent,
    ApplicationPreferences {
    override fun clear() {
        preferences.edit { clear() }
    }

    override var theme: Theme
        get() {
            val themePref = preferences.getString("theme", null)
            return themePref?.let {
                try {
                    enumValueOf<Theme>(it)
                } catch (_: IllegalArgumentException) {
                    return Theme.DARK
                }
            } ?: Theme.DARK
        }
        set(value) {
            preferences.edit {
                putString("theme", value.name)
            }
        }

    override fun maximumCellSizeInDP(): Int = preferences.getInt("maximumCellSize", 72)

    override var gridTakesRemainingSpaceIfNecessary: Boolean
        get() {
            return preferences.getBoolean("gridTakesRemainingSpaceIfNecessary", true)
        }
        set(value) {
            preferences.edit {
                putBoolean("gridTakesRemainingSpaceIfNecessary", value)
            }
        }

    override fun showDupedDigits(): Boolean = preferences.getBoolean("duplicates", true)

    override fun showBadMaths(): Boolean = preferences.getBoolean("badmaths", true)

    override fun showOperators(): Boolean = preferences.getBoolean("showOperators", true)

    override fun setShowOperators(showOperators: Boolean) {
        preferences.edit {
            putBoolean("showOperators", showOperators)
        }
    }

    override fun addPencilsAtStart(): Boolean = preferences.getBoolean("pencilatstart", false)

    override fun fillSingleCagesAtStart(): Boolean = preferences.getBoolean("fillSingleCagesAtStart", false)

    override fun removePencils(): Boolean = preferences.getBoolean("removepencils", true)

    override var useFastFinishingMode: Boolean
        get() {
            return preferences.getBoolean("useFastFinishingMode", false)
        }
        set(value) {
            preferences.edit {
                putBoolean("useFastFinishingMode", value)
            }
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

    override var numeralSystem: NumeralSystem
        get() {
            val system = preferences.getString("numeralSystem", NumeralSystem.Decimal.name)!!
            return enumValueOf(system)
        }
        set(numeralSystem) {
            preferences.edit {
                putString("numeralSystem", numeralSystem.name)
            }
        }

    override var show3x3Pencils: Boolean
        get() {
            return preferences.getBoolean("pencil3x3", false)
        }
        set(value) {
            preferences.edit {
                putBoolean("pencil3x3", value)
            }
        }

    override fun newUserCheck(): Boolean = preferences.getBoolean("newuser", true)

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

    override val gameVariant: GameOptionsVariant
        get() = loadIntoGameVariant()

    override fun showFullscreen(): Boolean = preferences.getBoolean("showfullscreen", true)

    override fun keepScreenOn(): Boolean = preferences.getBoolean("keepscreenon", true)

    override fun showTimer(): Boolean = preferences.getBoolean("showtimer", true)

    private fun loadIntoGameVariant(): GameOptionsVariant =
        GameOptionsVariant(
            showOperators(),
            operations,
            digitSetting,
            difficultySetting,
            singleCageUsage,
            numeralSystem,
        )

    fun migrateGridSizeFromTwoToThree() {
        if (androidContext.resources.getBoolean(R.bool.debuggable)) {
            return
        }

        if (gridWidth == 2) {
            gridWidth = 3
        }

        if (gridHeigth == 2) {
            gridHeigth = 3
        }
    }
}
