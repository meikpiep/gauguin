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

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.holokenmod.R;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

public class NewGameOptionsFragment extends Fragment {
	
	private GridPreviewHolder gridPreviewHolder;
	private Slider widthSlider;
	private Slider heigthSlider;
	private boolean squareOnlyMode = false;
	
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
		squareOnlyMode = ApplicationPreferences.getInstance().getSquareOnlyGrid();
		
		widthSlider = view.findViewById(R.id.widthslider);
		heigthSlider = view.findViewById(R.id.heigthslider);
		SwitchMaterial squareOnlySwitch = view.findViewById(R.id.squareOnlySwitch);
		
		widthSlider.setValue(ApplicationPreferences.getInstance().getGridWidth());
		heigthSlider.setValue(ApplicationPreferences.getInstance().getGridHeigth());
		squareOnlySwitch.setChecked(squareOnlyMode);
		
		widthSlider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		heigthSlider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		squareOnlySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> squareOnlyChanged(isChecked));
		
		createFirstDigitSpinner(view);
		createSingleCageSpinner(view);
		createOperationsSpinner(view);
		
		SwitchMaterial showOperationsSwitch = view.findViewById(R.id.showOperationsSwitch);
		showOperationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> showOperationsChanged(isChecked));
		showOperationsSwitch.setChecked(GameVariant.getInstance().showOperators());
	}
	
	private void createFirstDigitSpinner(@NonNull View view) {
		Spinner spinner = view.findViewById(R.id.spinnerSingleCageUsage);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
				R.array.setting_digits_entries, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(createFirstDigitListener());
		spinner.setSelection(GameVariant.getInstance().getDigitSetting().ordinal());
	}
	
	@NonNull
	private AdapterView.OnItemSelectedListener createFirstDigitListener() {
		return new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				DigitSetting digitSetting = DigitSetting.values()[position];
				GameVariant.getInstance().setDigitSetting(digitSetting);
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
		spinner.setSelection(GameVariant.getInstance().getSingleCageUsage().ordinal());
	}
	
	@NonNull
	private AdapterView.OnItemSelectedListener createSingleCageListener() {
		return new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				SingleCageUsage singleCageUsage = SingleCageUsage.values()[position];
				GameVariant.getInstance().setSingleCageUsage(singleCageUsage);
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
		spinner.setSelection(GameVariant.getInstance().getCageOperation().ordinal());
	}
	
	@NonNull
	private AdapterView.OnItemSelectedListener createOperationsListener() {
		return new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				GridCageOperation operations = GridCageOperation.values()[position];
				GameVariant.getInstance().setCageOperation(operations);
				ApplicationPreferences.getInstance().setOperations(operations);
				gridPreviewHolder.refreshGrid();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
	}
	
	private void sizeSliderChanged(float value) {
		if (squareOnlyMode) {
			widthSlider.setValue(value);
			heigthSlider.setValue(value);
		}
		
		ApplicationPreferences.getInstance().setGridWidth(Math.round(widthSlider.getValue()));
		ApplicationPreferences.getInstance().setGridHeigth(Math.round(heigthSlider.getValue()));
		
		gridPreviewHolder.refreshGrid();
	}
	
	private void squareOnlyChanged(boolean isChecked) {
		squareOnlyMode = isChecked;
		ApplicationPreferences.getInstance().setSquareOnlyGrid(isChecked);
		
		if (squareOnlyMode) {
			float squareSize = Math.min(widthSlider.getValue(), heigthSlider.getValue());
			
			widthSlider.setValue(squareSize);
			heigthSlider.setValue(squareSize);
			
			ApplicationPreferences.getInstance().setGridWidth(Math.round(widthSlider.getValue()));
			ApplicationPreferences.getInstance().setGridHeigth(Math.round(heigthSlider.getValue()));
		}
	}
	
	private void showOperationsChanged(boolean isChecked) {
		GameVariant.getInstance().setShowOperators(isChecked);
		ApplicationPreferences.getInstance().setShowOperators(isChecked);
		
		gridPreviewHolder.refreshGrid();
	}
}
