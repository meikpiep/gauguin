package com.holokenmod.ui.newgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.holokenmod.R
import com.holokenmod.databinding.NewGameOptionsFragmentBinding
import com.holokenmod.options.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GridCellOptionsFragment : Fragment(R.layout.new_game_options_fragment), KoinComponent {
    private val applicationPreferences: ApplicationPreferences by inject()
    private var gridPreviewHolder: GridPreviewHolder? = null
    private var binding: NewGameOptionsFragmentBinding? = null

    fun setGridPreviewHolder(gridPreviewHolder: GridPreviewHolder) {
        this.gridPreviewHolder = gridPreviewHolder
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewGameOptionsFragmentBinding.inflate(inflater, parent, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createDifficultySpinner()
        createFirstDigitSpinner()
        createSingleCageSpinner()
        createOperationsSpinner()
        binding!!.showOperationsSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            showOperationsChanged(
                isChecked
            )
        }
        binding!!.showOperationsSwitch.isChecked =
            CurrentGameOptionsVariant.instance.showOperators
    }

    private fun createDifficultySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.setting_difficulty_entries, android.R.layout.simple_spinner_item
        )
        val autoComplete = binding!!.spinnerDifficulty.editText as AutoCompleteTextView
        autoComplete.setAdapter(adapter)
        autoComplete.setText(
            adapter.getItem(CurrentGameOptionsVariant.instance.difficultySetting.ordinal),
            false
        )
        autoComplete.onItemClickListener = createDifficultyListener()
    }

    private fun createDifficultyListener(): OnItemClickListener {
        return OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val difficultySetting = DifficultySetting.values()[position]
            CurrentGameOptionsVariant.instance.difficultySetting = difficultySetting
            applicationPreferences.difficultySetting = difficultySetting
            gridPreviewHolder!!.refreshGrid()
        }
    }

    private fun createFirstDigitSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.setting_digits_entries, android.R.layout.simple_spinner_item
        )
        val autoComplete = binding!!.spinnerFirstDigit.editText as AutoCompleteTextView
        autoComplete.setAdapter(adapter)
        autoComplete.setText(
            adapter.getItem(CurrentGameOptionsVariant.instance.digitSetting.ordinal),
            false
        )
        autoComplete.onItemClickListener = createFirstDigitListener()
    }

    private fun createFirstDigitListener(): OnItemClickListener {
        return OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val digitSetting = DigitSetting.values()[position]
            CurrentGameOptionsVariant.instance.digitSetting = digitSetting
            applicationPreferences.digitSetting = digitSetting
            gridPreviewHolder!!.refreshGrid()
        }
    }

    private fun createSingleCageSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.setting_single_cages_entries, android.R.layout.simple_spinner_item
        )
        val autoComplete = binding!!.spinnerSingleCageUsage.editText as AutoCompleteTextView
        autoComplete.setAdapter(adapter)
        autoComplete.setText(
            adapter.getItem(CurrentGameOptionsVariant.instance.singleCageUsage.ordinal),
            false
        )
        autoComplete.onItemClickListener = createSingleCageListener()
    }

    private fun createSingleCageListener(): OnItemClickListener {
        return OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val singleCageUsage = SingleCageUsage.values()[position]
            CurrentGameOptionsVariant.instance.singleCageUsage = singleCageUsage
            applicationPreferences.singleCageUsage = singleCageUsage
            gridPreviewHolder!!.refreshGrid()
        }
    }

    private fun createOperationsSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.setting_operations_entries, android.R.layout.simple_spinner_item
        )
        val autoComplete = binding!!.spinnerOperations.editText as AutoCompleteTextView
        autoComplete.setAdapter(adapter)
        autoComplete.setText(
            adapter.getItem(CurrentGameOptionsVariant.instance.cageOperation.ordinal),
            false
        )
        autoComplete.onItemClickListener = createOperationsListener()
    }

    private fun createOperationsListener(): OnItemClickListener {
        return OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val operations = GridCageOperation.values()[position]
            CurrentGameOptionsVariant.instance.cageOperation = operations
            applicationPreferences.operations = operations
            gridPreviewHolder!!.refreshGrid()
        }
    }

    private fun showOperationsChanged(isChecked: Boolean) {
        CurrentGameOptionsVariant.instance.showOperators = isChecked
        applicationPreferences.setShowOperators(isChecked)
        gridPreviewHolder!!.refreshGrid()
    }
}