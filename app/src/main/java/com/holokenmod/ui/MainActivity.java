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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.holokenmod.Game;
import com.holokenmod.Grid;
import com.holokenmod.GridCell;
import com.holokenmod.GridSize;
import com.holokenmod.R;
import com.holokenmod.SaveGame;
import com.holokenmod.StatisticsManager;
import com.holokenmod.Theme;
import com.holokenmod.UndoManager;
import com.holokenmod.Utils;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.GameVariant;

import java.io.File;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {
	
	private static final int UPDATE_RATE = 500;
	private static Theme theme;
	private static boolean rmpencil;
	
	private final Handler mHandler = new Handler();
	private final Handler mTimerHandler = new Handler();
	private GridUI kenKenGrid;
	private UndoManager undoList;
	private FloatingActionButton actionStatistics;
	private View actionUndo;
	private TextView timeView;
	private MaterialButton useBookmark;
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
		MainActivity.this.dismissDialog(0);
		MainActivity.this.startFreshGrid(true);
	};
	
	private Game game;
	private KeyPadFragment keyPadFragment;
	private DrawerLayout drawerLayout;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		
		ApplicationPreferences.getInstance().setPreferenceManager(
				PreferenceManager.getDefaultSharedPreferences(this));
		
		PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
		
		setContentView(R.layout.activity_main);
		
		GameVariant.getInstance().setShowOperators(
				ApplicationPreferences.getInstance().showOperators());
		
		GameVariant.getInstance().setCageOperation(
				ApplicationPreferences.getInstance().getOperations());
		
		GameVariant.getInstance().setDigitSetting(
				ApplicationPreferences.getInstance().getDigitSetting());
		
		GameVariant.getInstance().setSingleCageUsage(
				ApplicationPreferences.getInstance().getSingleCageUsage());
		
		Button eraserButton = findViewById(R.id.button_eraser);
		
		actionUndo = findViewById(R.id.undo);
		if (actionUndo == null) {
			actionUndo = findViewById(R.id.undoFromMainActivity);
		}
		
		actionStatistics = findViewById(R.id.hint);
		
		undoList = new UndoManager(actionUndo);
		
		this.kenKenGrid = findViewById(R.id.gridview);
		
		this.timeView = findViewById(R.id.playtime);
		
		//actionStatistics.setEnabled(false);
		actionUndo.setEnabled(false);
		
		eraserButton.setOnClickListener(v -> {
			final GridCell selectedCell = MainActivity.this.getGrid().getSelectedCell();
			if (!getGrid().isActive()) {
				return;
			}
			if (selectedCell == null) {
				return;
			}
			
			if (selectedCell.isUserValueSet() || selectedCell.getPossibles().size() > 0) {
				kenKenGrid.clearLastModified();
				undoList.saveUndo(selectedCell, false);
				selectedCell.clearUserValue();
			}
		});
		
		MaterialButton addBookmark = findViewById(R.id.button_add_bookmark);
		addBookmark.setOnClickListener((view) -> addBookmark());
		
		useBookmark = findViewById(R.id.button_use_bookmark);
		useBookmark.setOnClickListener((view) -> useBookmark());
		useBookmark.setEnabled(false);
		
		MaterialButton toogleFlaky = findViewById(R.id.button_toogle_flaky);
		toogleFlaky.setOnClickListener((view) -> {
			if (kenKenGrid.getGrid().isActive()) {
				GridCell selected = kenKenGrid.getGrid().getSelectedCell();
				
				if (selected != null) {
					undoList.saveUndo(selected, false);
					selected.toogleFlaky();
					
					kenKenGrid.invalidate();
				}
			}
		});
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		keyPadFragment = new KeyPadFragment();
		
		ft.replace(R.id.container33, keyPadFragment);
		ft.commit();
		
		this.kenKenGrid.setOnGridTouchListener(cell -> {
			kenKenGrid.setSelectorShown(true);
			selectCell();
		});
		
		this.kenKenGrid.setOnLongClickListener(v -> setSinglePossibleOnSelectedCell());
		
		this.kenKenGrid.setSolvedHandler(() -> {
			mTimerHandler.removeCallbacks(playTimer);
			getGrid().setPlayTime(System.currentTimeMillis() - starttime);
			
			showProgress(getString(R.string.puzzle_solved));
			actionStatistics.setEnabled(false);
			actionUndo.setEnabled(false);
			
			StatisticsManager statisticsManager = new StatisticsManager(this, getGrid());
			Optional<String> recordTime = statisticsManager.storeStatisticsAfterFinishedGame();
			String recordText = getString(R.string.puzzle_record_time);
			
			recordTime.ifPresent(record ->
					showProgress(recordText + " " + record));
			
			statisticsManager.storeStreak(true);
			
			final long solvetime = getGrid().getPlayTime();
			String solveStr = Utils.convertTimetoStr(solvetime);
			timeView.setText(solveStr);
		});
		
		this.kenKenGrid.setFocusable(true);
		this.kenKenGrid.setFocusableInTouchMode(true);
		registerForContextMenu(this.kenKenGrid);
		
		actionStatistics.setOnClickListener(v -> checkProgress());
		
		actionUndo.setOnClickListener(v -> {
			kenKenGrid.clearLastModified();
			undoList.restoreUndo();
			kenKenGrid.invalidate();
		});
		
		BottomAppBar appBar = findViewById(R.id.topAppBar);
		NavigationView navigationView = findViewById(R.id.mainNavigationView);
		drawerLayout = findViewById(R.id.container);
		
		MaterialButton openDrawerButton = findViewById(R.id.openDrawerFromMainActivity);
		
		if (openDrawerButton != null) {
			openDrawerButton.setOnClickListener((view) -> drawerLayout.open());
		}
		
		navigationView.setNavigationItemSelectedListener((menuItem) -> {
			switch (menuItem.getItemId()) {
				case R.id.newGame2:
					createNewGame();
					break;
				case R.id.menu_show_mistakes:
					this.kenKenGrid.markInvalidChoices();
					cheatedOnGame();
					return true;
				case R.id.menu_reveal_cell:
					final GridCell selectedCell = getGrid().getSelectedCell();
					
					if (selectedCell == null) {
						break;
					}
					selectedCell.setUserValue(selectedCell.getValue());
					selectedCell.setCheated(true);
					this.kenKenGrid.invalidate();
					cheatedOnGame();
					break;
				case R.id.menu_reveal_cage:
					final GridCell selected = getGrid().getSelectedCell();
					
					if (selected == null) {
						break;
					}
					this.kenKenGrid.solve(false);
					cheatedOnGame();
					break;
				case R.id.menu_show_solution:
					this.kenKenGrid.solve(true);
					cheatedOnGame();
					break;
				case R.id.menu_save:
					final Intent i = new Intent(this, SaveGameListActivity.class);
					startActivityForResult(i, 7);
					break;
				case R.id.menu_restart_game:
					restartGameDialog();
					break;
				case R.id.menu_stats:
					startActivity(new Intent(this, StatsActivity.class));
					break;
				case R.id.menu_settings:
					startActivity(new Intent(this, SettingsActivity.class));
					break;
				case R.id.menu_help:
					openHelpDialog();
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
				switch (menuItem.getItemId()) {
					case R.id.hint:
						checkProgress();
						break;
					case R.id.undo:
						kenKenGrid.clearLastModified();
						undoList.restoreUndo();
						kenKenGrid.invalidate();
						break;
					default:
						break;
				}
				
				return true;
			});
			
			appBar.setNavigationOnClickListener((view) -> {
				drawerLayout.open();
			});
		}
		
		loadApplicationPreferences();
		
		if (ApplicationPreferences.getInstance().newUserCheck()) {
			openHelpDialog();
		} else {
			final SaveGame saver = new SaveGame(this);
			restoreSaveGame(saver);
		}
	}
	
	private void addBookmark() {
		undoList.saveBookmark();
		useBookmark.setEnabled(true);
	}
	
	private void useBookmark() {
		undoList.restoreBookmark();
		kenKenGrid.invalidate();
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
		
		if (data != null) {
			final Bundle extras = data.getExtras();
			final String gridSizeString = extras.getString(Intent.EXTRA_TEXT);
		
			postNewGame(GridSize.create(gridSizeString));
			
			return;
		}
		
		if (requestCode != 7 || resultCode != Activity.RESULT_OK) {
			return;
		}
		
		final Bundle extras = data.getExtras();
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
		} else if (event.getAction() == KeyEvent.ACTION_DOWN &&
				keyCode == KeyEvent.KEYCODE_MENU) {
			//Todo: When is this calles?
			//actionShowMenu.performLongClick();
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
		newGameGridDialog();
	}
	
	void postNewGame(final GridSize gridSize) {
		if (getGrid() != null && getGrid().isActive()) {
			new StatisticsManager(this, getGrid()).storeStreak(false);
		}
		
		Grid grid = new Grid(gridSize);
		kenKenGrid.setGrid(grid);
		
		showDialog(0);
		final Thread t = new Thread() {
			@Override
			public void run() {
				MainActivity.this.kenKenGrid.reCreate();
				
				createGameObject();
				
				MainActivity.this.mHandler.post(newGameReady);
			}
		};
		t.start();
	}
	
	private void createGameObject() {
		game = new Game(kenKenGrid.getGrid(), kenKenGrid, undoList);
		
		keyPadFragment.setGame(game);
	}
	
	private synchronized void startFreshGrid(final boolean newGame) {
		undoList.clear();
		
		this.kenKenGrid.setTheme(theme);
		this.actionStatistics.setEnabled(true);
		this.actionUndo.setEnabled(false);
		
		if (newGame) {
			new StatisticsManager(this, getGrid()).storeStatisticsAfterNewGame();
			starttime = System.currentTimeMillis();
			mTimerHandler.postDelayed(playTimer, 0);
			if (ApplicationPreferences.getInstance().getPrefereneces()
					.getBoolean("pencilatstart", true)) {
				for (final GridCell cell : getGrid().getCells()) {
					addAllPossibles(cell);
				}
			}
		}
	}
	
	private void addAllPossibles(final GridCell cell) {
		for (final int i : getGrid().getPossibleDigits()) {
			cell.addPossible(i);
		}
	}
	
	private void restoreSaveGame(final SaveGame saver) {
		if (saver.restore(this.kenKenGrid)) {
			startFreshGrid(false);
			if (!getGrid().isSolved()) {
				getGrid().setActive(true);
			} else {
				getGrid().setActive(false);
				getGrid().getSelectedCell().setSelected(false);
				this.actionUndo.setEnabled(false);
				mTimerHandler.removeCallbacks(playTimer);
			}
			
			createGameObject();
			
			this.kenKenGrid.invalidate();
		} else {
			newGameGridDialog();
		}
	}
	
	private boolean setSinglePossibleOnSelectedCell() {
		final GridCell selectedCell = getGrid().getSelectedCell();
		if (!getGrid().isActive()) {
			return false;
		}
		if (selectedCell == null) {
			return false;
		}
		
		if (selectedCell.getPossibles().size() == 1) {
			kenKenGrid.clearLastModified();
			undoList.saveUndo(selectedCell, false);
			selectedCell.setUserValue(selectedCell.getPossibles().iterator().next());
			if (rmpencil) {
				game.removePossibles(selectedCell);
			}
		}
		
		this.kenKenGrid.requestFocus();
		this.kenKenGrid.invalidate();
		return true;
	}
	
	private synchronized void selectCell() {
		final GridCell selectedCell = getGrid().getSelectedCell();
		if (!getGrid().isActive()) {
			return;
		}
		if (selectedCell == null) {
			return;
		}
		
		this.kenKenGrid.requestFocus();
		this.kenKenGrid.invalidate();
	}
	
	@Override
	protected Dialog onCreateDialog(final int id) {
		final ProgressDialog mProgressDialog = new ProgressDialog(this);
		
		mProgressDialog.setMessage(getResources().getString(R.string.dialog_building_msg));
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setCancelable(false);
		
		return mProgressDialog;
	}
	
	public void checkProgress() {
		final int mistakes = getGrid().getNumberOfMistakes();
		final int filled = getGrid().getNumberOfFilledCells();

		final String text = getResources().getQuantityString(R.plurals.toast_mistakes,
				mistakes, mistakes) + " " +
				getResources().getQuantityString(R.plurals.toast_filled,
						filled, filled);
		
		int duration;
		
		if (mistakes == 0) {
			duration = 1500;
		} else {
			duration = 4000;
		}
		
		Snackbar.make(actionUndo, text, duration)
				.setAnchorView(actionUndo)
				.setAction("Undo", (view) -> {
					undoList.restoreUndo();
					kenKenGrid.invalidate();
					checkProgress();
				})
				.show();
	}
	
	private void newGameGridDialog() {
		Intent intent = new Intent(this, NewGameActivity.class);
		
		startActivityForResult(intent, 0);
	}
	
	private void restartGameDialog() {
		if (!getGrid().isActive()) {
			return;
		}
		final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(R.string.dialog_restart_title)
				.setMessage(R.string.dialog_restart_msg)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setNegativeButton(R.string.dialog_cancel, (dialog, id) -> dialog.cancel())
				.setPositiveButton(R.string.dialog_ok, (dialog, id) -> {
					MainActivity.this.kenKenGrid.clearUserValues();
					getGrid().setActive(true);
					MainActivity.this.startFreshGrid(true);
				})
				.show();
	}
	
	private void openHelpDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		final View layout = inflater.inflate(R.layout.dialog_help,
				findViewById(R.id.help_layout));
		builder.setTitle(R.string.help_section_title)
				.setView(layout)
				.setNeutralButton(R.string.about_section_title, (dialog, id) -> MainActivity.this
						.openAboutDialog())
				.setPositiveButton(R.string.dialog_ok, (dialog, id) -> dialog.cancel())
				.show();
	}
	
	private void openAboutDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		final View layout = inflater.inflate(R.layout.dialog_about,
				findViewById(R.id.about_layout));
		
		builder.setTitle(R.string.about_section_title)
				.setView(layout)
				.setNeutralButton(R.string.help_section_title, (dialog, id) -> MainActivity.this
						.openHelpDialog())
				.setPositiveButton(R.string.dialog_ok, (dialog, id) -> dialog.cancel())
				.show();
	}
	
	private void showProgress(final String string) {
		Snackbar.make(actionUndo, string, Snackbar.LENGTH_LONG)
				.setAnchorView(actionUndo)
				.show();
	}
	
	private void makeToast(final int resId) {
		Snackbar.make(actionUndo, resId, Snackbar.LENGTH_LONG)
				.setAnchorView(actionUndo)
				.show();
	}
}