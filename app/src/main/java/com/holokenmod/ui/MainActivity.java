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
import android.content.DialogInterface;
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

import com.holokenmod.ApplicationPreferences;
import com.holokenmod.GameVariant;
import com.holokenmod.Grid;
import com.holokenmod.GridCell;
import com.holokenmod.R;
import com.holokenmod.SaveGame;
import com.holokenmod.Theme;
import com.holokenmod.UndoList;
import com.holokenmod.UndoState;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobi.glowworm.lib.ui.widget.Boast;

public class MainActivity extends Activity {
    
    public static final int UPDATE_RATE = 500;
    public static final int MAX_UNDO_LIST = 80;
    public static Theme theme;
    public static boolean rmpencil;
    final Handler mHandler = new Handler();
    final Handler mTimerHandler = new Handler();

    public SharedPreferences stats;
    public GridUI kenKenGrid;
    public UndoList undoList = new UndoList(MAX_UNDO_LIST);
    private final List<Button> numbers = new ArrayList<>();
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
    private int lastnum = 0;

    //runs without timer be reposting self
    Runnable playTimer = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - starttime;
            timeView.setText(Utils.convertTimetoStr(millis));
            mTimerHandler.postDelayed(this, UPDATE_RATE);
        }
    };
    // Create runnable for posting
    final Runnable newGameReady = new Runnable() {
        public void run() {
            MainActivity.this.dismissDialog(0);
            MainActivity.this.startFreshGrid(true);
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApplicationPreferences.getInstance().setPreferenceManager(
                PreferenceManager.getDefaultSharedPreferences(this));

        PreferenceManager.setDefaultValues(this, R.xml.activity_settings, false);
        this.stats = getSharedPreferences("stats", MODE_PRIVATE);

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

        eraserButton = findViewById(R.id.button_eraser);
        penButton = findViewById(R.id.button_pen);

        ImageButton actionNewGame = findViewById(R.id.icon_new);
        actionStatistics = findViewById(R.id.icon_hint);
        actionUndo = findViewById(R.id.icon_undo);
        actionShowCellMenu = findViewById(R.id.icon_cell_menu);
        actionShowMenu = findViewById(R.id.icon_overflow);

        this.kenKenGrid = findViewById(R.id.gridview);

        this.controlKeypad = findViewById(R.id.controls);
        this.topLayout = findViewById(R.id.container);
        this.titleContainer = findViewById(R.id.titlecontainer);

        this.timeView = titleContainer.findViewById(R.id.playtime);

        actionStatistics.setVisibility(View.INVISIBLE);
        actionUndo.setVisibility(View.INVISIBLE);
        this.controlKeypad.setVisibility(View.INVISIBLE);

        int number = ApplicationPreferences.getInstance().getDigitSetting().getMinimumDigit();
        for (Button numberButton : numbers) {
            numberButton.setText(Integer.toString(number));
            number++;
        }
        for (Button numberButton : numbers) {
            numberButton.setOnClickListener(v -> {
                if (v.isSelected()) {
                    clearSelectedButton();
                } else {
                    // Convert text of button (number) to Integer
                    int d = Integer.parseInt(((Button) v).getText().toString());
                    enterPossibleNumber(d);
                }
            });
            numberButton.setOnLongClickListener(v -> {
                if (v.isSelected()) {
                    clearSelectedButton();
                } else {
                    // Convert text of button (number) to Integer
                    int d = Integer.parseInt(((Button) v).getText().toString());
                    enterNumber(d);
                }

                return true;
            });
        }

        eraserButton.setOnClickListener(v -> {
            GridCell selectedCell = MainActivity.this.getGrid().getSelectedCell();
            if (!MainActivity.this.kenKenGrid.mActive)
                return;
            if (selectedCell == null)
                return;

            if (selectedCell.isUserValueSet() || selectedCell.getPossibles().size()>0) {
                kenKenGrid.clearLastModified();
                saveUndo(selectedCell, false);
                selectedCell.clearUserValue();
            }
        });

        penButton.setOnClickListener(v -> setSinglePossibleOnSelectedCell());
        penButton.setOnLongClickListener(v -> setSinglePossibles());

        this.kenKenGrid.setOnGridTouchListener(cell -> {
            kenKenGrid.mSelectorShown = true;
            selectCell();
        });

        this.kenKenGrid.setOnLongClickListener(v -> setSinglePossibleOnSelectedCell());

        this.kenKenGrid.setSolvedHandler(() -> {
            mTimerHandler.removeCallbacks(playTimer);
            kenKenGrid.mPlayTime = System.currentTimeMillis() - starttime;

            makeToast(getString(R.string.puzzle_solved));
            titleContainer.setBackgroundColor(0xFF0099CC);
            actionStatistics.setVisibility(View.INVISIBLE);
            actionUndo.setVisibility(View.INVISIBLE);
            clearSelectedButton();
            storeStats(false);
            storeStreak(true);
        });
        
        registerForContextMenu(actionShowCellMenu);
        registerForContextMenu(actionShowMenu);

        actionNewGame.setOnClickListener(v -> createNewGame());

        actionStatistics.setOnClickListener(v -> checkProgress());

        actionUndo.setOnClickListener(v -> {
            kenKenGrid.clearLastModified();
            restoreUndo();
            kenKenGrid.invalidate();
        });

        actionShowCellMenu.setOnClickListener(View::performLongClick);

        actionShowMenu.setOnClickListener(View::performLongClick);

        this.kenKenGrid.setFocusable(true);
        this.kenKenGrid.setFocusableInTouchMode(true);
        registerForContextMenu(this.kenKenGrid);

        loadApplicationPreferences();

        if (newUserCheck())
            openHelpDialog();
        else {
            SaveGame saver = new SaveGame(this);
            restoreSaveGame(saver);
        }

    }

    private Grid getGrid() {
        return this.kenKenGrid.getGrid();
    }

    protected void onActivityResult(int requestCode, int resultCode,
              Intent data) {
        if (requestCode != 7 || resultCode != Activity.RESULT_OK)
            return;
        Bundle extras = data.getExtras();
        String filename = extras.getString("filename");
        Log.d("HoloKen", "Loading game: " + filename);
        SaveGame saver = new SaveGame(filename);
        restoreSaveGame(saver);
    }
    
    public void onPause() {
        if (this.kenKenGrid.getGrid().getGridSize() > 3) {
            this.kenKenGrid.mPlayTime = System.currentTimeMillis() - starttime;
            mTimerHandler.removeCallbacks(playTimer);
            // NB: saving solved games messes up the timer?
            SaveGame saver = new SaveGame(this);
            saver.Save(this.kenKenGrid);

        }
        super.onPause();
    }
    
    public void onResume() {
        loadApplicationPreferences();

        GameVariant.getInstance().setShowOperators(
                ApplicationPreferences.getInstance().showOperators());

        if (this.kenKenGrid.mActive) {
            this.kenKenGrid.requestFocus();
            this.kenKenGrid.invalidate();
            starttime = System.currentTimeMillis() - this.kenKenGrid.mPlayTime;
            mTimerHandler.postDelayed(playTimer, 0);
        }
        super.onResume();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
             case R.id.menu_save:
                 Intent i = new Intent(this, SaveGameListActivity.class);
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
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == actionShowCellMenu && kenKenGrid.mActive)
            getMenuInflater().inflate(R.menu.solutions, menu);
        else
            getMenuInflater().inflate(R.menu.activity_main, menu);
        return;
    }
    
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == R.id.group_overflow) {
            switch (item.getItemId()) {
                case R.id.menu_save:
                    Intent i = new Intent(this, SaveGameListActivity.class);
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
            }
        }
        else {
            GridCell selectedCell = getGrid().getSelectedCell();
            if (selectedCell == null)
                return super.onContextItemSelected(item);
         
            switch (item.getItemId()) {
                 case R.id.menu_show_mistakes:
                     this.kenKenGrid.markInvalidChoices();
                     return true;
                 case R.id.menu_reveal_cell:
                     selectedCell.setUserValue(selectedCell.getValue());
                     selectedCell.setCheated(true);
                     this.kenKenGrid.invalidate();
                     break;
                 case R.id.menu_reveal_cage:
                     this.kenKenGrid.solve(false);
                     break;
                 case R.id.menu_show_solution:
                     this.kenKenGrid.solve(true);
                     break;
            }
         
             makeToast(R.string.toast_cheated);
             storeStreak(false);
         }
        return super.onContextItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && 
                keyCode == KeyEvent.KEYCODE_BACK && this.kenKenGrid.mSelectorShown) {
            this.kenKenGrid.requestFocus();
            this.kenKenGrid.mSelectorShown = false;
            this.kenKenGrid.invalidate();
            return true;
        }
        else if (event.getAction() == KeyEvent.ACTION_DOWN && 
                keyCode == KeyEvent.KEYCODE_MENU) 
            actionShowMenu.performLongClick();
        return super.onKeyDown(keyCode, event);
    }
  
    /***************************
     * Helper functions to create new game
     ***************************/  

    public void loadApplicationPreferences() {
        // Re-check ApplicationPreferences
        rmpencil = ApplicationPreferences.getInstance().removePencils();
        theme = ApplicationPreferences.getInstance().getTheme();
        for (int i = 0; i<numbers.size(); i++) {
            if (theme == Theme.LIGHT) {
                numbers.get(i).setTextColor(getResources().getColorStateList(R.color.text_button));
                numbers.get(i).setBackgroundResource(R.drawable.keypad_button);
            }
            else if (theme == Theme.DARK) {
                numbers.get(i).setTextColor(getResources().getColorStateList(R.color.text_button_dark));
                numbers.get(i).setBackgroundResource(R.drawable.keypad_button_dark);
            }
        }

        if (theme == Theme.LIGHT) {
            eraserButton.setBackgroundResource(R.drawable.toggle_mode_bg);
            penButton.setBackgroundResource(R.drawable.toggle_mode_bg);
        }
        else if (theme == Theme.DARK) {
            eraserButton.setBackgroundResource(R.drawable.toggle_mode_bg_dark);
            penButton.setBackgroundResource(R.drawable.toggle_mode_bg_dark);
        }

        String gridMathMode = ApplicationPreferences.getInstance().getPrefereneces().getString("defaultoperations", "0");
        if (!gridMathMode.equals("ask")) {
            SharedPreferences.Editor prefeditor = ApplicationPreferences.getInstance().getPrefereneces().edit();
            prefeditor.putInt("mathmodes", Integer.parseInt(gridMathMode)).commit();
        }
        
        this.topLayout.setBackgroundColor(theme.getBackgroundColor());
        this.kenKenGrid.setTheme(theme);
        
        if (ApplicationPreferences.getInstance().getPrefereneces().getBoolean("keepscreenon", true))
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!ApplicationPreferences.getInstance().getPrefereneces().getBoolean("showfullscreen", false))
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (ApplicationPreferences.getInstance().getPrefereneces().getBoolean("showtimer", true))
            this.timeView.setVisibility(View.VISIBLE);
        else
            this.timeView.setVisibility(View.INVISIBLE);

    }
    
    public void createNewGame() {
        // Check ApplicationPreferences.getInstance().getPrefereneces() for new game
         String gridSizePref = ApplicationPreferences.getInstance().getPrefereneces().getString("defaultgamegrid", "ask");
         String gridMathMode = ApplicationPreferences.getInstance().getPrefereneces().getString("defaultoperations", "0");
         String gridOpMode = ApplicationPreferences.getInstance().getPrefereneces().getString("defaultshowop", "true");

         if (gridMathMode.equals("ask") || gridOpMode.equals("ask"))
             newGameModeDialog();
         else if (!kenKenGrid.mActive && !gridSizePref.equals("ask"))
             postNewGame(Integer.parseInt(gridSizePref));
         else if (kenKenGrid.mActive || gridSizePref.equals("ask"))
             newGameGridDialog();
    }

    public void postNewGame(final int gridSize) {
        if(kenKenGrid.mActive)
            storeStreak(false);
        kenKenGrid.setGrid(new Grid(gridSize));
        showDialog(0);
        Thread t = new Thread() {
            @Override
            public void run() {
                MainActivity.this.kenKenGrid.reCreate();
                MainActivity.this.mHandler.post(newGameReady);
            }
        };
        t.start();
    }

    private void setButtonVisibility(int gridSize) {
        for (int i=0; i<numbers.size(); i++) {
            numbers.get(i).setEnabled(true);
            if (i>=gridSize)
                numbers.get(i).setEnabled(false);
        }
        this.controlKeypad.setVisibility(View.VISIBLE);
    }
    
    private void clearSelectedButton() {
        if (lastnum != 0)
            numbers.get(lastnum-1).setSelected(false);
        lastnum = 0;
    }
    
    private synchronized void startFreshGrid(boolean newGame) {
        undoList.clear();
        clearSelectedButton();
        
        this.topLayout.setBackgroundColor(theme.getBackgroundColor());
        this.kenKenGrid.setTheme(theme);
        this.actionStatistics.setVisibility(View.VISIBLE);
        this.actionUndo.setVisibility(View.INVISIBLE);
        titleContainer.setBackgroundResource(R.drawable.menu_button);
        setButtonVisibility(kenKenGrid.getGrid().getGridSize());
        
        if (newGame) {
            storeStats(true);
            starttime = System.currentTimeMillis();
            mTimerHandler.postDelayed(playTimer, 0);
            if(ApplicationPreferences.getInstance().getPrefereneces().getBoolean("pencilatstart", true)) {
                for (GridCell cell : getGrid().getCells()) {
                    addAllPossibles(cell);
                }
            }
        }
    }

    private void addAllPossibles(GridCell cell) {
        for (int i : this.kenKenGrid.getGrid().getPossibleDigits()) {
                cell.addPossible(i);
            }
    }

    private void restoreSaveGame(SaveGame saver) {
        if (saver.Restore(this.kenKenGrid)) {
            startFreshGrid(false);
            if(!this.kenKenGrid.getGrid().isSolved())
                this.kenKenGrid.mActive = true;
            else {
                this.kenKenGrid.mActive = false;
                getGrid().getSelectedCell().setSelected(false);
                this.actionUndo.setVisibility(View.INVISIBLE);
                titleContainer.setBackgroundColor(0xFF0099CC);
                mTimerHandler.removeCallbacks(playTimer);
            }
            this.kenKenGrid.invalidate();
        }
        else
            newGameGridDialog();
    }
    
    private void storeStats(boolean newGame) {
        if (newGame) {
            int gamestat = stats.getInt("playedgames"+kenKenGrid.getGrid().getGridSize(), 0);
            SharedPreferences.Editor editor = stats.edit();
            editor.putInt("playedgames" + kenKenGrid.getGrid().getGridSize(), gamestat + 1);
            editor.commit();
        }
        else {
            int gridsize = this.kenKenGrid.getGrid().getGridSize();
            
            // assess hint penalty - gridsize^2/2 seconds for each cell
            long penalty = (long)kenKenGrid.getGrid().countCheated() * 500 * gridsize * gridsize;
            
            kenKenGrid.mPlayTime += penalty;
            long solvetime = kenKenGrid.mPlayTime;
            String solveStr = Utils.convertTimetoStr(solvetime);
            timeView.setText(solveStr);
            
            int hintedstat = stats.getInt("hintedgames"+gridsize, 0);
            int solvedstat = stats.getInt("solvedgames"+gridsize, 0);
            long timestat = stats.getLong("solvedtime"+gridsize, 0);
            long totaltimestat = stats.getLong("totaltime"+gridsize, 0);
            SharedPreferences.Editor editor = stats.edit();

            if (penalty != 0) {
                editor.putInt("hintedgames"+gridsize, hintedstat+1);
                solveStr += "^";
            }
            else
                editor.putInt("solvedgames"+gridsize, solvedstat+1);
            
            editor.putLong("totaltime"+gridsize, totaltimestat+solvetime);
            if (timestat == 0 || timestat > solvetime) {
                editor.putLong("solvedtime"+gridsize, solvetime);
                makeToast(getString(R.string.puzzle_record_time)+" "+solveStr);
            }
            editor.commit();
        }
    }
    
    private void storeStreak(boolean isSolved) {
        int solved_streak = stats.getInt("solvedstreak", 0);
        int longest_streak = stats.getInt("longeststreak", 0);
        SharedPreferences.Editor editor = stats.edit();
        
        if (isSolved) {
            editor.putInt("solvedstreak", solved_streak + 1);
            if (solved_streak == longest_streak)
                editor.putInt("longeststreak", solved_streak+1);
        }
        else
            editor.putInt("solvedstreak", 0);
        editor.commit();
    }
    
    private synchronized void enterNumber (int number) {
        GridCell selectedCell = getGrid().getSelectedCell();
        if (!this.kenKenGrid.mActive)
            return;
        if (selectedCell == null)
            return;
        kenKenGrid.clearLastModified();

        saveUndo(selectedCell, false);

        selectedCell.setUserValue(number);
        if (rmpencil) {
            removePossibles(selectedCell);
        }

        this.kenKenGrid.requestFocus();
        this.kenKenGrid.invalidate();
    }

    private synchronized void enterPossibleNumber (int number) {
        GridCell selectedCell = getGrid().getSelectedCell();
        if (!this.kenKenGrid.mActive)
            return;
        if (selectedCell == null)
            return;
        kenKenGrid.clearLastModified();

        saveUndo(selectedCell, false);

        if (selectedCell.isUserValueSet()) {
            int oldValue = selectedCell.getUserValue();
            selectedCell.clearUserValue();
            selectedCell.togglePossible(oldValue);
        }

        selectedCell.togglePossible(number);

        this.kenKenGrid.requestFocus();
        this.kenKenGrid.invalidate();
    }

    private void removePossibles(GridCell selectedCell) {
        ArrayList<GridCell> possibleCells =
                this.kenKenGrid.getGrid().getPossiblesInRowCol(selectedCell);
        for (GridCell cell : possibleCells) {
             saveUndo(cell, true);
             cell.setLastModified(true);
             cell.removePossible(selectedCell.getUserValue());
        }
    }

    private boolean setSinglePossibles() {
        ArrayList<GridCell> possibleCells =
                this.kenKenGrid.getGrid().getSinglePossibles();

        do {
            int counter = 0;
            for (GridCell cell : possibleCells) {
                if (cell.getPossibles().size()==1) {
                    //set batch as false for first cell
                    saveUndo(cell, counter++ != 0);

                    cell.setUserValueIntern(cell.getPossibles().iterator().next());
                    removePossibles(cell);
                }
            }
            possibleCells=this.kenKenGrid.getGrid().getSinglePossibles();

        } while (possibleCells.size() > 0);

        this.kenKenGrid.requestFocus();
        this.kenKenGrid.invalidate();

        return true;
    }

    private boolean setSinglePossibleOnSelectedCell() {
        GridCell selectedCell = getGrid().getSelectedCell();
        if (!this.kenKenGrid.mActive)
            return false;
        if (selectedCell == null)
            return false;

        if (selectedCell.getPossibles().size() == 1) {
            kenKenGrid.clearLastModified();
            saveUndo(selectedCell, false);
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
        GridCell selectedCell = getGrid().getSelectedCell();
        if (!this.kenKenGrid.mActive)
            return;
        if (selectedCell == null)
            return;

        this.kenKenGrid.requestFocus();
        this.kenKenGrid.invalidate();
    }

    private synchronized void saveUndo(GridCell cell, boolean batch) {
        UndoState undoState = new UndoState(cell,
                cell.getUserValue(), cell.getPossibles(), batch);
        undoList.add(undoState);
        this.actionUndo.setVisibility(View.VISIBLE);
    }
    
    private synchronized void restoreUndo() {
        if(!undoList.isEmpty()) {
            UndoState undoState = undoList.removeLast();
            GridCell cell = undoState.getCell();
            cell.setUserValue(undoState.getUserValue());
            cell.setPossibles(undoState.getPossibles());
            cell.setLastModified(true);
            if(undoState.getBatch())
                restoreUndo();
        }
        if(undoList.isEmpty())
            this.actionUndo.setVisibility(View.INVISIBLE);
    }
    
    public void getScreenShot() {
        if (!kenKenGrid.mActive)
            return;
        File path = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)+"/HoloKen/");
        if (!path.exists()) 
               path.mkdir();

        GridUI grid= findViewById(R.id.gridview);
        for (GridCell cell : grid.getGrid().getCells())
            cell.setSelected(false);
        grid.setDrawingCacheEnabled(true);
        String filename = "/holoken_"+ grid.getGrid().getGridSize() + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())+".png";

        //Bitmap bitmap = loadBitmapFromView(grid);
        Bitmap bitmap = grid.getDrawingCache();
        File file = new File(path,filename);
        try  {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 90, ostream);
            ostream.flush();
            ostream.close();
        } 
        catch (Exception e)          {
            e.printStackTrace();
        }
        grid.destroyDrawingCache();
        makeToast(getString(R.string.puzzle_screenshot)+path);
        
        // Initiate sharing dialog
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(share, getString(R.string.menu_share)));
    }
    
    
    /***************************
     * Functions to create various alert dialogs
     ***************************/   
    
    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_building_msg));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        return mProgressDialog;
    }
    
    public void checkProgress() {
        int counter[] = this.kenKenGrid.getGrid().countMistakes();
        String string = getResources().getQuantityString(R.plurals.toast_mistakes, 
                            counter[0], counter[0]) + " " + 
                        getResources().getQuantityString(R.plurals.toast_filled, 
                            counter[1], counter[1]);
        makeToast(string);
    }
    
    public void newGameModeDialog() {
        //Preparing views
        View layout = getLayoutInflater().inflate(R.layout.dialog_mode,
                findViewById(R.id.mode_layout));
        final CheckBox showOps = layout.findViewById(R.id.check_show_ops);
        final RadioGroup mathModes = layout.findViewById(R.id.radio_math_modes);
        
        String gridMathMode = ApplicationPreferences.getInstance().getPrefereneces().getString("defaultoperations", "0");
        if (!gridMathMode.equals("ask"))
            mathModes.check(mathModes.getCheckedRadioButtonId()+Integer.parseInt(gridMathMode));
        
        showOps.setChecked(ApplicationPreferences.getInstance().showOperators());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.menu_new)
               .setView(layout)
               .setNegativeButton(R.string.dialog_cancel, (dialog, id) -> dialog.cancel())
               .setPositiveButton(R.string.dialog_ok, (dialog, id) -> {
                   int index = mathModes.indexOfChild(mathModes.findViewById(mathModes.getCheckedRadioButtonId()));
                   ApplicationPreferences.getInstance().getPrefereneces().edit().putInt("mathmodes", index).commit();
                   GameVariant.getInstance().setShowOperators(showOps.isChecked());

                   String gridSizePref = ApplicationPreferences.getInstance().getPrefereneces().getString("defaultgamegrid", "ask");
                   if (gridSizePref.equals("ask"))
                       newGameGridDialog();
                   else
                       postNewGame(Integer.parseInt(gridSizePref));
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
        };
 
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.menu_new)
               .setItems(items, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int item) {
                       MainActivity.this.postNewGame(item + 4);
                   }
               })
               .show();
    }

    public void restartGameDialog() {
        if (!kenKenGrid.mActive)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.dialog_restart_title)
               .setMessage(R.string.dialog_restart_msg)
               .setIcon(android.R.drawable.ic_dialog_alert)
               .setNegativeButton(R.string.dialog_cancel, (dialog, id) -> dialog.cancel())
               .setPositiveButton(R.string.dialog_ok, (dialog, id) -> {
                   MainActivity.this.kenKenGrid.clearUserValues();
                   MainActivity.this.kenKenGrid.mActive = true;
                   MainActivity.this.startFreshGrid(true);
               })
               .show();
    }

    public void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View layout = inflater.inflate(R.layout.dialog_help,
                findViewById(R.id.help_layout));
        builder.setTitle(R.string.help_section_title)
               .setView(layout)
               .setNeutralButton(R.string.about_section_title, (dialog, id) -> MainActivity.this.openAboutDialog())
               .setPositiveButton(R.string.dialog_ok, (dialog, id) -> dialog.cancel())
               .show();
    }

    public void openAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View layout = inflater.inflate(R.layout.dialog_about,
                findViewById(R.id.about_layout));

        builder.setTitle(R.string.about_section_title)
               .setView(layout)
               .setNeutralButton(R.string.help_section_title, (dialog, id) -> MainActivity.this.openHelpDialog())
               .setPositiveButton(R.string.dialog_ok, (dialog, id) -> dialog.cancel())
               .show();
    }

    public void makeToast(String string) {
        Boast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show(true);
        }
    private void makeToast( int resId) {
        Boast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show(true);
    }
    
    public boolean newUserCheck() {
        boolean new_user = ApplicationPreferences.getInstance().getPrefereneces().getBoolean("newuser", true);
        if (new_user) {
          SharedPreferences.Editor prefeditor = ApplicationPreferences.getInstance().getPrefereneces().edit();
          prefeditor.putBoolean("newuser", false);
          prefeditor.commit();
        }
        return new_user;
    }

}