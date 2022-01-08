package com.holokenmod.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.R;
import com.holokenmod.creation.GridCreator;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

public class NewGameActivity extends AppCompatActivity {
	private Slider widthSlider;
	private Slider heigthSlider;
	private boolean squareOnlyMode = false;
	
	public NewGameActivity() {
	}
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("showfullscreen", false)) {
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		setContentView(R.layout.activity_newgame);
		
		FloatingActionButton startNewGameButton = (FloatingActionButton) findViewById(R.id.startnewgame);
		startNewGameButton.setOnClickListener(v -> startNewGame());
		
		widthSlider = (Slider) findViewById(R.id.widthslider);
		heigthSlider = (Slider) findViewById(R.id.heigthslider);
		
		SwitchMaterial squareOnlySwitch = (SwitchMaterial) findViewById(R.id.squareOnlySwitch);
		squareOnlySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> squareOnlyChanged(isChecked));
		
		widthSlider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		heigthSlider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		
		createFirstDigitSpinner();
		createSingleCageSpinner();
		createOperationsSpinner();
		
		SwitchMaterial showOperationsSwitch = (SwitchMaterial) findViewById(R.id.showOperationsSwitch);
		showOperationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> showOperationsChanged(isChecked));
		showOperationsSwitch.setChecked(GameVariant.getInstance().showOperators());
		
		refreshGrid();
	}
	
	private void createFirstDigitSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerSingleCageUsage);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
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
				DigitSetting firstDigit = DigitSetting.values()[position];
				GameVariant.getInstance().setDigitSetting(firstDigit);
				ApplicationPreferences.getInstance().setDigitSetting(firstDigit);
				refreshGrid();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
	}
	
	private void createSingleCageSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerFirstDigit);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
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
				refreshGrid();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
	}
	
	private void createOperationsSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.spinnerOperations);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
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
				refreshGrid();
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
		
		refreshGrid();
	}
	
	private void squareOnlyChanged(boolean isChecked) {
		squareOnlyMode = isChecked;
		
		if (squareOnlyMode) {
			float squareSize = Math.min(widthSlider.getValue(), heigthSlider.getValue());
			
			widthSlider.setValue(squareSize);
			heigthSlider.setValue(squareSize);
		}
	}
	
	private void showOperationsChanged(boolean isChecked) {
		GameVariant.getInstance().setShowOperators(isChecked);
		ApplicationPreferences.getInstance().setShowOperators(isChecked);
		
		refreshGrid();
	}
	
	private void startNewGame() {
		GridSize gridsize = getGridSize();
		
		Intent intent = NewGameActivity.this.getIntent();
		
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, gridsize.toString());
		
		NewGameActivity.this.setResult(0, intent);
		NewGameActivity.this.finish();
	}
	
	@NonNull
	private GridSize getGridSize() {
		GridSize gridsize = new GridSize(
				Math.round(widthSlider.getValue()),
				Math.round(heigthSlider.getValue()));
		return gridsize;
	}
	
	synchronized void refreshGrid() {
		GridUI gridUi = (GridUI) findViewById(R.id.newGridPreview);
		
		GridCreator gridCreator = new GridCreator(getGridSize());
		Grid grid = gridCreator.createRandomizedGridWithCages();
		
		gridUi.setGrid(grid);
		
		grid.addAllCells();
		
		gridUi.rebuidCellsFromGrid();
		gridUi.invalidate();
	}
}