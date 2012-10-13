/***************************************************************************
 *  Copyright 2012 Amanda Chow
 *  Holoken - KenKen(tm) game developed for Android 
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

package com.tortuca.holoken;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    // Define constants
    public static final int ERASER = 0;
    public static final int PEN = 1;
    public static final int PENCIL = 2;

    public static final int UPDATE_RATE = 500;
    // technically should define in colors.xml?
    public static final int BG_COLOURS[] = {0xFFf3efe7, 0xFF272727};
    public static final int TEXT_COLOURS[] = {0xF0000000, 0xFFFFFFFF};
    
    // Define variables
    public SharedPreferences preferences, stats;
    public static int theme;

    Button numbers[] = new Button[9];
    ImageButton actions[] = new ImageButton[4];
    RadioButton modes[] = new RadioButton[3];
    // eraser/pen/pencil - holo green/orange/light orange
    int modeColours[] = {0xFF99cc00,0xFFffaa33,0xccffaa33}; 

    LinearLayout topLayout, controlKeypad, solvedContainer;
    RelativeLayout titleContainer;
    TextView timeView, recordView;
    long starttime = 0;
    
    public GridView kenKenGrid;
    public int undoCellNum, undoUserValue;
    public ArrayList<Integer> undoPossibles;
    
    ProgressDialog mProgressDialog;
    final Handler mHandler = new Handler();
    final Handler mTimerHandler = new Handler();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up preferences
        PreferenceManager.setDefaultValues(this, R.xml.activity_settings, false);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.stats = getSharedPreferences("stats", MODE_PRIVATE);
        
        String themePref = this.preferences.getString("alternatetheme", "0");
        theme = Integer.parseInt(themePref);
        
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
        
        modes[ERASER] = (RadioButton)findViewById(R.id.button_eraser);
        modes[PEN] = (RadioButton)findViewById(R.id.button_pen);
        modes[PENCIL] = (RadioButton)findViewById(R.id.button_pencil);

        actions[0]= (ImageButton)findViewById(R.id.icon_new);
        actions[1]= (ImageButton)findViewById(R.id.icon_hint);
        actions[2]= (ImageButton)findViewById(R.id.icon_undo);
        actions[3]= (ImageButton)findViewById(R.id.icon_overflow);
        
        this.kenKenGrid = (GridView)findViewById(R.id.gridview);
        this.kenKenGrid.mContext = this;
        
        this.controlKeypad = (LinearLayout)findViewById(R.id.controls);
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
                    // If in eraser mode, automatically change to pencil mode
                    if (modes[ERASER].isChecked())
                        modes[PENCIL].toggle();
                    // Convert text of button (number) to Integer
                    int d = Integer.parseInt(((Button)v).getText().toString());
                    enterNumber(d);
                }
            });
        
        for (int i = 0; i<modes.length; i++)
            this.modes[i].setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    modifyCell();
                }
            });
        
        // Pen in all pencil marks/maybes on a long click
        this.modes[PEN].setOnLongClickListener(new OnLongClickListener() { 
            @Override
            public boolean onLongClick(View v) {
                for (GridCell cell : kenKenGrid.mCells)
                    if (cell.mPossibles.size() == 1)
                        cell.setUserValue(cell.mPossibles.get(0));
                    kenKenGrid.invalidate();
                    return true;
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
                    titleContainer.setBackgroundColor(0xFF33B5E5);
                    actions[1].setVisibility(View.INVISIBLE);
                    actions[2].setVisibility(View.INVISIBLE);    
                    storeStats(false);
                }
        });
        this.kenKenGrid.setFocusable(true);
        this.kenKenGrid.setFocusableInTouchMode(true);
        registerForContextMenu(this.kenKenGrid);
        
        for (int i = 0; i<actions.length; i++)
            this.actions[i].setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    switch(((ImageButton)v).getId()) {
                        case R.id.icon_new:
                            if(kenKenGrid.mActive)
                                newGameDialog();
                            else
                                createNewGame();
                            break;
                        case R.id.icon_undo:
                            restoreUndo(kenKenGrid.mCells.get(undoCellNum));
                            break;
                        case R.id.icon_hint:
                            checkProgress();
                            break;
                        case R.id.icon_overflow:
                            openOptionsMenu();
                            break;
                    }
                }
            });
        
        SaveGame saver = new SaveGame();
        restoreSaveGame(saver);
        this.topLayout.setBackgroundColor(BG_COLOURS[theme]);
        this.kenKenGrid.setTheme(theme);
        
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
            SaveGame saver = new SaveGame();
            saver.Save(this.kenKenGrid);

        }
        super.onPause();
    }
    
    public void onResume() {
        loadPreferences();
    
        this.kenKenGrid.mDupedigits = this.preferences.getBoolean("duplicates", true);
        this.kenKenGrid.mBadMaths = this.preferences.getBoolean("badmaths", true);
        this.kenKenGrid.mShowOperators = this.preferences.getBoolean("showoperators", true);
        //alternatetheme
        if (this.kenKenGrid.mActive) {
            this.kenKenGrid.requestFocus();
            this.kenKenGrid.invalidate();
            starttime = System.currentTimeMillis() - this.kenKenGrid.mPlayTime;
            mTimerHandler.postDelayed(playTimer, 0);
        }
        super.onResume();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
             case R.id.menu_new:
                 createNewGame();
                 break;               
             case R.id.menu_save:
                Intent i = new Intent(this, SaveGameListActivity.class);
                startActivityForResult(i, 7);
                 break;
                case R.id.menu_restart_game:
                    if(kenKenGrid.mGridSize > 3)
                        restartGameDialog();
                   break;        
             case R.id.menu_share:
                 if(kenKenGrid.mGridSize > 3)
                     getScreenShot();
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
        if (!kenKenGrid.mActive)
            return;
        getMenuInflater().inflate(R.menu.solutions, menu);
        return;
    }
    
    public boolean onContextItemSelected(MenuItem item) {
         GridCell selectedCell = this.kenKenGrid.mSelectedCell;
         if (selectedCell == null)
             return super.onContextItemSelected(item);
         
         switch (item.getItemId()) {
             case R.id.menu_show_mistakes:
                 this.kenKenGrid.markInvalidChoices();
                 break;
            case R.id.menu_reveal_cell:
                selectedCell.setUserValue(selectedCell.mValue);
                selectedCell.mCheated = true;
                this.kenKenGrid.invalidate();
                break;
                case R.id.menu_reveal_cage:
                    this.kenKenGrid.Solve(this.kenKenGrid.mCages.get(
                        selectedCell.mCageId).mCells, true);
                    break;
                case R.id.menu_show_solution:
                this.kenKenGrid.Solve(this.kenKenGrid.mCells,true);
                   break;
         }
         
           Toast.makeText(this, R.string.toast_cheated, Toast.LENGTH_SHORT).show();
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
        return super.onKeyDown(keyCode, event);
    }
  
    /***************************
     * Helper functions to create new game
     ***************************/  

    public void loadPreferences() {
        // Re-check preferences
        String themePref = this.preferences.getString("alternatetheme", "0");
        theme = Integer.parseInt(themePref);
        this.topLayout.setBackgroundColor(BG_COLOURS[theme]);
        for (int i = 0; i<numbers.length; i++) {
            if (theme == GridView.THEME_LIGHT) {
                numbers[i].setTextColor(getResources().getColorStateList(R.drawable.text_button));
                numbers[i].setBackgroundResource(R.drawable.keypad_button);
                if (i<modes.length)
                    modes[i].setBackgroundResource(R.drawable.radio_button);
            }
            else if (theme == GridView.THEME_DARK) {
                numbers[i].setTextColor(getResources().getColorStateList(R.drawable.text_button_dark));
                numbers[i].setBackgroundResource(R.drawable.keypad_button_dark);
                if (i<modes.length)
                    modes[i].setBackgroundResource(R.drawable.radio_button_dark);
            }
        }
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
         if (gridSizePref.equals("ask")) {
             newGameDialog();
         }
         else {
             int gridSize = Integer.parseInt(gridSizePref);     
             postNewGame(gridSize);
         }
    }

    public void postNewGame(final int gridSize) {
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
    
    // called by newGameReady and restartGameDialog
    public void startNewGame(boolean freshgrid) {
        storeStats(true);
        
        if (freshgrid) {
            this.topLayout.setBackgroundColor(BG_COLOURS[theme]);
            this.kenKenGrid.setTheme(theme);
        }

        this.actions[1].setVisibility(View.VISIBLE);
        this.actions[2].setVisibility(View.INVISIBLE);
        titleContainer.setBackgroundResource(R.drawable.menu_button);
        
        starttime = System.currentTimeMillis();
        mTimerHandler.postDelayed(playTimer, 0);
    }
    
    // Create runnable for posting
    final Runnable newGameReady = new Runnable() {
        public void run() {
            MainActivity.this.dismissDialog(0);
            MainActivity.this.setButtonVisibility(kenKenGrid.mGridSize);
            MainActivity.this.kenKenGrid.invalidate();
            MainActivity.this.startNewGame(true);
        }
    };
        
    public void setButtonVisibility(int gridSize) {
        for (int i=0; i<9; i++) {
            this.numbers[i].setEnabled(true);
            if (i>=gridSize)
                this.numbers[i].setEnabled(false);
        }    
        this.controlKeypad.setVisibility(View.VISIBLE);
    }
    
    //runs without timer be reposting self
    Runnable playTimer = new Runnable() {
        @Override
        public void run() {
           long millis = System.currentTimeMillis() - starttime;
           timeView.setText(convertTimetoStr(millis));
           mTimerHandler.postDelayed(this, UPDATE_RATE);
        }
    };

    public String convertTimetoStr(long time) {
        int seconds = (int) (time / 1000);
        int minutes = seconds / 60 % 60;
        int hours   = seconds / 3600;
        seconds     = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public void restoreSaveGame(SaveGame saver) {
        if (saver.Restore(this.kenKenGrid)) {
            this.setButtonVisibility(this.kenKenGrid.mGridSize);
            if(!this.kenKenGrid.isSolved()) {
                this.kenKenGrid.mActive = true;
                this.actions[1].setVisibility(View.VISIBLE);
                titleContainer.setBackgroundResource(R.drawable.menu_button);
            }
            else {
                this.kenKenGrid.mActive = false;
                titleContainer.setBackgroundColor(0xFF33B5E5);
                mTimerHandler.removeCallbacks(playTimer);
            }
            this.topLayout.setBackgroundColor(BG_COLOURS[theme]);
            this.kenKenGrid.setTheme(theme);
            this.kenKenGrid.invalidate();
        }
        else
            newGameDialog();
    }
    
    public void storeStats(boolean newGame) {
        if (newGame) {
            int gamestat = stats.getInt("playedgames"+kenKenGrid.mGridSize, 0);
            SharedPreferences.Editor editor = stats.edit();
            editor.putInt("playedgames"+kenKenGrid.mGridSize, gamestat+1);
            editor.commit();
        }
        else {
            int gridsize = this.kenKenGrid.mGridSize;
            
            // assess hint penalty - gridsize^2 seconds for each cell
            long penalty = kenKenGrid.countCheated()*1000*gridsize*gridsize;
            
            kenKenGrid.mPlayTime += penalty;
            long solvetime = kenKenGrid.mPlayTime;
            String solveStr = convertTimetoStr(solvetime);
            timeView.setText(solveStr);
            
            int hintedstat = stats.getInt("hintedgames"+gridsize, 0);
            int solvedstat = stats.getInt("solvedgames"+gridsize, 0);
            long timestat = stats.getLong("solvedtime"+gridsize, 0);
            SharedPreferences.Editor editor = stats.edit();

            if (penalty != 0) {
                editor.putInt("hintedgames"+gridsize, hintedstat+1);
                solveStr += "^";
            }
            else
                editor.putInt("solvedgames"+gridsize, solvedstat+1);
            
            if (timestat == 0 || timestat > solvetime) {
                editor.putLong("solvedtime"+gridsize, solvetime);
                makeToast(getString(R.string.puzzle_record_time)+" "+solveStr);
            }
            editor.commit();
        }
    }
    
    /***************************
     * Helper functions to modify KenKen grid cells
     ***************************/  
    
    public void enterNumber (int number) {
        GridCell selectedCell = this.kenKenGrid.mSelectedCell;
        if (!this.kenKenGrid.mActive)
            return;
        if (selectedCell == null)
            return;
        saveUndo(selectedCell);
        
        if (modes[PENCIL].isChecked()) {
            if (selectedCell.isUserValueSet())
                selectedCell.clearUserValue();
            selectedCell.togglePossible(number);
        }
        else {
            selectedCell.setUserValue(number);
            selectedCell.mPossibles.clear();
        }
        this.kenKenGrid.requestFocus();
        this.kenKenGrid.invalidate();
    }   
 
    public void modifyCell() {
        GridCell selectedCell = this.kenKenGrid.mSelectedCell;
        if (!this.kenKenGrid.mActive)
            return;
        if (selectedCell == null)
            return;
        
        if (modes[PEN].isChecked()) {
            selectedCell.setSelectedCellColor(modeColours[PEN]);
            if (selectedCell.mPossibles.size() == 1) {
                saveUndo(selectedCell);
                selectedCell.setUserValue(selectedCell.mPossibles.get(0));
            }
        }
        else if (modes[PENCIL].isChecked()) {
            selectedCell.setSelectedCellColor(modeColours[PENCIL]);
            if (selectedCell.isUserValueSet()) {
                saveUndo(selectedCell);
                int x = selectedCell.getUserValue();
                selectedCell.clearUserValue();
                selectedCell.togglePossible(x);
            }
        }
        else if (modes[ERASER].isChecked()) {
            selectedCell.setSelectedCellColor(modeColours[ERASER]); //green
            if (selectedCell.isUserValueSet() || selectedCell.mPossibles.size()>0) {
                saveUndo(selectedCell);
                selectedCell.mPossibles.clear();
                selectedCell.clearUserValue();
            }
        }

        this.kenKenGrid.requestFocus();
        this.kenKenGrid.invalidate();
    }       
    
    public void saveUndo(GridCell cell) {
        // save grid cell, possible stack using linked list?
        this.undoCellNum = cell.mCellNumber;
        this.undoUserValue = cell.getUserValue();
        this.undoPossibles = copyArrayList(cell.mPossibles);
        this.actions[2].setVisibility(View.VISIBLE);
    }
    
    public void restoreUndo(GridCell cell) {
        cell.setUserValue(this.undoUserValue);
        cell.mPossibles = this.undoPossibles;
        this.kenKenGrid.invalidate();
        this.actions[2].setVisibility(View.INVISIBLE);
    }
    
    public void checkProgress() {
        int counter[] = this.kenKenGrid.countMistakes();
        String string = counter[0] + " " + getString(R.string.toast_mistakes) +
                " " + counter[1] + " " + getString(R.string.toast_filled);
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
    }
    
    public ArrayList<Integer> copyArrayList(ArrayList<Integer> oldlist) {
        ArrayList<Integer> copylist = new ArrayList<Integer>(oldlist);
        Collections.copy(copylist,oldlist);
        return copylist;
    }
    
    public void getScreenShot() {
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
    
    // Create a new game dialog menu and return default grid size
    public void newGameDialog() {
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
                       MainActivity.this.postNewGame(item+4);
                   }
               })
               .show();
    }
    

    // Create a Restart Game dialog
    public void restartGameDialog() {
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
                        MainActivity.this.startNewGame(false);
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
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

}