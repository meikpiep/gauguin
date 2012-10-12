package com.tortuca.holoken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SaveGameListActivity extends ListActivity {
    public static final String SAVEGAME_DIR = "/data/data/com.tortuca.holoken/";
    public static final String SAVEGAME_AUTO = SAVEGAME_DIR + "autosave";
    public static final String SAVEGAME_PREFIX_ = SAVEGAME_DIR + "savegame_";

    private SaveGameListAdapter mAdapter;
    public boolean mCurrentSaved;
    TextView empty;
    ListView saveGameList;
    ImageButton discardButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("showfullscreen", false))
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_savegame);
        final Button saveButton =(Button) findViewById(R.id.savebutton);
        discardButton =(ImageButton) findViewById(R.id.discardbutton);
        empty = (TextView)findViewById(android.R.id.empty);
        saveGameList = (ListView) findViewById(android.R.id.list);

        String themePref = PreferenceManager.getDefaultSharedPreferences(this).getString("alternatetheme", "0");
        int theme = Integer.parseInt(themePref);
        this.findViewById(R.id.saveGameContainer).setBackgroundColor(
                MainActivity.BG_COLOURS[theme]);
        /*if (theme == GridView.THEME_LIGHT)
            saveButton.setTextColor(R.drawable.text_button);
        if (theme == GridView.THEME_DARK)
            saveButton.setTextColor(R.drawable.text_button_dark);
         */
        saveButton.setTextColor(MainActivity.TEXT_COLOURS[theme]);

        saveGameList.setEmptyView(empty);
        this.mAdapter = new SaveGameListAdapter(this);
        saveGameList.setAdapter(this.mAdapter);

        saveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                saveButton.setEnabled(false);
                currentSaveGame();
            }
        });

        if (this.mCurrentSaved)
            saveButton.setEnabled(false);

        discardButton.setEnabled(false);
        if (mAdapter.getCount() != 0)
            discardButton.setEnabled(true);

        discardButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                deleteAllGamesDialog();
            }
        });
    }

    public void deleteSaveGame(final String filename) {
        new File(filename).delete();
        mAdapter.refreshFiles();
        mAdapter.notifyDataSetChanged();
    }

    public void deleteAllSaveGames() {
        File dir = new File(SAVEGAME_DIR);
        String[] allFiles = dir.list();
        for (String entryName : allFiles)
            if (entryName.startsWith("savegame_"))
                new File(dir + "/" + entryName).delete();
        mAdapter.refreshFiles();
        mAdapter.notifyDataSetChanged();

        discardButton.setEnabled(false);
    }

    public void deleteGameDialog(final String filename) {
        new AlertDialog.Builder(SaveGameListActivity.this)
        .setTitle(R.string.dialog_delete_title)
        .setMessage(R.string.dialog_delete_msg)
        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
        })
        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    SaveGameListActivity.this.deleteSaveGame(filename);
                }
        })
        .show();
    }

    public void deleteAllGamesDialog() {
        new AlertDialog.Builder(SaveGameListActivity.this)
        .setTitle(R.string.dialog_delete_all_title)
        .setMessage(R.string.dialog_delete_all_msg)
        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
        })
        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    SaveGameListActivity.this.deleteAllSaveGames();
                }
        })
        .show();
    }

    public void loadSaveGame(String filename) {
        Intent i = new Intent().putExtra("filename", filename);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    public void currentSaveGame() {
        this.mCurrentSaved = true;
        int fileIndex;

        for (fileIndex = 0 ; ; fileIndex++)
            if (! new File(SAVEGAME_PREFIX_ + fileIndex).exists())
                break;
        String filename = SAVEGAME_PREFIX_ + fileIndex;
        try {
            this.copy(new File(SAVEGAME_AUTO), new File(filename));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.mAdapter.refreshFiles();
        this.mAdapter.notifyDataSetChanged();
    }


    void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
