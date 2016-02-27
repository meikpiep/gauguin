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

package com.holokenmod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
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
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
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

import com.mobiRic.ui.widget.Boast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends Activity {
    
    // Define constants
    public static final int ERASER = 0;
    public static final int PEN = 1;
    public static final int INPUT = 2;
    public static final int PENCIL = 2;

    public static final int UPDATE_RATE = 500;
    public static final int MAX_UNDO_LIST = 80;
    public static final int BG_COLOURS[] = {0xFFf3efe7, 0xFF272727};
    public static final int TEXT_COLOURS[] = {0xF0000000, 0xFFFFFFFF};
    public static int theme;
    public static boolean rmpencil;
    final Handler mHandler = new Handler();
    final Handler mTimerHandler = new Handler();
    // Define variables
    public SharedPreferences preferences, stats;
    public GridView kenKenGrid;
    public UndoList undoList = new UndoList(MAX_UNDO_LIST);
    Button numbers[] = new Button[9];
    ImageButton actions[] = new ImageButton[4];
    ImageButton modes[] = new ImageButton[3];
    // eraser/pen/pencil - holo green/orange/light orange
    int modeColours[] = {0xFF99cc00, Color.rgb(105,105,105),0xbbffaa33};
    LinearLayout topLayout, solvedContainer;
    TableLayout controlKeypad;
    RelativeLayout titleContainer;
    TextView timeView, recordView;
    long starttime = 0;
    int lastnum = 0;
    ProgressDialog mProgressDialog;
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
        // Set up preferences
        PreferenceManager.setDefaultValues(this, R.xml.activity_settings, false);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.stats = getSharedPreferences("stats", MODE_PRIVATE);

        setContentView(R.layout.activity_main);

        // Associate variables with views
        numbers[0] = (Button)findViewById(R.id.button1);
        numbers[1] = (Button)findViewById(R.id.button2);
        numbers[2] = (Button)findViewById(R.id.button3);
        numbers[3] = (Button)findViewById(R.id.button4);
        numbers[4] = (Button)findViewById(R.id.button5);
        numbers[5] = (Button)findViewById(R.id.button6);
        numbers[6] = (Button)findViewById(R.id.button7);
        numbers[7] = (Button)findViewById(R.id.button8);
        numbers[8] = (Button)findViewById(R.id.button9);

        modes[ERASER] = (ImageButton)findViewById(R.id.button_eraser);
        modes[PEN]    = (ImageButton)findViewById(R.id.button_pen);
        modes[INPUT]  = (ImageButton)findViewById(R.id.button_input);
        modes[PEN].setSelected(true);

        actions[0]= (ImageButton)findViewById(R.id.icon_new);
        actions[1]= (ImageButton)findViewById(R.id.icon_hint);
        actions[2]= (ImageButton)findViewById(R.id.icon_undo);
        actions[3]= (ImageButton)findViewById(R.id.icon_overflow);

        this.kenKenGrid = (GridView)findViewById(R.id.gridview);
        this.kenKenGrid.mContext = this;

        this.controlKeypad = (TableLayout)findViewById(R.id.controls);
        this.topLayout = (LinearLayout)findViewById(R.id.container);
        this.titleContainer = (RelativeLayout)findViewById(R.id.titlecontainer);

        this.timeView = (TextView)titleContainer.findViewById(R.id.playtime);

        actions[1].setVisibility(View.INVISIBLE);
        actions[2].setVisibility(View.INVISIBLE);
        this.controlKeypad.setVisibility(View.INVISIBLE);

        // Set up listeners
        for (int i = 0; i<numbers.length; i++)
            this.numbers[i].setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (v.isSelected()) {
                        clearSelectedButton();
                    }
                    else {
                        // If in eraser mode, automatically change to pencil mode
                        if (modes[ERASER].isSelected()) {
                            modes[ERASER].setSelected(false);
                            modes[PEN].setSelected(false);
                            kenKenGrid.mSelectedCell.setSelectedCellColor(modeColours[PENCIL]);
                            modes[PEN].setImageResource(R.drawable.toggle_pencil);
                        }
                        // Convert text of button (number) to Integer
                        int d = Integer.parseInt(((Button)v).getText().toString());
                        enterNumber(d);
                        if (modes[INPUT].isSelected()) {
                            clearSelectedButton();
                            v.setSelected(true);
                            lastnum = d;
                        }
                    }
                }
            });

        for (int i = 0; i<modes.length; i++)
            this.modes[i].setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    switch(v.getId()) {
                        case R.id.button_eraser:
                            clearSelectedButton();
                            v.setSelected(!v.isSelected());
                            break;
                        case R.id.button_pen:
                            if(modes[ERASER].isSelected()) {
                                modes[ERASER].setSelected(false);
                            }
                            else {
                                if(v.isSelected())
                                    modes[PEN].setImageResource(R.drawable.toggle_pencil);
                                else
                                    modes[PEN].setImageResource(R.drawable.toggle_pen);
                                v.setSelected(!v.isSelected());
                            }
                            break;
                        case R.id.button_input:
                            if(v.isSelected()) {
                                modes[INPUT].setImageResource(R.drawable.toggle_grid);
                                clearSelectedButton();
                            }
                            else
                                modes[INPUT].setImageResource(R.drawable.toggle_number);
                            v.setSelected(!v.isSelected());
                            return;
                    }
                    modifyCell();
                }
            });

        // Pen in all pencil marks/maybes on a long click
        this.modes[PEN].setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return setSinglePossibles();
            }
        });

        this.kenKenGrid.setOnGridTouchListener(this.kenKenGrid.new OnGridTouchListener() {
            @Override
            public void gridTouched(GridCell cell) {
                kenKenGrid.mSelectorShown = true;
                modifyCell();
            }
        });

        this.kenKenGrid.setSolvedHandler(this.kenKenGrid.new OnSolvedListener() {
            @Override
            public void puzzleSolved() {
                mTimerHandler.removeCallbacks(playTimer);
                kenKenGrid.mPlayTime = System.currentTimeMillis() - starttime;

                makeToast(getString(R.string.puzzle_solved));
                titleContainer.setBackgroundColor(0xFF0099CC);
                actions[1].setVisibility(View.INVISIBLE);
                actions[2].setVisibility(View.INVISIBLE);
                clearSelectedButton();
                storeStats(false);
                storeStreak(true);
            }
        });
        
        registerForContextMenu(this.actions[3]);
        for (int i = 0; i<actions.length; i++)
            this.actions[i].setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    switch(v.getId()) {
                        case R.id.icon_new:
                            createNewGame();
                            break;
                        case R.id.icon_undo:
                            kenKenGrid.clearLastModified();
                            restoreUndo();
                            kenKenGrid.invalidate();
                            break;
                        case R.id.icon_hint:
                            checkProgress();
                            break;
                        case R.id.icon_overflow:
                            v.performLongClick();
                            break;
                    }
                }
            });

        this.kenKenGrid.setFocusable(true);
        this.kenKenGrid.setFocusableInTouchMode(true);
        registerForContextMenu(this.kenKenGrid);

        loadPreferences();

        if (newUserCheck())
            openHelpDialog();
        else {
            SaveGame saver = new SaveGame(this);
            restoreSaveGame(saver);
        }

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
        if (this.kenKenGrid.mGridSize > 3) {
            this.kenKenGrid.mPlayTime = System.currentTimeMillis() - starttime;
            mTimerHandler.removeCallbacks(playTimer);
            // NB: saving solved games messes up the timer?
            SaveGame saver = new SaveGame(this);
            saver.Save(this.kenKenGrid);

        }
        super.onPause();
    }
    
    public void onResume() {
        loadPreferences();
        this.kenKenGrid.mDupedigits = this.preferences.getBoolean("duplicates", true);
        this.kenKenGrid.mBadMaths = this.preferences.getBoolean("badmaths", true);
        
        String gridOpMode = preferences.getString("defaultshowop", "true");
        kenKenGrid.mShowOperators = Boolean.valueOf(gridOpMode);
        
        if (this.kenKenGrid.mActive) {
            this.kenKenGrid.requestFocus();
            this.kenKenGrid.invalidate();
            starttime = System.currentTimeMillis() - this.kenKenGrid.mPlayTime;
            mTimerHandler.postDelayed(playTimer, 0);
        }
        super.onResume();
    }
    //accidently left in from head
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
             /**case R.id.menu_new:
                 createNewGame();
              break;              **/
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
        if (v == kenKenGrid && kenKenGrid.mActive)
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
            GridCell selectedCell = this.kenKenGrid.mSelectedCell;
            if (selectedCell == null)
                return super.onContextItemSelected(item);
         
            switch (item.getItemId()) {
                 case R.id.menu_show_mistakes:
                     this.kenKenGrid.markInvalidChoices();
                     return true;
                 case R.id.menu_reveal_cell:
                     selectedCell.setUserValue(selectedCell.mValue);
                     selectedCell.mCheated = true;
                     this.kenKenGrid.invalidate();
                     break;
                 case R.id.menu_reveal_cage:
                     this.kenKenGrid.Solve(false, true);
                     break;
                 case R.id.menu_show_solution:
                     this.kenKenGrid.Solve(true, true);
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
            this.actions[3].performLongClick();
        return super.onKeyDown(keyCode, event);
    }
  
    /***************************
     * Helper functions to create new game
     ***************************/  

    public void loadPreferences() {
        // Re-check preferences
        rmpencil = this.preferences.getBoolean("removepencils", false);
        String themePref = this.preferences.getString("alternatetheme", "0");
        theme = Integer.parseInt(themePref);
        for (int i = 0; i<numbers.length; i++) {
            if (theme == GridView.THEME_LIGHT) {
                numbers[i].setTextColor(getResources().getColorStateList(R.color.text_button));
                numbers[i].setBackgroundResource(R.drawable.keypad_button);
                if (i<modes.length)
                    modes[i].setBackgroundResource(R.drawable.toggle_mode_bg);
            }
            else if (theme == GridView.THEME_DARK) {
                numbers[i].setTextColor(getResources().getColorStateList(R.color.text_button_dark));
                numbers[i].setBackgroundResource(R.drawable.keypad_button_dark);
                if (i<modes.length)
                    modes[i].setBackgroundResource(R.drawable.toggle_mode_bg_dark);
            }
        }
        
        String gridMathMode = preferences.getString("defaultoperations", "0");
        if (!gridMathMode.equals("ask")) {
            Editor prefeditor = preferences.edit();
            prefeditor.putInt("mathmodes", Integer.parseInt(gridMathMode)).commit();
        }
        
        this.topLayout.setBackgroundColor(BG_COLOURS[theme]);
        this.kenKenGrid.setTheme(theme);
        
        this.topLayout.setBackgroundColor(BG_COLOURS[theme]);
        this.kenKenGrid.setTheme(theme);

        if (this.preferences.getBoolean("keepscreenon", true))
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!this.preferences.getBoolean("showfullscreen", false))
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (this.preferences.getBoolean("showtimer", true))
            this.timeView.setVisibility(View.VISIBLE);
        else
            this.timeView.setVisibility(View.INVISIBLE);

    }
    
    public void createNewGame() {
        // Check preferences for new game
         String gridSizePref = this.preferences.getString("defaultgamegrid", "ask");
         String gridMathMode = this.preferences.getString("defaultoperations", "0");
         String gridOpMode = this.preferences.getString("defaultshowop", "true");

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
        kenKenGrid.mGridSize = gridSize;
        showDialog(0);
        Thread t = new Thread() {
            public void run() {
                MainActivity.this.kenKenGrid.reCreate();
                MainActivity.this.mHandler.post(newGameReady);
            }
        };
        t.start();
    }

        
