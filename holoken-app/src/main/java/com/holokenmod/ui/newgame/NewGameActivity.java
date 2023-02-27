package com.holokenmod.ui.newgame;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.holokenmod.R;
import com.holokenmod.calculation.GridCalculationService;
import com.holokenmod.calculation.GridPreviewCalculationService;
import com.holokenmod.creation.GridCreator;
import com.holokenmod.databinding.ActivityNewgameBinding;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.GameVariant;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NewGameActivity extends AppCompatActivity implements GridPreviewHolder {
	private final GridPreviewCalculationService gridCalculator = new GridPreviewCalculationService();
	private Future<Grid> gridFuture = null;
	private GridShapeOptionsFragment gridShapeOptionsFragment;
	
	public NewGameActivity() {
	}
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		
		super.onCreate(savedInstanceState);
		
		ActivityNewgameBinding binding = ActivityNewgameBinding.inflate(getLayoutInflater());
		
		setContentView(binding.getRoot());
		
		if (!PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("showfullscreen", false)) {
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		MaterialButton startNewGameButton = binding.startnewgame;
		startNewGameButton.setOnClickListener(v -> startNewGame());
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		GridCellOptionsFragment cellOptionsFragment = new GridCellOptionsFragment();
		cellOptionsFragment.setGridPreviewHolder(this);
		
		ft.replace(R.id.newGameOptions, cellOptionsFragment);
		ft.commit();
		
		FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
		gridShapeOptionsFragment = new GridShapeOptionsFragment();
		gridShapeOptionsFragment.setGridPreviewHolder(this);
		
		ft2.replace(R.id.newGameGridShapeOptions, gridShapeOptionsFragment);
		ft2.commit();
		
		refreshGrid();
	}
	
	private void startNewGame() {
		GridSize gridsize = getGridSize();
		
		Intent intent = NewGameActivity.this.getIntent();
		
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, gridsize.toString());
		
		GameVariant variant = new GameVariant(
				getGridSize(),
				ApplicationPreferences.getInstance().getGameVariant());
		
		Grid grid = gridCalculator.getGrid(variant);
		
		if (grid != null) {
			GridCalculationService.getInstance().setVariant(variant);
			GridCalculationService.getInstance().setNextGrid(grid);
		}
		
		NewGameActivity.this.setResult(0, intent);
		NewGameActivity.this.finishAfterTransition();
	}
	
	@NonNull
	private GridSize getGridSize() {
		return new GridSize(
				ApplicationPreferences.getInstance().getGridWidth(),
				ApplicationPreferences.getInstance().getGridHeigth());
	}
	
	@Override
	public synchronized void refreshGrid() {
		if (gridFuture != null && !gridFuture.isDone()) {
			gridFuture.cancel(true);
		}
		
		GameVariant variant = new GameVariant(
				getGridSize(),
				CurrentGameOptionsVariant.getInstance());
		
		gridFuture = gridCalculator.getOrCreateGrid(variant);
		
		Grid grid = null;
		
		boolean previewStillCalculating = false;
		
		try {
			grid = gridFuture.get(250, TimeUnit.MILLISECONDS);
			grid.addAllCells();
		} catch (ExecutionException|InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			grid = new GridCreator(variant).createRandomizedGridWithCages();
			
			previewStillCalculating = true;
		}
		
		gridShapeOptionsFragment.setGrid(grid);
		
		if (gridShapeOptionsFragment.getGridUI() != null) {
			gridShapeOptionsFragment.getGridUI().setPreviewStillCalculating(previewStillCalculating);
			
			gridShapeOptionsFragment.getGridUI().rebuildCellsFromGrid();
			gridShapeOptionsFragment.getGridUI().updateTheme();
			gridShapeOptionsFragment.getGridUI().invalidate();
		}
		
		if (previewStillCalculating) {
			Thread gridPreviewThread = new Thread(this::createPreview);
			gridPreviewThread.setName("PreviewFromNew-" + variant.getWidth() + "x" + variant.getHeight());
			
			gridPreviewThread.start();
		}
	}
	
	private void createPreview() {
		Grid previewGrid = null;
		
		try {
			Future<Grid> gridFuture = gridCalculator.getOrCreateGrid(new GameVariant(
					getGridSize(),
					CurrentGameOptionsVariant.getInstance()));
			
			previewGrid = gridFuture.get();
		} catch (ExecutionException | InterruptedException ex) {
			ex.printStackTrace();
		}
		
		Grid finalPreviewGrid = previewGrid;
		
		previewGridCalculated(finalPreviewGrid);
	}
	
	private void previewGridCalculated(Grid grid) {
		this.runOnUiThread(() -> {
			//TransitionManager.beginDelayedTransition(findViewById(R.id.newGame));
			if (gridShapeOptionsFragment.getGridUI() == null) {
				return;
			}
			
			gridShapeOptionsFragment.getGridUI().setGrid(grid);
			
			grid.addAllCells();
			
			gridShapeOptionsFragment.getGridUI().rebuildCellsFromGrid();
			gridShapeOptionsFragment.getGridUI().updateTheme();
			gridShapeOptionsFragment.getGridUI().setPreviewStillCalculating(false);
			
			gridShapeOptionsFragment.getGridUI().invalidate();
		});
	}
}