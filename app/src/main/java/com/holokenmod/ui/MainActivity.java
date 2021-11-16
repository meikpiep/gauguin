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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holokenmod.Grid;
import com.holokenmod.GridCell;
import com.holokenmod.R;
import com.holokenmod.SaveGame;
import com.holokenmod.StatisticsManager;
import com.holokenmod.Theme;
import com.holokenmod.UndoManager;
import com.holokenmod.Utils;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;
import com.holokenmod.options.GridCageDefaultOperation;
import com.holokenmod.options.GridCageOperation;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import mobi.glowworm.lib.ui.widget.Boast;

public class MainActivity extends Activity {
	
	private static final int UPDATE_RATE = 500;
	private static Theme theme;
	private static boolean rmpencil;
	
	private final Handler mHandler = new Handler();
	private final Handler mTimerHandler = new Handler();
	private final List<Button> numbers = new ArrayList<>();
	private final List<Button> allNumbers = new ArrayList<>();
	private Button numberExtra;
	private GridUI kenKenGrid;
	private UndoManager undoList;
	private ImageButton actionStatistics;
	private ImageButton actionUndo;
	private ImageButton actionShowCellMenu;
	private ImageButton actionShowMenu;
	private ImageButton penButton;
	private ImageButton eraserButton;
	private LinearLayout topLayout;
	private TableLayout controlKeypad;
	private RelativeLayout titleContainer;
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
		MainActivity.this.dismissDialog(0);
		MainActivity.this.startFreshGrid(true);
	};
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ApplicationPreferences.getInstance().setPreferenceManager(
				PreferenceManager.getDefaultSharedPreferences(this));
		
		PreferenceManager.setDefaultValues(this, R.xml.activity_settings, false);
		
		setContentView(R.layout.activity_main);
		
		numbers.add(findViewById(R.id.button1));
		numbers.add(findViewById(R.id.button2));
		numbers.add(findViewById(R.id.button3));
		numbers.add(findViewById(R.id.button4));
		numbers.add(findViewById(R.id.button5));
		numbers.add(findViewById(R.id.button6));
		numbers.add(findViewById(R.id.button7));
		numbers.add(findViewById(R.id.button8));
		numbers.add(findViewById(R.id.button9));
		numberExtra = findViewById(R.id.buttonExtra);
		
		allNumbers.addAll(numbers);
		allNumbers.add(numberExtra);
		
		eraserButton = findViewById(R.id.button_eraser);
		penButton = findViewById(R.id.button_pen);
		
		final ImageButton actionNewGame = findViewById(R.id.icon_new);
		actionStatistics = findViewById(R.id.icon_hint);
		actionUndo = findViewById(R.id.icon_undo);
		actionShowCellMenu = findViewById(R.id.icon_cell_menu);
		actionShowMenu = findViewById(R.id.icon_overflow);
		
		undoList = new UndoManager(actionUndo);
		
		this.kenKenGrid = findViewById(R.id.gridview);
		
		this.controlKeypad = findViewById(R.id.controls);
		this.topLayout = findViewById(R.id.container);
		this.titleContainer = findViewById(R.id.titlecontainer);
		
		this.timeView = titleContainer.findViewById(R.id.playtime);
		
		actionStatistics.setVisibility(View.INVISIBLE);
		actionUndo.setVisibility(View.INVISIBLE);
		this.controlKeypad.setVisibility(View.INVISIBLE);
		
		for (final Button numberButton : numbers) {
			addButtonListeners(numberButton);
		}
		addButtonListeners(numberExtra);
		
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
		
		penButton.setOnClickListener(v -> setSinglePossibleOnSelectedCell());
		penButton.setOnLongClickListener(v -> {
			setSinglePossibles();
			return true;
		});
		
		this.kenKenGrid.setOnGridTouchListener(cell -> {
			kenKenGrid.setSelectorShown(true);
			selectCell();
		});
		
		this.kenKenGrid.setOnLongClickListener(v -> setSinglePossibleOnSelectedCell());
		
		this.kenKenGrid.setSolvedHandler(() -> {
			mTimerHandler.removeCallbacks(playTimer);
			getGrid().setPlayTime(System.currentTimeMillis() - starttime);
			
			makeToast(getString(R.string.puzzle_solved));
			titleContainer.setBackgroundColor(0xFF0099CC);
			actionStatistics.setVisibility(View.INVISIBLE);
			actionUndo.setVisibility(View.INVISIBLE);
			
			StatisticsManager statisticsManager = new StatisticsManager(this, getGrid());
			Optional<String> recordTime = statisticsManager.storeStatisticsAfterFinishedGame();
			String recordText = getString(R.string.puzzle_record_time);
			
			recordTime.ifPresent(record ->
					makeToast(recordText + " " + record));
			
			statisticsManager.storeStreak(true);
			
			final long solvetime = getGrid().getPlayTime();
			String solveStr = Utils.convertTimetoStr(solvetime);
			timeView.setText(solveStr);
		});
		
		registerForContextMenu(actionShowCellMenu);
		registerForContextMenu(actionShowMenu);
		
		actionNewGame.setOnClickListener(v -> createNewGame());
		
		actionStatistics.setOnClickListener(v -> checkProgress());
		
		actionUndo.setOnClickListener(v -> {
			kenKenGrid.clearLastModified();
			undoList.restoreUndo();
			kenKenGrid.invalidate();
		});
		
		actionShowCellMenu.setOnClickListener(View::performLongClick);
		
		actionShowMenu.setOnClickListener(View::performLongClick);
		
		this.kenKenGrid.setFocusable(true);
		this.kenKenGrid.setFocusableInTouchMode(true);
		registerForContextMenu(this.kenKenGrid);
		
		loadApplicationPreferences();
		
		if (ApplicationPreferences.getInstance().newUserCheck()) {
			openHelpDialog();
		} else {
			final SaveGame saver = new SaveGame(this);
			restoreSaveGame(saver);
		}
	}
	
	private void addButtonListeners(Button numberButton) {
		numberButton.setOnClickListener(v -> {
			// Convert text of button (number) to Integer
			final int d = Integer.parseInt(((Button) v).getText().toString());
			enterPossibleNumber(d);
		});
		numberButton.setOnLongClickListener(v -> {
			// Convert text of button (number) to Integer
			final int d = Integer.parseInt(((Button) v).getText().toString());
			enterNumber(d);
			
			return true;
		});
	}
	
	private Grid getGrid() {
		return kenKenGrid.getGrid();
	}
	
	protected void onActivityResult(final int requestCode, final int resultCode,
									final Intent data) {
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
		if (getGrid().getGridSize() > 3) {
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
		
		GameVariant.getInstance().setShowOperators(
				ApplicationPreferences.getInstance().showOperators());
		
		if (getGrid().isActive()) {
			this.kenKenGrid.requestFocus();
			this.kenKenGrid.invalidate();
			starttime = System.currentTimeMillis() - getGrid().getPlayTime();
			mTimerHandler.postDelayed(playTimer, 0);
		}
		super.onResume();
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_save:
				final Intent i = new Intent(this, SaveGameListActivity.class);
				startActivityForResult(i, 7);
				break;
			case R.id.menu_restart_game:
				restartGameDialog();
				break;
			case R.id.menu_share:
				getScreenShot();
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
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v,
									final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v == actionShowCellMenu && getGrid().isActive()) {
			getMenuInflater().inflate(R.menu.solutions, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_main, menu);
		}
	}
	
	public boolean onContextItemSelected(final MenuItem item) {
		if (item.getGroupId() == R.id.group_overflow) {
			onOptionsItemSelected(item);
		} else {
			final GridCell selectedCell = getGrid().getSelectedCell();
			
			switch (item.getItemId()) {
				case R.id.menu_show_mistakes:
					this.kenKenGrid.markInvalidChoices();
					return true;
				case R.id.menu_reveal_cell:
					if (selectedCell == null) {
						break;
					}
					selectedCell.setUserValue(selectedCell.getValue());
					selectedCell.setCheated(true);
					this.kenKenGrid.invalidate();
					break;
				case R.id.menu_reveal_cage:
					if (selectedCell == null) {
						break;
					}
					this.kenKenGrid.solve(false);
					break;
				case R.id.menu_show_solution:
					this.kenKenGrid.solve(true);
					break;
			}
			
			makeToast(R.string.toast_cheated);
			new StatisticsManager(this, getGrid()).storeStreak(false);
		}
		return super.onContextItemSelected(item);
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
			actionShowMenu.performLongClick();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void loadApplicationPreferences() {
		rmpencil = ApplicationPreferences.getInstance().removePencils();
		theme = ApplicationPreferences.getInstance().getTheme();
		for (Button number : allNumbers) {
			if (theme == Theme.LIGHT) {
				number.setTextColor(getResources().getColorStateList(R.color.text_button));
				number.setBackgroundResource(R.drawable.keypad_button);
			} else if (theme == Theme.DARK) {
				number.setTextColor(getResources().getColorStateList(R.color.text_button_dark));
				number.setBackgroundResource(R.drawable.keypad_button_dark);
			}
		}
		
		if (theme == Theme.LIGHT) {
			eraserButton.setBackgroundResource(R.drawable.toggle_mode_bg);
			penButton.setBackgroundResource(R.drawable.toggle_mode_bg);
		} else if (theme == Theme.DARK) {
			eraserButton.setBackgroundResource(R.drawable.toggle_mode_bg_dark);
			penButton.setBackgroundResource(R.drawable.toggle_mode_bg_dark);
		}
		
		final String gridMathMode = ApplicationPreferences.getInstance().getPrefereneces()
				.getString("defaultoperations", "0");
		if (!gridMathMode.equals("ask")) {
			final SharedPreferences.Editor prefeditor = ApplicationPreferences.getInstance()
					.getPrefereneces().edit();
			prefeditor.putInt("mathmodes", Integer.parseInt(gridMathMode)).commit();
		}
		
		this.topLayout.setBackgroundColor(theme.getBackgroundColor());
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
		final String gridSizePref = ApplicationPreferences.getInstance().getPrefereneces()
				.getString("defaultgamegrid", "ask");
		final String gridMathMode = ApplicationPreferences.getInstance().getPrefereneces()
				.getString("defaultoperations", "0");
		final String gridOpMode = ApplicationPreferences.getInstance().getPrefereneces()
				.getString("defaultshowop", "true");
		
		if (gridMathMode.equals("ask") || gridOpMode.equals("ask")) {
			newGameModeDialog();
		} else if (!getGrid().isActive() && !gridSizePref.equals("ask")) {
			postNewGame(Integer.parseInt(gridSizePref));
		} else {
			newGameGridDialog();
		}
	}
	
	private void postNewGame(final int gridSize) {
		if (getGrid().isActive()) {
			new StatisticsManager(this, getGrid()).storeStreak(false);
		}
		kenKenGrid.setGrid(new Grid(gridSize));
		showDialog(0);
		final Thread t = new Thread() {
			@Override
			public void run() {
				MainActivity.this.kenKenGrid.reCreate();
				MainActivity.this.mHandler.post(newGameReady);
			}
		};
		t.start();
	}
	
	private void setButtonLabels() {
		DigitSetting digitSetting = ApplicationPreferences.getInstance().getDigitSetting();
		
		if (digitSetting == DigitSetting.FIRST_DIGIT_ZERO) {
			numberExtra.setText("0");
		} else if (getGrid().getGridSize() >= 10) {
			numberExtra.setText("10");
		}
		
		int number = 1;
		
		for (final Button numberButton : numbers) {
			numberButton.setText(Integer.toString(number));
			number++;
		}
	}
	
	private void setButtonVisibility() {
		DigitSetting digitSetting = ApplicationPreferences.getInstance().getDigitSetting();
		
		for (int i = 0; i < numbers.size(); i++) {
			numbers.get(i).setEnabled(i < digitSetting.getMaximumDigit(getGrid().getGridSize()));
		}
		
		boolean useExtraNumber = digitSetting == DigitSetting.FIRST_DIGIT_ZERO
				|| getGrid().getGridSize() >= 10;
		
		numberExtra.setEnabled(useExtraNumber);
		
		this.controlKeypad.setVisibility(View.VISIBLE);
	}
	
	private synchronized void startFreshGrid(final boolean newGame) {
		undoList.clear();
		
		this.topLayout.setBackgroundColor(theme.getBackgroundColor());
		this.kenKenGrid.setTheme(theme);
		this.actionStatistics.setVisibility(View.VISIBLE);
		this.actionUndo.setVisibility(View.INVISIBLE);
		titleContainer.setBackgroundResource(R.drawable.menu_button);
		setButtonLabels();
		setButtonVisibility();
		
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
				this.actionUndo.setVisibility(View.INVISIBLE);
				titleContainer.setBackgroundColor(0xFF0099CC);
				mTimerHandler.removeCallbacks(playTimer);
			}
			this.kenKenGrid.invalidate();
		} else {
			newGameGridDialog();
		}
	}
	
	private synchronized void enterNumber(final int number) {
		final GridCell selectedCell = getGrid().getSelectedCell();
		if (!getGrid().isActive()) {
			return;
		}
		if (selectedCell == null) {
			return;
		}
		kenKenGrid.clearLastModified();
		
		undoList.saveUndo(selectedCell, false);
		
		selectedCell.setUserValue(number);
		if (rmpencil) {
			removePossibles(selectedCell);
		}
		
		this.kenKenGrid.requestFocus();
		this.kenKenGrid.invalidate();
	}
	
	private synchronized void enterPossibleNumber(final int number) {
		final GridCell selectedCell = getGrid().getSelectedCell();
		if (!getGrid().isActive()) {
			return;
		}
		if (selectedCell == null) {
			return;
		}
		kenKenGrid.clearLastModified();
		
		undoList.saveUndo(selectedCell, false);
		
		if (selectedCell.isUserValueSet()) {
			final int oldValue = selectedCell.getUserValue();
			selectedCell.clearUserValue();
			selectedCell.togglePossible(oldValue);
		}
		
		selectedCell.togglePossible(number);
		
		this.kenKenGrid.requestFocus();
		this.kenKenGrid.invalidate();
	}
	
	private void removePossibles(final GridCell selectedCell) {
		final ArrayList<GridCell> possibleCells =
				getGrid().getPossiblesInRowCol(selectedCell);
		for (final GridCell cell : possibleCells) {
			undoList.saveUndo(cell, true);
			cell.setLastModified(true);
			cell.removePossible(selectedCell.getUserValue());
		}
	}
	
	private void setSinglePossibles() {
		ArrayList<GridCell> possibleCells =
				getGrid().getSinglePossibles();
		
		do {
			int counter = 0;
			for (final GridCell cell : possibleCells) {
				if (cell.getPossibles().size() == 1) {
					//set batch as false for first cell
					undoList.saveUndo(cell, counter++ != 0);
					
					cell.setUserValueIntern(cell.getPossibles().iterator().next());
					removePossibles(cell);
				}
			}
			possibleCells = getGrid().getSinglePossibles();
			
		} while (possibleCells.size() > 0);
		
		this.kenKenGrid.requestFocus();
		this.kenKenGrid.invalidate();
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
				removePossibles(selectedCell);
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
	
	public void getScreenShot() {
		if (!getGrid().isActive()) {
			return;
		}
		final File path = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES) + "/HoloKen/");
		if (!path.exists()) {
			path.mkdir();
		}
		
		for (final GridCell cell : getGrid().getCells()) {
			cell.setSelected(false);
		}
		kenKenGrid.setDrawingCacheEnabled(true);
		final String filename = "/holoken_" + getGrid().getGridSize() + "_" +
				new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".png";
		
		final Bitmap bitmap = kenKenGrid.getDrawingCache();
		final File file = new File(path, filename);
		try {
			file.createNewFile();
			final FileOutputStream ostream = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 90, ostream);
			ostream.flush();
			ostream.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		kenKenGrid.destroyDrawingCache();
		makeToast(getString(R.string.puzzle_screenshot) + path);
		
		// Initiate sharing dialog
		final Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/png");
		share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		startActivity(Intent.createChooser(share, getString(R.string.menu_share)));
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
		final int[] counter = getGrid().countMistakes();
		final String string = getResources().getQuantityString(R.plurals.toast_mistakes,
				counter[0], counter[0]) + " " +
				getResources().getQuantityString(R.plurals.toast_filled,
						counter[1], counter[1]);
		makeToast(string);
	}
	
	public void newGameModeDialog() {
		//Preparing views
		final View layout = getLayoutInflater().inflate(R.layout.dialog_mode,
				findViewById(R.id.mode_layout));
		final CheckBox showOps = layout.findViewById(R.id.check_show_ops);
		final RadioGroup mathModes = layout.findViewById(R.id.radio_math_modes);
		
		final GridCageDefaultOperation defaultOperations = ApplicationPreferences.getInstance().getDefaultOperations();
		
		if (defaultOperations != GridCageDefaultOperation.ASK) {
			mathModes.check(mathModes.getCheckedRadioButtonId() + defaultOperations.getId());
		}
		
		showOps.setChecked(ApplicationPreferences.getInstance().showOperators());
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(R.string.menu_new)
				.setView(layout)
				.setNegativeButton(R.string.dialog_cancel, (dialog, id) -> dialog.cancel())
				.setPositiveButton(R.string.dialog_ok, (dialog, id) -> {
					final int index = mathModes.indexOfChild(mathModes
							.findViewById(mathModes.getCheckedRadioButtonId()));
					
					GridCageOperation operation = GridCageDefaultOperation.getById(index).getOperation();
					
					GameVariant.getInstance().setCageOperation(operation);
					GameVariant.getInstance().setShowOperators(showOps.isChecked());
					
					final String gridSizePref = ApplicationPreferences.getInstance()
							.getPrefereneces().getString("defaultgamegrid", "ask");
					if (gridSizePref.equals("ask")) {
						newGameGridDialog();
					} else {
						postNewGame(Integer.parseInt(gridSizePref));
					}
				})
				.show();
	}
	
	public void newGameGridDialog() {
		final CharSequence[] items = {
				getString(R.string.grid_size_4),
				getString(R.string.grid_size_5),
				getString(R.string.grid_size_6),
				getString(R.string.grid_size_7),
				getString(R.string.grid_size_8),
				getString(R.string.grid_size_9),
				getString(R.string.grid_size_10),
		};
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(R.string.menu_new)
				.setItems(items, (dialog, item) -> MainActivity.this.postNewGame(item + 4))
				.show();
	}
	
	public void restartGameDialog() {
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
	
	public void openHelpDialog() {
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
	
	public void openAboutDialog() {
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
	
	public void makeToast(final String string) {
		Boast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show(true);
	}
	
	private void makeToast(final int resId) {
		Boast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show(true);
	}
}