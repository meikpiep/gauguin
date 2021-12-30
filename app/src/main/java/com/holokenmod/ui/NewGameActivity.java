package com.holokenmod.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;
import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.R;
import com.holokenmod.creation.GridCreator;

public class NewGameActivity extends AppCompatActivity {
	private Slider widthSlider;
	private Slider heigthSlider;
	
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
		
		ImageButton startNewGameButton = (ImageButton) findViewById(R.id.startnewgame);
		widthSlider = (Slider) findViewById(R.id.widthslider);
		heigthSlider = (Slider) findViewById(R.id.heigthslider);
		
		startNewGameButton.setOnClickListener(v -> startNewGame());
		
		widthSlider.addOnChangeListener((slider, value,  fromUser) -> refreshGrid());
		heigthSlider.addOnChangeListener((slider, value,  fromUser) -> refreshGrid());
		
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