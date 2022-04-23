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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.holokenmod.Game;
import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.R;
import com.holokenmod.SaveGame;
import com.holokenmod.StatisticsManager;
import com.holokenmod.Theme;
import com.holokenmod.UndoManager;
import com.holokenmod.Utils;
import com.holokenmod.creation.GridCalculationListener;
import com.holokenmod.creation.GridCalculationService;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.GameVariant;

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
	private static Theme theme;
	private static boolean rmpencil;
	
	private final Handler mHandler = new Handler(Looper.getMainLooper());
	private final Handler mTimerHandler = new Handler(Looper.getMainLooper());
	private GridUI kenKenGrid;
	private UndoManager undoList;
	private FloatingActionButton actionStatistics;
	private View undoButton;
	private View eraserButton;
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
	
	// Create runnable for posting
	final Runnable newGameReady = () -> {
		//MainActivity.this.dismissDialog(0);
		MainActivity.this.startFreshGrid(true);
		MainActivity.this.kenKenGrid.setVisibility(View.VISIBLE);
	};
	
	private Game game;
	private KeyPadFragment keyPadFragment;
	private DrawerLayout drawerLayout;
	private FerrisWheelView ferrisWheel;
	private TextView loadingLabel;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		
		ApplicationPreferences.getInstance().setPreferenceManager(
				PreferenceManager.getDefaultSharedPreferences(this));
		
		PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
		
		setContentView(R.layout.activity_main);
		
		GameVariant.getInstance().loadPreferences(ApplicationPreferences.getInstance());
		
		undoButton = findViewById(R.id.undo);
		
		actionStatistics = findViewById(R.id.hint);
		
		undoList = new UndoManager(undoButton);
		game = new Game(undoList);
		
		this.kenKenGrid = findViewById(R.id.gridview);
		
		this.timeView = findViewById(R.id.playtime);
		
		undoButton.setEnabled(false);
		
		eraserButton = findViewById(R.id.eraser);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		keyPadFragment = new KeyPadFragment();
		
		ft.replace(R.id.keypadFrame, keyPadFragment);
		ft.commit();
		
		this.kenKenGrid.setOnGridTouchListener(cell -> {
			kenKenGrid.setSelectorShown(true);
			game.selectCell();
		});
		
		this.kenKenGrid.setOnLongClickListener(v -> setSinglePossibleOnSelectedCell());
		
		this.game.setSolvedHandler(() -> {
			mTimerHandler.removeCallbacks(playTimer);
			getGrid().setPlayTime(System.currentTimeMillis() - starttime);
			
			showProgress(getString(R.string.puzzle_solved));
			actionStatistics.setEnabled(false);
			undoButton.setEnabled(false);
			
			StatisticsManager statisticsManager = new StatisticsManager(this, getGrid());
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
		});
		
		this.kenKenGrid.setFocusable(true);
		this.kenKenGrid.setFocusableInTouchMode(true);
		registerForContextMenu(this.kenKenGrid);
		
		actionStatistics.setOnClickListener(v -> checkProgress());
		
		undoButton.setOnClickListener(v -> {
			game.undoOneStep();
		});
		
		eraserButton.setOnClickListener(v -> {
			game.eraseSelectedCell();
		});
		
		BottomAppBar appBar = findViewById(R.id.mainBottomAppBar);
		NavigationView navigationView = findViewById(R.id.mainNavigationView);
		drawerLayout = findViewById(R.id.container);
		
		navigationView.setNavigationItemSelectedListener((menuItem) -> {
			switch (menuItem.getItemId()) {
				case R.id.newGame2:
					createNewGame();
					break;
				case R.id.menu_save:
					final Intent i = new Intent(this, SaveGameListActivity.class);
					startActivityForResult(i, 7);
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
		});
		
		if (appBar != null) {
			appBar.setOnMenuItemClickListener((menuItem) -> {
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
				}
				
				return true;
			});
			
			appBar.setNavigationOnClickListener(view -> drawerLayout.open());
		}
		
		GridCalculationService.getInstance().addListener(new GridCalculationListener() {
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
		});
		
		
		ferrisWheel = findViewById(R.id.ferrisWheelView);
		loadingLabel = findViewById(R.id.loadingLabel);
		
		loadApplicationPreferences();
		
		if (ApplicationPreferences.getInstance().newUserCheck()) {
			new MainDialogs(this, game).openHelpDialog();
		} else {
			final SaveGame saver = new SaveGame(this);
			restoreSaveGame(saver);
		}
	}
	
	private void showAndStartGame(Grid currentGrid) {
		MainActivity.this.runOnUiThread(() -> {
			kenKenGrid.setGrid(currentGrid);
			
			MainActivity.this.kenKenGrid.reCreate();
			
			updateGameObject();
			
			MainActivity.this.mHandler.post(newGameReady);
			
			ViewGroup viewGroup = MainActivity.this.findViewById(R.id.container);
			TransitionManager.beginDelayedTransition(viewGroup, new Fade(Fade.OUT));
			
			ferrisWheel.setVisibility(View.INVISIBLE);
			ferrisWheel.stopAnimation();
			loadingLabel.setVisibility(View.INVISIBLE);
			
			TransitionManager.endTransitions(viewGroup);
		});
	}
	
	private void cheatedOnGame() {
		makeToast(R.string.toast_cheated);
		new StatisticsManager(this, getGrid()).storeStreak(false);
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
		
		final SaveGame saver = new SaveGame(new File(filename));
		restoreSaveGame(saver);
	}
	
	public void onPause() {
		if (getGrid() != null && getGrid().getGridSize().getAmountOfNumbers() > 0) {
			getGrid().setPlayTime(System.currentTimeMillis() - starttime);
			mTimerHandler.removeCallbacks(playTimer);
			// NB: saving solved games messes up the timer?
			final SaveGame saver = new SaveGame(this);
			
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
		rmpencil = ApplicationPreferences.getInstance().removePencils();
		theme = ApplicationPreferences.getInstance().getTheme();
		
		if (theme == Theme.LIGHT) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		} else if (theme == Theme.DARK) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		}
		
		this.kenKenGrid.setTheme(theme);
		
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
	
	void postNewGame(final GridSize gridSize) {
		if (getGrid() != null && getGrid().isActive()) {
			new StatisticsManager(this, getGrid()).storeStreak(false);
		}
		
		final Grid grid = new Grid(gridSize);
		kenKenGrid.setGrid(grid);

		//showDialog(0);
		final Thread t = new Thread() {
			@Override
			public void run() {
				if (grid.getGridSize().getAmountOfNumbers() < 2) {
					return;
				}
				
				GridCalculationService calculationService = GridCalculationService.getInstance();
				
				if (calculationService.hasCalculatedNextGrid(grid.getGridSize(), GameVariant.getInstance())) {
					Grid grid = calculationService.consumeNextGrid();
					grid.setActive(true);
					
					showAndStartGame(grid);
					
					calculationService.calculateNextGrid();
				} else {
					calculationService.calculateCurrentAndNextGrids(grid.getGridSize(), GameVariant
							.getInstance().copy());
				}
			}
		};
		t.start();
	}
	
	private void updateGameObject() {
		game.setGridUI(kenKenGrid);
		game.setGrid(kenKenGrid.getGrid());
		
		keyPadFragment.setGame(game);
	}
	
	synchronized void startFreshGrid(final boolean newGame) {
		undoList.clear();
		
		this.kenKenGrid.setTheme(theme);
		this.actionStatistics.setEnabled(true);
		this.undoButton.setEnabled(false);
		
		if (newGame) {
			new StatisticsManager(this, getGrid()).storeStatisticsAfterNewGame();
			starttime = System.currentTimeMillis();
			mTimerHandler.postDelayed(playTimer, 0);
			
			getGrid().addPossiblesAtNewGameIfNecessary();
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
			
			GridCalculationService.getInstance().setGameParameter(kenKenGrid.getGrid().getGridSize(), GameVariant.getInstance().copy());
			//GridCalculationService.getInstance().calculateNextGrid();
		} else {
			new MainDialogs(this, game).newGameGridDialog();
		}
	}
	
	private boolean setSinglePossibleOnSelectedCell() {
		return game.setSinglePossibleOnSelectedCell(rmpencil);
	}
	
	public void checkProgress() {
		final int mistakes = getGrid().getNumberOfMistakes();
		final int filled = getGrid().getNumberOfFilledCells();

		final String text = getResources().getQuantityString(R.plurals.toast_mistakes,
				mistakes, mistakes) +
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