/***************************************************************************
 *  Copyright 2016 Adam Queler
 *  HolokenMod - KenKen(tm) game developed for Android
 *  (A modified version of Holoken 1.1.1 and 1.2 by Amanda Chow which was)
 *  (a modified version of MathDoku 1.9 by Ben Buxton and Stephen Lee)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 ***************************************************************************/

package com.holokenmod.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.holokenmod.R;
import com.holokenmod.StatisticsManager;
import com.holokenmod.Theme;
import com.holokenmod.Utils;
import com.holokenmod.calculation.GridCalculationListener;
import com.holokenmod.calculation.GridCalculationService;
import com.holokenmod.creation.GridDifficulty;
import com.holokenmod.game.CurrentGameSaver;
import com.holokenmod.game.Game;
import com.holokenmod.game.SaveGame;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.GameVariant;
import com.holokenmod.undo.UndoListener;
import com.holokenmod.undo.UndoManager;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import ru.github.igla.ferriswheel.FerrisWheelView;

public class MainActivity extends AppCompatActivity {
	
	private static final int UPDATE_RATE = 500;
	
	private final Handler mHandler = new Handler(Looper.getMainLooper());
	private final Handler mTimerHandler = new Handler(Looper.getMainLooper());
	private GridUI kenKenGrid;
	private UndoManager undoList;
	private FloatingActionButton actionStatistics;
	private View undoButton;
	private TextView timeView;
	private long starttime = 0;
	
	//runs without timer be reposting self
	final Runnable playTimer = new Runnable() {
		@Override
		public void run() {
			final long millis = System.currentTimeMillis() - starttime;
			timeView.setText(Utils.convertTimetoStr(millis));
			mTimerHandler.postDelayed(this, UPDATE_RATE);
		}
	};
	
	private Game game;
	private KeyPadFragment keyPadFragment;
	private DrawerLayout drawerLayout;
	private FerrisWheelView ferrisWheel;
	private TextView loadingLabel;
	private ConstraintLayout constraintLayout;
	private float keypadFrameHorizontalBias;
	
	@SuppressLint("MissingInflatedId")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		
		ApplicationPreferences.getInstance().setPreferenceManager(
				PreferenceManager.getDefaultSharedPreferences(this));
		
		PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
		
		setContentView(R.layout.activity_main);
		
		ApplicationPreferences.getInstance().loadGameVariant();
		
		undoButton = findViewById(R.id.undo);
		actionStatistics = findViewById(R.id.hint);
		
		UndoListener undoListener = undoPossible -> undoButton.setEnabled(undoPossible);
		
		undoList = new UndoManager(undoListener);
		game = new Game(undoList);
		
		this.kenKenGrid = findViewById(R.id.gridview);
		this.timeView = findViewById(R.id.playtime);
		
		undoButton.setEnabled(false);
		
		View eraserButton = findViewById(R.id.eraser);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		keyPadFragment = new KeyPadFragment();
		
		ft.replace(R.id.keypadFrame, keyPadFragment);
		ft.commit();

		this.kenKenGrid.initializeWithGame(game);
		
		this.game.setSolvedHandler(this::gameSolved);
		
		registerForContextMenu(this.kenKenGrid);
		
		actionStatistics.setOnClickListener(v -> checkProgress());
		undoButton.setOnClickListener(v -> game.undoOneStep());
		eraserButton.setOnClickListener(v -> game.eraseSelectedCell());
		
		constraintLayout = findViewById(R.id.mainConstraintLayout);
		
		BottomAppBar appBar = findViewById(R.id.mainBottomAppBar);
		NavigationView navigationView = findViewById(R.id.mainNavigationView);
		drawerLayout = findViewById(R.id.container);
		
		navigationView.setNavigationItemSelectedListener(this::menuItemSelected);
		
		if (appBar != null) {
			appBar.setOnMenuItemClickListener(this::appBarSelected);
			
			appBar.setNavigationOnClickListener(view -> drawerLayout.open());
		}
		
		GridCalculationService.getInstance().addListener(createGridCalculationListener());
		
		ferrisWheel = findViewById(R.id.ferrisWheelView);
		loadingLabel = findViewById(R.id.loadingLabel);
		
		loadApplicationPreferences();
		
