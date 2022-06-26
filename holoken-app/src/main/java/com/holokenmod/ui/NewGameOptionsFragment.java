package com.holokenmod.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.holokenmod.R;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

public class NewGameOptionsFragment extends Fragment {
	
	private GridPreviewHolder gridPreviewHolder;
	
	public NewGameOptionsFragment() {
		super(R.layout.new_game_options_fragment);
		
	}
	
	void setGridPreviewHolder(GridPreviewHolder gridPreviewHolder) {
		this.gridPreviewHolder = gridPreviewHolder;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.new_game_options_fragment, parent, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		createFirstDigitSpinner(view);
		createSingleCageSpinner(view);
		createOperationsSpinner(view);
		
		SwitchMaterial showOperationsSwitch = view.findViewById(R.id.showOperationsSwitch);
		showOperationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> showOperationsChanged(isChecked));
		showOperationsSwitch.setChecked(CurrentGameOptionsVariant.getInstance().showOperators());
	}
	
	private void createFirstDigitSpinner(@NonNull View view) {
		Spinner spinner = view.findViewById(R.id.spinnerSingleCageUsage);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
				R.array.setting_digits_entries, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(createFirstDigitListener());
		spinner.setSelection(CurrentGameOptionsVariant.getInstance().getDigitSetting().ordinal());
	}
	
	@NonNull
	private AdapterView.OnItemSelectedListener createFirstDigitListener() {
		return new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				DigitSetting digitSetting = DigitSetting.values()[position];
				CurrentGameOptionsVariant.getInstance().setDigitSetting(digitSetting);
				ApplicationPreferences.getInstance().setDigitSetting(digitSetting);
				gridPreviewHolder.refreshGrid();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
	}
	
	private void createSingleCageSpinner(@NonNull View view) {
		Spinner spinner = view.findViewById(R.id.spinnerFirstDigit);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
				R.array.setting_single_cages_entries, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(createSingleCageListener());
		spinner.setSelection(CurrentGameOptionsVariant.getInstance().getSingleCageUsage().ordinal());
	}
	
	@NonNull
	private AdapterView.OnItemSelectedListener createSingleCageListener() {
		return new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				SingleCageUsage singleCageUsage = SingleCageUsage.values()[position];
				CurrentGameOptionsVariant.getInstance().setSingleCageUsage(singleCageUsage);
				ApplicationPreferences.getInstance().setSingleCageUsage(singleCageUsage);
				gridPreviewHolder.refreshGrid();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
	}
	
	private void createOperationsSpinner(@NonNull View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.spinnerOperations);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
				R.array.setting_operations_entries, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(createOperationsListener());
		spinner.setSelection(CurrentGameOptionsVariant.getInstance().getCageOperation().ordinal());
	}
	
	@NonNull
	private AdapterView.OnItemSelectedListener createOperationsListener() {
		return new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				GridCageOperation operations = GridCageOperation.values()[position];
				CurrentGameOptionsVariant.getInstance().setCageOperation(operations);
				ApplicationPreferences.getInstance().setOperations(operations);
				gridPreviewHolder.refreshGrid();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
	}
	
	private void showOperationsChanged(boolean isChecked) {
		CurrentGameOptionsVariant.getInstance().setShowOperators(isChecked);
		ApplicationPreferences.getInstance().setShowOperators(isChecked);
		
		gridPreviewHolder.refreshGrid();
	}
}
