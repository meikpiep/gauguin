package org.piepmeyer.gauguin.ui.newgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.databinding.FragmentNewGameOptionsBinding
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.difficulty.MainGameDifficultyLevelBalloon
import kotlin.math.max

class GridCellOptionsFragment :
    Fragment(R.layout.fragment_new_game_options),
    KoinComponent {
    private val applicationPreferences: ApplicationPreferences by inject()
    private lateinit var viewModel: NewGameViewModel

    private lateinit var binding: FragmentNewGameOptionsBinding

    private lateinit var singleDifficultyIdMap: Map<Int, Set<DifficultySetting>>

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNewGameOptionsBinding.inflate(inflater, parent, false)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        viewModel = ViewModelProvider(requireActivity())[NewGameViewModel::class.java]

        binding.digitsExampleGridView.updateTheme()

        createDifficultyChips()
        createSingleCellUsageChips()
        createOperationsChips()
        createDigitsChips()
        createNumeralSystemChips()

        binding.difficultyInfoIcon.setOnClickListener {
            val variant = viewModel.gameVariantState.value

            val difficulty =
                variant.variant.options.difficultiesSetting
                    .singleOrNull()

            MainGameDifficultyLevelBalloon(difficulty, variant.variant).showBalloon(
                baseView = binding.difficultyInfoIcon,
                inflater = this.layoutInflater,
                parent = binding.root,
                lifecycleOwner = viewLifecycleOwner,
                anchorView = binding.difficultyInfoIcon,
            )
        }

        val tabs = binding.newGameOptionsTablayout
        tabs.addOnTabSelectedListener(
            object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    updateVisibility(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    // not needed
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    // not needed
                }
            },
        )
        updateVisibility(tabs.getTabAt(0)!!)

        binding.showOperationsSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            showOperationsChanged(isChecked)
        }
        binding.showOperationsSwitch.isChecked = applicationPreferences.showOperators()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameVariantState.collect {
                    gameVariantChanged(it)
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.difficultySelectionState.collect {
                    updateDifficultyMultiSelection()
                }
            }
        }
    }

    private fun updateVisibility(tab: TabLayout.Tab) {
        binding.newGameOptionsBasicScrollView.visibility = visibleIfTabPositionActive(tab, 0)
        binding.newGameOptionsNumbersScrollView.visibility = visibleIfTabPositionActive(tab, 1)
        binding.newGameOptionsAdvancedScrollView.visibility = visibleIfTabPositionActive(tab, 2)
    }

    private fun visibleIfTabPositionActive(
        tab: TabLayout.Tab,
        position: Int,
    ) = if (tab.position == position) {
        View.VISIBLE
    } else {
        View.GONE
    }

    private fun createSingleCellUsageChips() {
        val singleCellUsageIdMap =
            mapOf(
                binding.chipSingleCagesDynamic.id to SingleCageUsage.DYNAMIC,
                binding.chipSingleCagesFixedNumber.id to SingleCageUsage.FIXED_NUMBER,
                binding.chipSingleCagesNoSingleCages.id to SingleCageUsage.NO_SINGLE_CAGES,
            )

        binding.singleCellUsageChipGroup.setOnCheckedStateChangeListener { _, _ ->
            val singleCageOption = singleCellUsageIdMap[binding.singleCellUsageChipGroup.checkedChipId]!!

            applicationPreferences.singleCageUsage = singleCageOption
            viewModel.calculateGrid()
        }

        binding.singleCellUsageChipGroup.check(
            singleCellUsageIdMap
                .filterValues {
                    it == applicationPreferences.singleCageUsage
                }.keys
                .first(),
        )
    }

    private fun createOperationsChips() {
        val operationsIdMap =
            mapOf(
                binding.chipOperationsAll.id to GridCageOperation.OPERATIONS_ALL,
                binding.chipOperationsAdditionSubtraction.id to GridCageOperation.OPERATIONS_ADD_SUB,
                binding.chipOperationsAdditionMultiplication.id to GridCageOperation.OPERATIONS_ADD_MULT,
                binding.chipOperationsMultiplication.id to GridCageOperation.OPERATIONS_MULT,
            )

        binding.operationsChipGroup.setOnCheckedStateChangeListener { _, _ ->
            val operations = operationsIdMap[binding.operationsChipGroup.checkedChipId]!!

            applicationPreferences.operations = operations
            viewModel.calculateGrid()
        }

        binding.operationsChipGroup.check(
            operationsIdMap
                .filterValues {
                    it == applicationPreferences.operations
                }.keys
                .first(),
        )
    }

    private fun createDigitsChips() {
        val digitsIdMap =
            mapOf(
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

            applicationPreferences.digitSetting = digits
            viewModel.calculateGrid()
        }

        binding.digitsChipGroup.check(
            digitsIdMap
                .filterValues {
                    it == applicationPreferences.digitSetting
                }.keys
                .first(),
        )
    }

    private fun createDifficultyChips() {
        singleDifficultyIdMap =
            mapOf(
                binding.chipDifficultyAny.id to DifficultySetting.all(),
                binding.chipDifficultyVeryEasy.id to setOf(DifficultySetting.VERY_EASY),
                binding.chipDifficultyEasy.id to setOf(DifficultySetting.EASY),
                binding.chipDifficultyMedium.id to setOf(DifficultySetting.MEDIUM),
                binding.chipDifficultyHard.id to setOf(DifficultySetting.HARD),
                binding.chipDifficultyVeryHard.id to setOf(DifficultySetting.EXTREME),
            )

        binding.difficultyChipGroup.setOnCheckedStateChangeListener(difficultyChangelistener())

        binding.difficultyMultiSelectionSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateDifficultyMultiSelection(
                if (isChecked) DifficultySelectionState.MULTI_SELECTION else DifficultySelectionState.SINGLE_SELECTION,
            )
        }
    }

    private fun difficultyChangelistener() =
        ChipGroup.OnCheckedStateChangeListener { _, newCheckedChipIds ->
            val digitdifficulties =
                (binding.difficultyChipGroup.checkedChipIds + newCheckedChipIds)
                    .flatMap { singleDifficultyIdMap[it]!! }
                    .toSet()

            applicationPreferences.difficultiesSetting = digitdifficulties

            viewModel.calculateGrid()
        }

    private fun updateDifficultyMultiSelection() {
        val multiSelection = viewModel.difficultySelectionState.value == DifficultySelectionState.MULTI_SELECTION

        binding.difficultyChipGroup.setOnCheckedStateChangeListener(null)

        binding.difficultyChipGroup.isSingleSelection = !multiSelection
        binding.chipDifficultyAny.visibility = if (multiSelection) View.GONE else View.VISIBLE
        binding.difficultyMultiSelectionSwitch.isChecked = multiSelection

        if (multiSelection) {
            binding.chipDifficultyVeryEasy.isChecked = DifficultySetting.VERY_EASY in applicationPreferences.difficultiesSetting
            binding.chipDifficultyEasy.isChecked = DifficultySetting.EASY in applicationPreferences.difficultiesSetting
            binding.chipDifficultyMedium.isChecked = DifficultySetting.MEDIUM in applicationPreferences.difficultiesSetting
            binding.chipDifficultyHard.isChecked = DifficultySetting.HARD in applicationPreferences.difficultiesSetting
            binding.chipDifficultyVeryHard.isChecked = DifficultySetting.EXTREME in applicationPreferences.difficultiesSetting
        } else {
            val chipId =
                singleDifficultyIdMap
                    .filterValues { it == applicationPreferences.difficultiesSetting }
                    .keys
                    .first()

            binding.difficultyChipGroup.check(chipId)
        }

        binding.difficultyChipGroup.setOnCheckedStateChangeListener(difficultyChangelistener())
    }

    private fun createNumeralSystemChips() {
        val numeralSystemIdMap =
            mapOf(
                binding.chipNumeralSystemBinary.id to NumeralSystem.Binary,
                binding.chipNumeralSystemQuaternary.id to NumeralSystem.Quaternary,
                binding.chipNumeralSystemOctal.id to NumeralSystem.Octal,
                binding.chipNumeralSystemDecimal.id to NumeralSystem.Decimal,
                binding.chipNumeralSystemHexaDecimal.id to NumeralSystem.Hexadecimal,
            )

        binding.numeralSystemChipGroup.setOnCheckedStateChangeListener { _, _ ->
            val numeralSystem = numeralSystemIdMap[binding.numeralSystemChipGroup.checkedChipId]!!

            applicationPreferences.numeralSystem = numeralSystem
            viewModel.calculateGrid()
        }

        binding.numeralSystemChipGroup.check(
            numeralSystemIdMap
                .filterValues {
                    it == applicationPreferences.numeralSystem
                }.keys
                .first(),
        )
    }

    private fun showOperationsChanged(isChecked: Boolean) {
        applicationPreferences.setShowOperators(isChecked)
        viewModel.calculateGrid()
    }

    private fun gameVariantChanged(state: GridVariantState) {
        binding.singleCellUsageChipGroup.forEach { it.isEnabled = viewModel.singleCellOptionsAvailable() }

        val numbersBadgeShouldBeVisible =
            binding.digitsChipGroup.checkedChipId != binding.chipDigitsFromOne.id ||
                binding.numeralSystemChipGroup.checkedChipId != binding.chipNumeralSystemDecimal.id
        val advancedBadgeShouldBeVisible =
            binding.singleCellUsageChipGroup.checkedChipId != binding.chipSingleCagesFixedNumber.id ||
                !binding.showOperationsSwitch.isChecked

        setBadgeVisibility(
            numbersBadgeShouldBeVisible,
            binding.newGameOptionsTablayout.getTabAt(1)!!,
        )

        setBadgeVisibility(
            advancedBadgeShouldBeVisible,
            binding.newGameOptionsTablayout.getTabAt(2)!!,
        )

        val grid =
            GridBuilder(max(state.variant.width, state.variant.height), 1, state.variant.options)
                .createGrid()

        var cellIndex = 0

        grid.cages =
            state.variant.possibleDigits.map {
                val cell = grid.cells[cellIndex]

                cell.value = it
                cell.userValue = it
                GridCage.createWithSingleCellArithmetic(cellIndex++, grid, cell)
            }

        binding.digitsExampleGridView.hideCageText = true
        binding.digitsExampleGridView.grid = grid
        binding.digitsExampleGridView.requestLayout()
    }

    private fun setBadgeVisibility(
        shouldBeVisible: Boolean,
        tab: TabLayout.Tab,
    ) {
        if (shouldBeVisible) {
            if (tab.badge == null) {
                tab.orCreateBadge.isVisible = true
            }
        } else {
            tab.removeBadge()
        }
    }
}