		if (ApplicationPreferences.getInstance().newUserCheck()) {
			new MainDialogs(this, game).openHelpDialog();
		} else {
			final SaveGame saver = SaveGame.createWithDirectory(this.getFilesDir());
			restoreSaveGame(saver);
		}
	}
	
	@NonNull
	private GridCalculationListener createGridCalculationListener() {
		return new GridCalculationListener() {
			@Override
			public void startingCurrentGridCalculation() {
				MainActivity.this.runOnUiThread(() -> {
					findViewById(R.id.pendingCurrentGridCalculation).setVisibility(View.VISIBLE);
					findViewById(R.id.pendingNextGridCalculation).setVisibility(View.INVISIBLE);
					
					MainActivity.this.kenKenGrid.setVisibility(View.INVISIBLE);
					MainActivity.this.ferrisWheel.setVisibility(View.VISIBLE);
					MainActivity.this.loadingLabel.setVisibility(View.VISIBLE);
					
					MainActivity.this.ferrisWheel.startAnimation();
				});
			}
			
			@Override
			public void currentGridCalculated(Grid currentGrid) {
				MainActivity.this.runOnUiThread(() -> {
					findViewById(R.id.pendingCurrentGridCalculation).setVisibility(View.INVISIBLE);
					findViewById(R.id.pendingNextGridCalculation).setVisibility(View.INVISIBLE);
				});
				
				showAndStartGame(currentGrid);
			}
			
			@Override
			public void startingNextGridCalculation() {
				MainActivity.this.runOnUiThread(() -> {
					findViewById(R.id.pendingCurrentGridCalculation).setVisibility(View.INVISIBLE);
					findViewById(R.id.pendingNextGridCalculation).setVisibility(View.VISIBLE);
				});
			}
			
			@Override
			public void nextGridCalculated(Grid currentGrid) {
				MainActivity.this.runOnUiThread(() -> {
					findViewById(R.id.pendingCurrentGridCalculation).setVisibility(View.INVISIBLE);
					findViewById(R.id.pendingNextGridCalculation).setVisibility(View.INVISIBLE);
				});
			}
		};
	}
	
	private boolean appBarSelected(MenuItem menuItem) {
		int itemId = menuItem.getItemId();
		
		if (itemId == R.id.hint) {
			checkProgress();
		} else if (itemId == R.id.undo) {
			game.clearLastModified();
			undoList.restoreUndo();
			kenKenGrid.invalidate();
		} else if (itemId == R.id.eraser) {
			game.eraseSelectedCell();
		} else if (itemId == R.id.menu_show_mistakes) {
			this.game.markInvalidChoices();
			cheatedOnGame();
		} else if (itemId == R.id.menu_reveal_cell) {
			if (this.game.revealSelectedCell()) {
				cheatedOnGame();
			}
		} else if (itemId == R.id.menu_reveal_cage) {
			if (this.game.solveSelectedCage()) {
				cheatedOnGame();
			}
		} else if (itemId == R.id.menu_show_solution) {
			this.game.solveGrid();
			cheatedOnGame();
		} else if (itemId == R.id.menu_swap_keypad) {
			keypadFrameHorizontalBias += 0.25f;
			
			if (keypadFrameHorizontalBias == 1.0f) {
				keypadFrameHorizontalBias = 0.25f;
			}
			
			ConstraintSet constraintSet = new ConstraintSet();
			constraintSet.clone(constraintLayout);
			constraintSet.setHorizontalBias(R.id.keypadFrame,keypadFrameHorizontalBias);
			
			TransitionManager.beginDelayedTransition(constraintLayout);
			constraintSet.applyTo(constraintLayout);
		}
		
		return true;
	}
	
	private boolean menuItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.newGame2:
				createNewGame();
				break;
			case R.id.menu_load:
				final Intent i = new Intent(this, SaveGameListActivity.class);
				startActivityForResult(i, 7);
				break;
			case R.id.menu_save:
				new CurrentGameSaver(this.getFilesDir()).save();
				break;
			case R.id.menu_restart_game:
				new MainDialogs(this, game).restartGameDialog();
				break;
			case R.id.menu_stats:
				startActivity(new Intent(this, StatsActivity.class));
				break;
			case R.id.menu_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.menu_help:
				new MainDialogs(this, game).openHelpDialog();
				break;
			case R.id.menu_bugtracker:
				final Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://github.com/meikpiep/holokenmod/issues"));
				startActivity(intent);
				break;
			default:
				break;
		}
		
		drawerLayout.close();
		return true;
	}
	
	private void gameSolved() {
		mTimerHandler.removeCallbacks(playTimer);
		getGrid().setPlayTime(System.currentTimeMillis() - starttime);
		
		showProgress(getString(R.string.puzzle_solved));
		actionStatistics.setEnabled(false);
		undoButton.setEnabled(false);
		
		StatisticsManager statisticsManager = createStatisticsManager();
		Optional<String> recordTime = statisticsManager.storeStatisticsAfterFinishedGame();
		String recordText = getString(R.string.puzzle_record_time);
		
		recordTime.ifPresent(record ->
				showProgress(recordText + " " + record));
		
		statisticsManager.storeStreak(true);
		
		final long solvetime = getGrid().getPlayTime();
		String solveStr = Utils.convertTimetoStr(solvetime);
		timeView.setText(solveStr);
		
		KonfettiView konfettiView = findViewById(R.id.konfettiView);
		
		EmitterConfig emitterConfig = new Emitter(15L, TimeUnit.SECONDS).perSecond(150);
		Party party = new PartyFactory(emitterConfig)
				.angle(270)
				.spread(90)
				.setSpeedBetween(1f, 5f)
				.timeToLive(3000L)
				.position(0.0, 0.0, 1.0, 0.0)
				.build();
		
		konfettiView.start(party);
	}
	
	@NonNull
	private StatisticsManager createStatisticsManager() {
		return new StatisticsManager(this, getGrid());
	}
	
	private void showAndStartGame(Grid currentGrid) {
		this.runOnUiThread(() -> {
			TextView difficultyText = findViewById(R.id.difficulty);
			difficultyText.setText(new GridDifficulty(game.getGrid()).getInfo());
			
			ViewGroup viewGroup = findViewById(R.id.container);
			
			TransitionManager.beginDelayedTransition(viewGroup, new Fade(Fade.OUT));
			
			startFreshGrid(true);
			kenKenGrid.setVisibility(View.VISIBLE);
			
			kenKenGrid.setGrid(currentGrid);
			updateGameObject();
			
			kenKenGrid.reCreate();
			kenKenGrid.invalidate();
			
			ferrisWheel.setVisibility(View.INVISIBLE);
			ferrisWheel.stopAnimation();
			loadingLabel.setVisibility(View.INVISIBLE);
			
			TransitionManager.endTransitions(viewGroup);
		});
	}
	
	private void cheatedOnGame() {
		makeToast(R.string.toast_cheated);
		createStatisticsManager().storeStreak(false);
	}
	
	private Grid getGrid() {
		return kenKenGrid.getGrid();
	}
	
	protected void onActivityResult(final int requestCode,
									final int resultCode,
									final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data == null) {
			return;
		}
		
		final Bundle extras = data.getExtras();
		final String gridSizeString = extras.getString(Intent.EXTRA_TEXT);
		
		if (gridSizeString != null) {
			postNewGame(GridSize.create(gridSizeString));
			
			return;
		}
		
		if (requestCode != 7 || resultCode != Activity.RESULT_OK) {
			return;
		}
		
		final String filename = extras.getString("filename");
		
		Log.d("HoloKen", "Loading game: " + filename);
		
		final SaveGame saver = SaveGame.createWithFile(new File(filename));
		restoreSaveGame(saver);
	}
	
	public void onPause() {
		if (getGrid() != null && getGrid().getGridSize().getAmountOfNumbers() > 0) {
			getGrid().setPlayTime(System.currentTimeMillis() - starttime);
			mTimerHandler.removeCallbacks(playTimer);
			// NB: saving solved games messes up the timer?
			final SaveGame saver = SaveGame.createWithDirectory(this.getFilesDir());
			
			synchronized (this.kenKenGrid.lock) {    // Avoid saving game at the same time as creating puzzle
				saver.Save(getGrid());
			} // End of synchronised block
		}
		super.onPause();
	}
	
	public void onResume() {
		loadApplicationPreferences();
		
		if (getGrid() != null && getGrid().isActive()) {
			this.kenKenGrid.requestFocus();
			this.kenKenGrid.invalidate();
			starttime = System.currentTimeMillis() - getGrid().getPlayTime();
			mTimerHandler.postDelayed(playTimer, 0);
		}
		super.onResume();
	}
	
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN &&
				keyCode == KeyEvent.KEYCODE_BACK && this.kenKenGrid.isSelectorShown()) {
			this.kenKenGrid.requestFocus();
			this.kenKenGrid.setSelectorShown(false);
			this.kenKenGrid.invalidate();
			
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void loadApplicationPreferences() {
		Theme theme = ApplicationPreferences.getInstance().getTheme();
		
		if (theme == Theme.LIGHT) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		} else if (theme == Theme.DARK) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		}
		
		this.kenKenGrid.updateTheme();
		
		if (ApplicationPreferences.getInstance().getPrefereneces()
				.getBoolean("keepscreenon", true)) {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		
		if (!ApplicationPreferences.getInstance().getPrefereneces()
				.getBoolean("showfullscreen", false)) {
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		if (ApplicationPreferences.getInstance().getPrefereneces().getBoolean("showtimer", true)) {
			this.timeView.setVisibility(View.VISIBLE);
		} else {
			this.timeView.setVisibility(View.INVISIBLE);
		}
		
	}
	
	private void createNewGame() {
		new MainDialogs(this, game).newGameGridDialog();
	}
	
	private void postNewGame(final GridSize gridSize) {
		if (getGrid() != null && getGrid().isActive()) {
			createStatisticsManager().storeStreak(false);
		}
		
		GridCalculationService calculationService = GridCalculationService.getInstance();
		
		final GameVariant variant = new GameVariant(gridSize,
				CurrentGameOptionsVariant.getInstance().copy());
		
		if (calculationService.hasCalculatedNextGrid(variant)) {
			Grid grid = calculationService.consumeNextGrid();
			grid.setActive(true);
			
			showAndStartGame(grid);
			
			final Thread t = new Thread(calculationService::calculateNextGrid);
			t.start();
		} else {
			final Grid grid = new Grid(variant);
			kenKenGrid.setGrid(grid);
			
			final Thread t = new Thread() {
				@Override
				public void run() {
					if (gridSize.getAmountOfNumbers() < 2) {
						return;
					}
					
					GridCalculationService calculationService = GridCalculationService.getInstance();
					
					calculationService.calculateCurrentAndNextGrids(variant);
				}
			};
			
			t.start();
		}
	}
	
	private void updateGameObject() {
		game.setGridUI(kenKenGrid);
		game.setGrid(kenKenGrid.getGrid());
		
		keyPadFragment.setGame(game);
	}
	
	synchronized void startFreshGrid(final boolean newGame) {
		undoList.clear();
		
		this.kenKenGrid.updateTheme();
		this.actionStatistics.setEnabled(true);
		this.undoButton.setEnabled(false);
		
		if (newGame) {
			createStatisticsManager().storeStatisticsAfterNewGame();
			starttime = System.currentTimeMillis();
			mTimerHandler.postDelayed(playTimer, 0);
			
			if (ApplicationPreferences.getInstance().getPrefereneces()
					.getBoolean("pencilatstart", true)) {
				getGrid().addPossiblesAtNewGame();
			}
		}
	}
	
	private void restoreSaveGame(final SaveGame saver) {
		Optional<Grid> optionalGrid = saver.restore();
		
		if (optionalGrid.isPresent()) {
			Grid grid = optionalGrid.get();
			
			kenKenGrid.setGrid(grid);
			kenKenGrid.rebuidCellsFromGrid();
			
			startFreshGrid(false);
			if (!getGrid().isSolved()) {
				getGrid().setActive(true);
			} else {
				getGrid().setActive(false);
				
				if (getGrid().getSelectedCell() != null) {
					getGrid().getSelectedCell().setSelected(false);
				}
				
				this.undoButton.setEnabled(false);
				mTimerHandler.removeCallbacks(playTimer);
			}
			
			updateGameObject();
			
			this.kenKenGrid.invalidate();
			
			GridCalculationService.getInstance().setVariant(
					new GameVariant(
							kenKenGrid.getGrid().getGridSize(),
							CurrentGameOptionsVariant.getInstance().copy()));
			//GridCalculationService.getInstance().calculateNextGrid();
		} else {
			new MainDialogs(this, game).newGameGridDialog();
		}
	}
	
	public void checkProgress() {
		final int mistakes = getGrid().getNumberOfMistakes();
		final int filled = getGrid().getNumberOfFilledCells();

		final String text = getResources().getQuantityString(R.plurals.toast_mistakes,
				mistakes, mistakes)
				+ " / " +
				getResources().getQuantityString(R.plurals.toast_filled,
						filled, filled);
		
		int duration;
		
		if (mistakes == 0) {
			duration = 1500;
		} else {
			duration = 4000;
		}
		
		Snackbar.make(undoButton, text, duration)
				.setAnchorView(undoButton)
				.setAction("Undo", (view) -> {
					undoList.restoreUndo();
					kenKenGrid.invalidate();
					checkProgress();
				})
				.show();
	}
	
	private void showProgress(final String string) {
		Snackbar.make(undoButton, string, Snackbar.LENGTH_LONG)
				.setAnchorView(undoButton)
				.show();
	}
	
	private void makeToast(final int resId) {
		Snackbar.make(undoButton, resId, Snackbar.LENGTH_LONG)
				.setAnchorView(undoButton)
				.show();
	}
}