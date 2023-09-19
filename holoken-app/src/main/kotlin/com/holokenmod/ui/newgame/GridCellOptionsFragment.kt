package com.holokenmod.ui.newgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.holokenmod.R
import com.holokenmod.databinding.NewGameOptionsFragmentBinding
import com.holokenmod.options.ApplicationPreferences
import com.holokenmod.options.CurrentGameOptionsVariant
import com.holokenmod.options.DifficultySetting
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameVariant
import com.holokenmod.options.GridCageOperation
import com.holokenmod.options.SingleCageUsage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GridCellOptionsFragment : Fragment(R.layout.new_game_options_fragment), KoinComponent {
    private val applicationPreferences: ApplicationPreferences by inject()
    private var gridPreviewHolder: GridPreviewHolder? = null
    private lateinit var binding: NewGameOptionsFragmentBinding

    fun setGridPreviewHolder(gridPreviewHolder: GridPreviewHolder) {
        this.gridPreviewHolder = gridPreviewHolder
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewGameOptionsFragmentBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createDifficultyChips()
        createSingleCellUsageChips()
        createOperationsChips()
        createDigitsChips()

        val tabs = binding.newGameOptionsTablayout
        tabs.addOnTabSelectedListener(object: OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    updateVisibility(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) { }

                override fun onTabReselected(tab: TabLayout.Tab) { }
            }
        )
        updateVisibility(tabs.getTabAt(0)!!)

        binding.showOperationsSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            showOperationsChanged(isChecked)
        }
        binding.showOperationsSwitch.isChecked =
            CurrentGameOptionsVariant.instance.showOperators
    }

    private fun updateVisibility(tab: TabLayout.Tab) {
        val basicMode = if (tab.position == 0) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        val advancedMode = if (tab.position != 0) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        binding.difficultyLabel.visibility = basicMode
        binding.difficultyChipGroup.visibility = basicMode
        binding.singleCellUsageLabel.visibility = advancedMode
        binding.singleCellUsageChipGroup.visibility = advancedMode
        binding.operationsLabel.visibility = basicMode
        binding.operationsChipGroup.visibility = basicMode
        binding.digitsLabel.visibility = advancedMode
        binding.digitsChipGroup.visibility = advancedMode
        binding.showOperationsSwitch.visibility = advancedMode
    }

    private fun createSingleCellUsageChips() {
        val singleCellUsageIdMap = mapOf(
            binding.chipSingleCagesDynamic.id to SingleCageUsage.DYNAMIC,
            binding.chipSingleCagesFixedNumber.id to SingleCageUsage.FIXED_NUMBER,
            binding.chipSingleCagesNoSingleCages.id to SingleCageUsage.NO_SINGLE_CAGES,
        )

        binding.singleCellUsageChipGroup.setOnCheckedStateChangeListener { _, _ ->
            val singleCageOption = singleCellUsageIdMap[binding.singleCellUsageChipGroup.checkedChipId]!!

            CurrentGameOptionsVariant.instance.singleCageUsage = singleCageOption
            applicationPreferences.singleCageUsage = singleCageOption
            gridPreviewHolder?.refreshGrid()
        }

        binding.singleCellUsageChipGroup.check(singleCellUsageIdMap.filterValues {
            it == CurrentGameOptionsVariant.instance.singleCageUsage
        }.keys.first())
    }

    private fun createOperationsChips() {
        val operationsIdMap = mapOf(
            binding.chipOperationsAll.id to GridCageOperation.OPERATIONS_ALL,
            binding.chipOperationsAdditionSubtraction.id to GridCageOperation.OPERATIONS_ADD_SUB,
            binding.chipOperationsAdditionMultiplication.id to GridCageOperation.OPERATIONS_ADD_MULT,
            binding.chipOperationsMultiplication.id to GridCageOperation.OPERATIONS_MULT,
        )

        binding.operationsChipGroup.setOnCheckedStateChangeListener { _, _ ->
            val operations = operationsIdMap[binding.operationsChipGroup.checkedChipId]!!

            CurrentGameOptionsVariant.instance.cageOperation = operations
            applicationPreferences.operations = operations
            gridPreviewHolder?.refreshGrid()
        }

        binding.operationsChipGroup.check(operationsIdMap.filterValues {
            it == CurrentGameOptionsVariant.instance.cageOperation
        }.keys.first())
    }

    private fun createDigitsChips() {
        val digitsIdMap = mapOf(
            binding.chipDigitsFromZero.id to DigitSetting.FIRST_DIGIT_ZERO,
            binding.chipDigitsFromOne.id to DigitSetting.FIRST_DIGIT_ONE,
            binding.chipDigitsPrimes.id to DigitSetting.PRIME_NUMBERS,
            binding.chipDigitsFibonacci.id to DigitSetting.FIBONACCI_SEQUENCE,
            binding.chipDigitsPadovan.id to DigitSetting.PADOVAN_SEQUENCE,
            binding.chipDigitsFromMinusTwo.id to DigitSetting.FIRST_DIGIT_MINUS_TWO,
            binding.chipDigitsFromMinusFive.id to DigitSetting.FIRST_DIGIT_MINUS_FIVE,
        )

        binding.digitsChipGroup.setOnCheckedStateChangeListener { _, _ ->
            val digits = digitsIdMap[binding.digitsChipGroup.checkedChipId]!!

            CurrentGameOptionsVariant.instance.digitSetting = digits
            applicationPreferences.digitSetting = digits
            gridPreviewHolder?.refreshGrid()
        }

        binding.digitsChipGroup.check(digitsIdMap.filterValues {
            it == CurrentGameOptionsVariant.instance.digitSetting
        }.keys.first())
    }

    private fun createDifficultyChips() {
        val difficultyIdMap = mapOf(
            binding.chipDifficultyAny.id to DifficultySetting.ANY,
            binding.chipDifficultyVeryEasy.id to DifficultySetting.VERY_EASY,
            binding.chipDifficultyEasy.id to DifficultySetting.EASY,
            binding.chipDifficultyMedium.id to DifficultySetting.MEDIUM,
            binding.chipDifficultyHard.id to DifficultySetting.HARD,
            binding.chipDifficultyVeryHard.id to DifficultySetting.EXTREME,
        )

        binding.difficultyChipGroup.setOnCheckedStateChangeListener { _, _ ->
            val digitdifficulty = difficultyIdMap[binding.difficultyChipGroup.checkedChipId]!!

            CurrentGameOptionsVariant.instance.difficultySetting = digitdifficulty
            applicationPreferences.difficultySetting = digitdifficulty
            gridPreviewHolder?.refreshGrid()
        }

        binding.difficultyChipGroup.check(difficultyIdMap.filterValues {
            it == CurrentGameOptionsVariant.instance.difficultySetting
        }.keys.first())
    }

    private fun showOperationsChanged(isChecked: Boolean) {
        CurrentGameOptionsVariant.instance.showOperators = isChecked
        applicationPreferences.setShowOperators(isChecked)
        gridPreviewHolder!!.refreshGrid()
    }

    fun setGameVariant(variant: GameVariant) {
        //binding?.difficultyChipGroup?.isEnabled = GridDifficultyCalculator.isSupported(variant)
    }
}