//>>>>>>> tortucapkgchange
    public void setButtonVisibility(int gridSize) {
        for (int i=0; i<9; i++) {
            this.numbers[i].setEnabled(true);
            if (i>=gridSize)
                this.numbers[i].setEnabled(false);
        }
        this.controlKeypad.setVisibility(View.VISIBLE);
    }
    
    public void clearSelectedButton() {
        if (lastnum != 0)
            numbers[lastnum-1].setSelected(false);
        lastnum = 0;
    }
    
    // called by newGameReady, restoreSaveGame and restartGameDialog
    public synchronized void  startFreshGrid(boolean newGame) {
        undoList.clear();
        clearSelectedButton();
        
        this.topLayout.setBackgroundColor(BG_COLOURS[theme]);
        this.kenKenGrid.setTheme(theme);
        this.actions[1].setVisibility(View.VISIBLE);
        this.actions[2].setVisibility(View.INVISIBLE);
        titleContainer.setBackgroundResource(R.drawable.menu_button);
        setButtonVisibility(kenKenGrid.mGridSize);
        
        if (newGame) {
            storeStats(true);
            starttime = System.currentTimeMillis();
            mTimerHandler.postDelayed(playTimer, 0);
            for (GridCell cell:this.kenKenGrid.mCells) {
                    if(true)
                        {
                            addAllPossibles(cell);
                    }

            }
        }
    }

    private void addAllPossibles(GridCell cell) {
        for (int i = 1; i <= this.kenKenGrid.mGridSize; i++) {
                cell.mPossibles.add(i);
            }
        Collections.sort(cell.mPossibles);
    }

    public void restoreSaveGame(SaveGame saver) {
        if (saver.Restore(this.kenKenGrid)) {
            startFreshGrid(false);
            if(!this.kenKenGrid.isSolved())
                this.kenKenGrid.mActive = true;
            else {
                this.kenKenGrid.mActive = false;
                this.kenKenGrid.mSelectedCell.mSelected = false;
                this.actions[1].setVisibility(View.INVISIBLE);
                titleContainer.setBackgroundColor(0xFF0099CC);
                mTimerHandler.removeCallbacks(playTimer);
            }
            this.kenKenGrid.invalidate();
        }
        else
            newGameGridDialog();
    }
    
    public void storeStats(boolean newGame) {
        if (newGame) {
            int gamestat = stats.getInt("playedgames"+kenKenGrid.mGridSize, 0);
            SharedPreferences.Editor editor = stats.edit();
            editor.putInt("playedgames" + kenKenGrid.mGridSize, gamestat + 1);
            editor.commit();
        }
        else {
            int gridsize = this.kenKenGrid.mGridSize;
            
            // assess hint penalty - gridsize^2/2 seconds for each cell
            long penalty = kenKenGrid.countCheated()*500*gridsize*gridsize;
            
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
    
    public void storeStreak(boolean isSolved) {
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
    
    /***************************
     * Helper functions to modify KenKen grid cells
     ***************************/  
    
    public synchronized void enterNumber (int number) {
        GridCell selectedCell = this.kenKenGrid.mSelectedCell;
        if (!this.kenKenGrid.mActive)
            return;
        if (selectedCell == null)
            return;
        kenKenGrid.clearLastModified();

        saveUndo(selectedCell, false);
        if (modes[PEN].isSelected()) {
            selectedCell.setUserValue(number);
            if (rmpencil)
                removePossibles(selectedCell);
        }
        else {
            if (selectedCell.isUserValueSet())
                selectedCell.clearUserValue();
            selectedCell.togglePossible(number);
        }
        this.kenKenGrid.requestFocus();
        this.kenKenGrid.invalidate();
    }   
    
    public void removePossibles(GridCell selectedCell) {
        ArrayList<GridCell> possibleCells = 
                this.kenKenGrid.getPossiblesInRowCol(selectedCell);
        for (GridCell cell : possibleCells) {
             saveUndo(cell, true);
             cell.setLastModified(true);
             cell.removePossible(selectedCell.getUserValue());
        }
    }
    
    public boolean setSinglePossibles() {
        ArrayList<GridCell> possibleCells = 
                this.kenKenGrid.getSinglePossibles();
        int counter = 0;
        for (GridCell cell : possibleCells) {
            //set batch as false for first cell
            saveUndo(cell, counter++ != 0);
            //TODO: incorporate highlighting into my chain removal
//<<<<<<< HEAD
            cell.setUserValue(cell.mPossibles.get(0));
            removePossibles(cell);
//=======
//            cell.setLastModified(true);
//            cell.setUserValue(cell.mPossibles.get(0));
//>>>>>>> tortucapkgchange
        }
        this.kenKenGrid.requestFocus();
        this.kenKenGrid.invalidate();
        return true;
    }
    
    
    public synchronized void modifyCell() {
        GridCell selectedCell = this.kenKenGrid.mSelectedCell;
        if (!this.kenKenGrid.mActive)
            return;
        if (selectedCell == null)
            return;
        //kenKenGrid.clearLastModified();
        
        if (modes[ERASER].isSelected()) {
            selectedCell.setSelectedCellColor(modeColours[ERASER]); //green
            if (selectedCell.isUserValueSet() || selectedCell.mPossibles.size()>0) {
                kenKenGrid.clearLastModified();
                saveUndo(selectedCell, false);
                selectedCell.clearUserValue();
            }
        }
        else {   
            if (modes[INPUT].isSelected() && lastnum != 0)
                enterNumber(lastnum);
            if (modes[PEN].isSelected()) {
                selectedCell.setSelectedCellColor(modeColours[PEN]);
                if (selectedCell.mPossibles.size() == 1) {
                    kenKenGrid.clearLastModified();
                    saveUndo(selectedCell, false);
                    selectedCell.setUserValue(selectedCell.mPossibles.get(0));
                    if (rmpencil)
                        removePossibles(selectedCell);
                }
            }
            else {
                selectedCell.setSelectedCellColor(modeColours[PENCIL]);
                if(selectedCell.isUserValueSet()) {
                    kenKenGrid.clearLastModified();
                    saveUndo(selectedCell, false);
                    selectedCell.toggleUserValue();
                }
            }
        }
        this.kenKenGrid.requestFocus();
        this.kenKenGrid.invalidate();
    }
    
    public synchronized void saveUndo(GridCell cell, boolean batch) {
        UndoState undoState = new UndoState(cell.mCellNumber, 
                cell.getUserValue(), cell.mPossibles, batch);
        undoList.add(undoState);
        this.actions[2].setVisibility(View.VISIBLE);
    }
    
    public synchronized void restoreUndo() {
        if(!undoList.isEmpty()) {
            UndoState undoState = undoList.removeLast();
            GridCell cell = kenKenGrid.mCells.get(undoState.getCellNum());
            cell.setUserValue(undoState.getUserValue());
            cell.mPossibles = undoState.getPossibles();
            cell.setLastModified(true);
            if(undoState.getBatch())
                restoreUndo();
        }
        if(undoList.isEmpty())
            this.actions[2].setVisibility(View.INVISIBLE);
    }
    
    public void getScreenShot() {
        if (!kenKenGrid.mActive)
            return;
        File path = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)+"/HoloKen/");
        if (!path.exists()) 
               path.mkdir();

        GridView grid= (GridView)findViewById(R.id.gridview);
        for (GridCell cell : grid.mCells)
            cell.mSelected = false;
        grid.setDrawingCacheEnabled(true);
        String filename = "/holoken_"+ grid.mGridSize + "_" +
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
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_building_msg));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        return mProgressDialog;
    }
    
    public void checkProgress() {
        int counter[] = this.kenKenGrid.countMistakes();
        String string = getResources().getQuantityString(R.plurals.toast_mistakes, 
                            counter[0], counter[0]) + " " + 
                        getResources().getQuantityString(R.plurals.toast_filled, 
                            counter[1], counter[1]);
        makeToast( string);
    }
    
    public void newGameModeDialog() {
        //Preparing views
        View layout = getLayoutInflater().inflate(R.layout.dialog_mode, 
                (ViewGroup) findViewById(R.id.mode_layout));
        final CheckBox showOps = (CheckBox) layout.findViewById(R.id.check_show_ops);
        final RadioGroup mathModes = (RadioGroup) layout.findViewById(R.id.radio_math_modes);
        
        String gridMathMode = preferences.getString("defaultoperations", "0");
        if (!gridMathMode.equals("ask"))
            mathModes.check(mathModes.getCheckedRadioButtonId()+Integer.parseInt(gridMathMode));
        
        String gridOpMode = preferences.getString("defaultshowop", "true");
        showOps.setChecked(Boolean.valueOf(gridOpMode));
        
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.menu_new)
               .setView(layout)
               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               })
               .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       int index = mathModes.indexOfChild(mathModes.findViewById(mathModes.getCheckedRadioButtonId()));
                       preferences.edit().putInt("mathmodes", index).commit();
                       kenKenGrid.mShowOperators = showOps.isChecked();

                       String gridSizePref = preferences.getString("defaultgamegrid", "ask");
                       if (gridSizePref.equals("ask"))
                           newGameGridDialog();
                       else
                           postNewGame(Integer.parseInt(gridSizePref));
                   }
               })
               .show();
    }
    // Create a new game dialog menu and return default grid size
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

    // Create a Restart Game dialog
    public void restartGameDialog() {
        if (!kenKenGrid.mActive)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.dialog_restart_title)
               .setMessage(R.string.dialog_restart_msg)
               .setIcon(android.R.drawable.ic_dialog_alert)
               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               })
               .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       MainActivity.this.kenKenGrid.clearUserValues();
                       MainActivity.this.kenKenGrid.mActive = true;
                       MainActivity.this.startFreshGrid(true);
                   }
               })
               .show();
    }

    // Create a Help dialog
    public void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View layout = inflater.inflate(R.layout.dialog_help,
                                       (ViewGroup) findViewById(R.id.help_layout));
        builder.setTitle(R.string.help_section_title)
               .setView(layout)
               .setNeutralButton(R.string.about_section_title, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       MainActivity.this.openAboutDialog();
                   }
               })
               .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               })
               .show();
    }

    // Create a About dialog
    public void openAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View layout = inflater.inflate(R.layout.dialog_about,
                                       (ViewGroup) findViewById(R.id.about_layout));

        builder.setTitle(R.string.about_section_title)
               .setView(layout)
               .setNeutralButton(R.string.help_section_title, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.openHelpDialog();
                   }
               })
               .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               })
               .show();
    }

    public void makeToast(String string) {
        Boast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show(true);
        }
    private void makeToast( int resId) {
        Boast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show(true);
    }
    
    public boolean newUserCheck() {
        boolean new_user = preferences.getBoolean("newuser", true);
        if (new_user) {
          Editor prefeditor = preferences.edit();
          prefeditor.putBoolean("newuser", false);
          prefeditor.commit();
        }
        return new_user;
    }

}