package com.holokenmod.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.R;
import com.holokenmod.creation.GridCreator;
import com.holokenmod.options.ApplicationPreferences;

public class NewGameActivity extends AppCompatActivity implements GridPreviewHolder {
	private NewGameOptionsFragment optionsFragment;
	private Slider widthSlider;
	private Slider heigthSlider;
	private boolean squareOnlyMode = false;
	
	public NewGameActivity() {
	}
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		
		super.onCreate(savedInstanceState);
		
		if (!PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("showfullscreen", false)) {
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		setContentView(R.layout.activity_newgame);
		
		MaterialButton startNewGameButton = findViewById(R.id.startnewgame);
		startNewGameButton.setOnClickListener(v -> startNewGame());
		
		GridUI gridUi = (GridUI) findViewById(R.id.newGridPreview);
		gridUi.setTheme(ApplicationPreferences.getInstance().getTheme());
		
		squareOnlyMode = ApplicationPreferences.getInstance().getSquareOnlyGrid();
		
		widthSlider = findViewById(R.id.widthslider);
		heigthSlider = findViewById(R.id.heigthslider);
		SwitchMaterial squareOnlySwitch = findViewById(R.id.squareOnlySwitch);
		
		widthSlider.setValue(ApplicationPreferences.getInstance().getGridWidth());
		heigthSlider.setValue(ApplicationPreferences.getInstance().getGridHeigth());
		squareOnlySwitch.setChecked(squareOnlyMode);
		
		widthSlider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		heigthSlider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		squareOnlySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> squareOnlyChanged(isChecked));
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		optionsFragment = new NewGameOptionsFragment();
		optionsFragment.setGridPreviewHolder(this);
		
		ft.replace(R.id.newGameOptions, optionsFragment);
		ft.commit();
		
		refreshGrid();
	}
	
	private void sizeSliderChanged(float value) {
		if (squareOnlyMode) {
			widthSlider.setValue(value);
			heigthSlider.setValue(value);
		}
		
		ApplicationPreferences.getInstance().setGridWidth(Math.round(widthSlider.getValue()));
		ApplicationPreferences.getInstance().setGridHeigth(Math.round(heigthSlider.getValue()));
		
		refreshGrid();
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
		return new GridSize(
				Math.round(ApplicationPreferences.getInstance().getGridWidth()),
				Math.round(ApplicationPreferences.getInstance().getGridHeigth()));
	}
	
	@Override
	public synchronized void refreshGrid() {
		GridUI gridUi = (GridUI) findViewById(R.id.newGridPreview);
		
		GridCreator gridCreator = new GridCreator(getGridSize());
		Grid grid = gridCreator.createRandomizedGridWithCages();
		
		gridUi.setGrid(grid);
		
		grid.addAllCells();
		
		gridUi.rebuidCellsFromGrid();
		gridUi.invalidate();
	}
}