package com.holokenmod.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.R;
import com.holokenmod.creation.GridCreator;
import com.holokenmod.options.ApplicationPreferences;

public class NewGameActivity extends AppCompatActivity implements GridPreviewHolder {
	private NewGameOptionsFragment optionsFragment;
	
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
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		optionsFragment = new NewGameOptionsFragment();
		optionsFragment.setGridPreviewHolder(this);
		
		ft.replace(R.id.newGameOptions, optionsFragment);
		ft.commit();
		
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