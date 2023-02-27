package com.holokenmod.ui.newgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.holokenmod.R;
import com.holokenmod.databinding.NewGameOptionsFragmentBinding;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DifficultySetting;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

public class GridCellOptionsFragment extends Fragment {
	
	private GridPreviewHolder gridPreviewHolder;
	
	private NewGameOptionsFragmentBinding binding;
	
	public GridCellOptionsFragment() {
		super(R.layout.new_game_options_fragment);
		
	}
	
	void setGridPreviewHolder(GridPreviewHolder gridPreviewHolder) {
		this.gridPreviewHolder = gridPreviewHolder;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		binding = NewGameOptionsFragmentBinding.inflate(inflater, parent, false);
		
		return binding.getRoot();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		createDifficultySpinner();
		createFirstDigitSpinner();
		createSingleCageSpinner();
		createOperationsSpinner();
		
		binding.showOperationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> showOperationsChanged(isChecked));
		binding.showOperationsSwitch.setChecked(CurrentGameOptionsVariant.getInstance().showOperators());
	}
	
	private void createDifficultySpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
				R.array.setting_difficulty_entries, android.R.layout.simple_spinner_item);
		
		AutoCompleteTextView autoComplete = (AutoCompleteTextView) binding.spinnerDifficulty.getEditText();
		
		autoComplete.setAdapter(adapter);
		autoComplete.setText(adapter.getItem(CurrentGameOptionsVariant.getInstance().getDifficultySetting().ordinal()), false);
		autoComplete.setOnItemClickListener(createDifficultyListener());
	}
	
	@NonNull
	private AdapterView.OnItemClickListener createDifficultyListener() {
		return (parent, view, position, id) -> {
			DifficultySetting difficultySetting = DifficultySetting.values()[position];
			CurrentGameOptionsVariant.getInstance().setDifficultySetting(difficultySetting);
			ApplicationPreferences.getInstance().setDifficultySetting(difficultySetting);
			gridPreviewHolder.refreshGrid();
		};
	}

	private void createFirstDigitSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
				R.array.setting_digits_entries, android.R.layout.simple_spinner_item);
		
		AutoCompleteTextView autoComplete = (AutoCompleteTextView) binding.spinnerFirstDigit.getEditText();
		
		autoComplete.setAdapter(adapter);
		autoComplete.setText(adapter.getItem(CurrentGameOptionsVariant.getInstance().getDigitSetting().ordinal()), false);
		autoComplete.setOnItemClickListener(createFirstDigitListener());
	}
	
	@NonNull
	private AdapterView.OnItemClickListener createFirstDigitListener() {
		return (parent, view, position, id) -> {
			DigitSetting digitSetting = DigitSetting.values()[position];
			CurrentGameOptionsVariant.getInstance().setDigitSetting(digitSetting);
			ApplicationPreferences.getInstance().setDigitSetting(digitSetting);
			gridPreviewHolder.refreshGrid();
		};
	}
	
	private void createSingleCageSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.requireContext(),
				R.array.setting_single_cages_entries, android.R.layout.simple_spinner_item);
		
		AutoCompleteTextView autoComplete = (AutoCompleteTextView) binding.spinnerSingleCageUsage.getEditText();
		
		autoComplete.setAdapter(adapter);
		autoComplete.setText(adapter.getItem(CurrentGameOptionsVariant.getInstance().getSingleCageUsage().ordinal()), false);
		autoComplete.setOnItemClickListener(createSingleCageListener());
	}
	
	@NonNull
	private AdapterView.OnItemClickListener createSingleCageListener() {
		return (parent, view, position, id) -> {
			SingleCageUsage singleCageUsage = SingleCageUsage.values()[position];
			
			CurrentGameOptionsVariant.getInstance().setSingleCageUsage(singleCageUsage);
			ApplicationPreferences.getInstance().setSingleCageUsage(singleCageUsage);
			
			gridPreviewHolder.refreshGrid();
		};
	}
	
	private void createOperationsSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.requireContext(),
				R.array.setting_operations_entries, android.R.layout.simple_spinner_item);
		
		AutoCompleteTextView autoComplete = (AutoCompleteTextView) binding.spinnerOperations.getEditText();
		
		autoComplete.setAdapter(adapter);
		autoComplete.setText(adapter.getItem(CurrentGameOptionsVariant.getInstance().getCageOperation().ordinal()), false);
		autoComplete.setOnItemClickListener(createOperationsListener());
	}
	
	@NonNull
	private AdapterView.OnItemClickListener createOperationsListener() {
		return (parent, view, position, id) -> {
			GridCageOperation operations = GridCageOperation.values()[position];
			
			CurrentGameOptionsVariant.getInstance().setCageOperation(operations);
			ApplicationPreferences.getInstance().setOperations(operations);
			
			gridPreviewHolder.refreshGrid();
		};
	}
	
	private void showOperationsChanged(boolean isChecked) {
		CurrentGameOptionsVariant.getInstance().setShowOperators(isChecked);
		ApplicationPreferences.getInstance().setShowOperators(isChecked);
		
		gridPreviewHolder.refreshGrid();
	}
}
