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

package com.holokenmod.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.DisplayCutoutCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.holokenmod.R;
import com.holokenmod.StatisticsManager;
import com.holokenmod.Theme;
import com.holokenmod.Utils;
import com.holokenmod.calculation.GridCalculationListener;
import com.holokenmod.calculation.GridCalculationService;
import com.holokenmod.databinding.ActivityMainBinding;
import com.holokenmod.game.Game;
import com.holokenmod.game.SaveGame;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.GameVariant;
import com.holokenmod.ui.MainDialogs;
import com.holokenmod.ui.grid.GridCellSizeService;
import com.holokenmod.undo.UndoListener;
import com.holokenmod.undo.UndoManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class MainActivity extends AppCompatActivity {
	
	private static final int UPDATE_RATE = 500;
	
	private final Handler mTimerHandler = new Handler(Looper.getMainLooper());
	private UndoManager undoList;
	private long starttime = 0;
	
	//runs without timer be reposting self
	final Runnable playTimer = new Runnable() {
		@Override
		public void run() {
			final long millis = System.currentTimeMillis() - starttime;
			topFragment.setGameTime(Utils.convertTimetoStr(millis));
			mTimerHandler.postDelayed(this, UPDATE_RATE);
		}
	};
	
	private Game game;
	private KeyPadFragment keyPadFragment;
	private GameTopFragment topFragment;
	private View undoButton;
	private float keypadFrameHorizontalBias;
	
	private ActivityMainBinding binding;
	
	private static WindowInsetsCompat insets = null;
	
	@SuppressLint("MissingInflatedId")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.MainScreenTheme);
		super.onCreate(savedInstanceState);
		
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		
		setContentView(binding.getRoot());

		ApplicationPreferences.getInstance().setPreferenceManager(
				PreferenceManager.getDefaultSharedPreferences(this));
		
		PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
		
		ApplicationPreferences.getInstance().loadGameVariant();
		
		undoButton = findViewById(R.id.undo);
		
		UndoListener undoListener = undoPossible -> undoButton.setEnabled(undoPossible);
		
		undoList = new UndoManager(undoListener);
		game = new Game(undoList);
		
		undoButton.setEnabled(false);
		
		View eraserButton = findViewById(R.id.eraser);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		keyPadFragment = new KeyPadFragment();
		topFragment = new GameTopFragment();
		
		ft.replace(R.id.keypadFrame, keyPadFragment);
		ft.replace(R.id.gameTopFrame, topFragment);
		ft.commit();

		binding.gridview.initializeWithGame(game);
		
		GridCellSizeService.getInstance().setCellSizeListener(cellSizePercent -> {
			binding.gridview.setCellSizePercent(cellSizePercent);
			binding.gridview.forceLayout();
		});
		
		this.game.setSolvedHandler(this::gameSolved);
		
		registerForContextMenu(binding.gridview);
		
		binding.hint.setOnClickListener(v -> checkProgress());
		undoButton.setOnClickListener(v -> game.undoOneStep());
		eraserButton.setOnClickListener(v -> game.eraseSelectedCell());
		
		BottomAppBar appBar = findViewById(R.id.mainBottomAppBar);
		NavigationView navigationView = findViewById(R.id.mainNavigationView);
		binding.container.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		
		navigationView.setNavigationItemSelectedListener(new MainNavigationItemSelectedListener(this));
		
		Slider gridScaleSlider = navigationView.getHeaderView(0).findViewById(R.id.gridScaleSlider);
		gridScaleSlider.addOnChangeListener((slider, value, fromUser) -> {
			if (fromUser) {
				GridCellSizeService.getInstance().setCellSizePercent(Math.round(value));
			}
		});
		
		GridCellSizeService.getInstance().setCellSizePercent(GridCellSizeService.getInstance().getCellSizePercent());
		
		if (appBar != null) {
			appBar.setOnMenuItemClickListener(this::appBarSelected);
			
			appBar.setNavigationOnClickListener(view -> binding.container.open());
		}
		
		GridCalculationService.getInstance().addListener(createGridCalculationListener());
		
		loadApplicationPreferences();
		
		if (ApplicationPreferences.getInstance().newUserCheck()) {
			new MainDialogs(this, game).openHelpDialog();
		} else {
			final SaveGame saver = SaveGame.createWithDirectory(this.getFilesDir());
			restoreSaveGame(saver);
		}
		
		System.out.println("onCreate");
	}
	
	@NonNull
	private GridCalculationListener createGridCalculationListener() {
		return new GridCalculationListener() {
			@Override
			public void startingCurrentGridCalculation() {
				MainActivity.this.runOnUiThread(() -> {
					binding.pendingCurrentGridCalculation.setVisibility(View.VISIBLE);
					binding.pendingNextGridCalculation.setVisibility(View.INVISIBLE);
					
					MainActivity.this.binding.gridview.setVisibility(View.INVISIBLE);
					binding.ferrisWheelView.setVisibility(View.VISIBLE);
					
					binding.ferrisWheelView.startAnimation();
				});
			}
			
			@Override
			public void currentGridCalculated(Grid currentGrid) {
				MainActivity.this.runOnUiThread(() -> {
					binding.pendingCurrentGridCalculation.setVisibility(View.INVISIBLE);
					binding.pendingNextGridCalculation.setVisibility(View.INVISIBLE);
				});
				
				showAndStartGame(currentGrid);
			}
			
			@Override
			public void startingNextGridCalculation() {
				MainActivity.this.runOnUiThread(() -> {
					binding.pendingCurrentGridCalculation.setVisibility(View.INVISIBLE);
					binding.pendingNextGridCalculation.setVisibility(View.VISIBLE);
				});
			}
			
			@Override
			public void nextGridCalculated(Grid currentGrid) {
				MainActivity.this.runOnUiThread(() -> {
					binding.pendingCurrentGridCalculation.setVisibility(View.INVISIBLE);
					binding.pendingNextGridCalculation.setVisibility(View.INVISIBLE);
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
			binding.gridview.invalidate();
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
			constraintSet.clone(binding.mainConstraintLayout);
			constraintSet.setHorizontalBias(R.id.keypadFrame, keypadFrameHorizontalBias);
			
			TransitionManager.beginDelayedTransition(binding.mainConstraintLayout);
			constraintSet.applyTo(binding.mainConstraintLayout);
		}
		
		return true;
	}
	
	private void gameSolved() {
		mTimerHandler.removeCallbacks(playTimer);
		getGrid().setPlayTime(System.currentTimeMillis() - starttime);
		
		showProgress(getString(R.string.puzzle_solved));
		binding.hint.setEnabled(false);
		undoButton.setEnabled(false);
		
		StatisticsManager statisticsManager = createStatisticsManager();
		Optional<String> recordTime = statisticsManager.storeStatisticsAfterFinishedGame();
		String recordText = getString(R.string.puzzle_record_time);
		
		recordTime.ifPresent(record ->
				showProgress(recordText + " " + record));
		
		statisticsManager.storeStreak(true);
		
		final long solvetime = getGrid().getPlayTime();
		String solveStr = Utils.convertTimetoStr(solvetime);
		topFragment.setGameTime(solveStr);
		
		KonfettiView konfettiView = findViewById(R.id.konfettiView);
		
		EmitterConfig emitterConfig = new Emitter(15L, TimeUnit.SECONDS).perSecond(150);
		
		List<Integer> colors = new ArrayList<>();
		colors.add(MaterialColors.getColor(konfettiView, R.attr.colorPrimary));
		colors.add(MaterialColors.getColor(konfettiView, R.attr.colorOnPrimary));
		colors.add(MaterialColors.getColor(konfettiView, R.attr.colorSecondary));
		colors.add(MaterialColors.getColor(konfettiView, R.attr.colorOnSecondary));
		colors.add(MaterialColors.getColor(konfettiView, R.attr.colorTertiary));
		colors.add(MaterialColors.getColor(konfettiView, R.attr.colorOnTertiary));
		
		Party party = new PartyFactory(emitterConfig)
				.angle(270)
				.spread(90)
				.setSpeedBetween(1f, 5f)
				.timeToLive(3000L)
				.position(0.0, 0.0, 1.0, 0.0)
				.colors(colors)
				.build();
		
		konfettiView.start(party);
	}
	
	@NonNull
	private StatisticsManager createStatisticsManager() {
		return new StatisticsManager(this, getGrid());
	}
	
	private void showAndStartGame(Grid currentGrid) {
		this.runOnUiThread(() -> {
			binding.gridview.setGrid(currentGrid);
			updateGameObject();
			
			ViewGroup viewGroup = findViewById(R.id.container);
			
			TransitionManager.beginDelayedTransition(viewGroup, new Fade(Fade.OUT));
			
			startFreshGrid(true);
			binding.gridview.setVisibility(View.VISIBLE);
			
			binding.gridview.reCreate();
			binding.gridview.invalidate();
			
			binding.ferrisWheelView.setVisibility(View.INVISIBLE);
			binding.ferrisWheelView.stopAnimation();
			
			TransitionManager.endTransitions(viewGroup);
		});
	}
	
	private void cheatedOnGame() {
		makeToast(R.string.toast_cheated);
		createStatisticsManager().storeStreak(false);
	}
	
	private Grid getGrid() {
		return binding.gridview.getGrid();
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
			
			saver.Save(getGrid());
		}
		
		super.onPause();
	}
	
	public void onResume() {
		loadApplicationPreferences();
		
		if (getGrid() != null && getGrid().isActive()) {
			binding.gridview.requestFocus();
			binding.gridview.invalidate();
			starttime = System.currentTimeMillis() - getGrid().getPlayTime();
			mTimerHandler.postDelayed(playTimer, 0);
		}
		super.onResume();
	}
	
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN &&
				keyCode == KeyEvent.KEYCODE_BACK && binding.gridview.isSelectorShown()) {
			binding.gridview.requestFocus();
			binding.gridview.setSelectorShown(false);
			binding.gridview.invalidate();
			
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
		
		binding.gridview.updateTheme();
		
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

		this.topFragment.setTimerVisible(ApplicationPreferences.getInstance().getPrefereneces().getBoolean("showtimer", true));
		
		insetsChanged();
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		if (getWindow() != null) {
			insets = WindowInsetsCompat.toWindowInsetsCompat(getWindow().getDecorView()
					.getRootWindowInsets());
		}
		
		insetsChanged();
	}
	
	private void insetsChanged() {
		if (insets == null) {
			return;
		}
		
		this.runOnUiThread(() -> {
			ConstraintSet constraintSet = new ConstraintSet();
			constraintSet.clone(binding.mainConstraintLayout);
			
			constraintSet.setGuidelineBegin(binding.mainTopAreaStart.getId(), getRightEdgeOfCutOutArea());
			constraintSet.setGuidelineEnd(binding.mainTopAreaEnd.getId(), insets.getInsets(WindowInsetsCompat.Type.statusBars()).right);
			
			int topAreaBottom = Math.max(
					(int)(0.25 * MainActivity.this.getResources().getDisplayMetrics().xdpi),
					insets.getInsets(WindowInsetsCompat.Type.statusBars()).bottom);
			
			constraintSet.setGuidelineBegin(binding.mainTopAreaBottom.getId(), topAreaBottom);
			
			constraintSet.applyTo(binding.mainConstraintLayout);
			
			binding.mainConstraintLayout.requestLayout();
		});
	}
	
	private int getRightEdgeOfCutOutArea() {
		DisplayCutoutCompat cutout = insets.getDisplayCutout();
		
		if (cutout == null || cutout.getBoundingRects().isEmpty()) {
			return 0;
		}
		
		return cutout.getBoundingRects().get(0).right;
	}
	
	void createNewGame() {
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
			t.setName("PreviewCalculatorFromMainNext-" + variant.getWidth() + "x" + variant.getHeight());
			t.start();
		} else {
			final Grid grid = new Grid(variant);
			binding.gridview.setGrid(grid);
			
			final Thread t = new Thread(() -> {
				if (gridSize.getAmountOfNumbers() < 2) {
					return;
				}
				
				GridCalculationService calcService = GridCalculationService.getInstance();
			
				calcService.calculateCurrentAndNextGrids(variant);
			});
			
			t.setName("PreviewCalculatorFromMainNonNext-" + variant.getWidth() + "x" + variant.getHeight());
			t.start();
		}
	}
	
	private void updateGameObject() {
		game.setGridUI(binding.gridview);
		game.setGrid(binding.gridview.getGrid());
		
		keyPadFragment.setGame(game);
		topFragment.setGame(game);
	}
	
	public synchronized void startFreshGrid(final boolean newGame) {
		undoList.clear();
		
		binding.gridview.updateTheme();
		binding.hint.setEnabled(true);
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
			
			binding.gridview.setGrid(grid);
			binding.gridview.rebuildCellsFromGrid();
			
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
			
			binding.gridview.invalidate();
			
			GridCalculationService.getInstance().setVariant(
					new GameVariant(
							binding.gridview.getGrid().getGridSize(),
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
		
		Snackbar.make(binding.hint, text, duration)
				.setAnchorView(binding.hint)
				.setAction("Undo", (view) -> {
					undoList.restoreUndo();
					binding.gridview.invalidate();
					checkProgress();
				})
				.show();
	}
	
	private void showProgress(final String string) {
		Snackbar.make(binding.hint, string, Snackbar.LENGTH_LONG)
				.setAnchorView(binding.hint)
				.show();
	}
	
	private void makeToast(final int resId) {
		Snackbar.make(binding.hint, resId, Snackbar.LENGTH_LONG)
				.setAnchorView(binding.hint)
				.show();
	}
	
	Game getGame() {
		return game;
	}
}