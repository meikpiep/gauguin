package com.holokenmod.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.holokenmod.ApplicationPreferences;
import com.holokenmod.GridCell;
import com.holokenmod.R;
import com.holokenmod.SaveGame;
import com.holokenmod.Theme;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class SaveGameListAdapter extends BaseAdapter {
    
    public final ArrayList<String> mGameFiles;
    private final LayoutInflater inflater;
    private final SaveGameListActivity mContext;
    //private Typeface mFace;

    public SharedPreferences preferences;

    public SaveGameListAdapter(final SaveGameListActivity context) {
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mGameFiles = new ArrayList<String>();
        this.refreshFiles();
    }
    
    public class SortSavedGames implements Comparator<String> {
        long save1 = 0;
        long save2 = 0;
        public int compare(final String object1, final String object2) {
            try {
                save1 = new SaveGame(mContext.getFilesDir().getPath() + "/" + object1).ReadDate();
                save2 = new SaveGame(mContext.getFilesDir().getPath() + "/" + object2).ReadDate();
            }
            catch (final Exception e) {
                //
            }
            return (int) ((save2 - save1)/1000);
        }
        
    }
    
    public void refreshFiles() {
        this.mGameFiles.clear();
        final File dir = mContext.getFilesDir();
        final String[] allFiles = dir.list();
        for (final String entryName : allFiles)
            if (entryName.startsWith("savegame_"))
                this.mGameFiles.add(entryName);
        Collections.sort(this.mGameFiles, new SortSavedGames());
    }

    public int getCount() {
        return this.mGameFiles.size();
    }

    public Object getItem(final int arg0) {
        //if (arg0 == 0)
        //    return "";
        return this.mGameFiles.get(arg0);
    }

    public long getItemId(final int position) {
        return position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = inflater.inflate(R.layout.object_savegame, null);

        final GridUI grid = convertView.findViewById(R.id.saveGridView);
        final TextView gametitle = convertView.findViewById(R.id.saveGameTitle);
        final TextView datetime = convertView.findViewById(R.id.saveDateTime);

        final String saveFile = mContext.getFilesDir().getPath() + "/"+ this.mGameFiles.get(position);
        
        this.preferences = PreferenceManager.getDefaultSharedPreferences(convertView.getContext());
        grid.mActive = false;

        final Theme theme = ApplicationPreferences.getInstance().getTheme();

        convertView.findViewById(R.id.saveGameRow).setBackgroundColor(
                theme.getBackgroundColor());
        gametitle.setTextColor(theme.getTextColor());
        datetime.setTextColor(theme.getTextColor());

        final SaveGame saver = new SaveGame(saveFile);
        try {
            saver.Restore(grid);
        }
        catch (final Exception e) {
            // Error, delete the file.
            new File(saveFile).delete();
            return convertView;
        }
        grid.setBackgroundColor(0xFFFFFFFF);
        for (final GridCell cell : grid.getGrid().getCells())
            cell.setSelected(false);
        
        final long millis = grid.mPlayTime;
        gametitle.setText(String.format("%dx%d - ", grid.getGrid().getGridSize(), 
                grid.getGrid().getGridSize()) + Utils.convertTimetoStr(millis));
        
        final Calendar gameDateTime = Calendar.getInstance();
        gameDateTime.setTimeInMillis(grid.mDate);
        datetime.setText("" + DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.SHORT).format(grid.mDate));
        
        final ImageButton loadButton = convertView.findViewById(R.id.button_play);
        loadButton.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                mContext.loadSaveGame(saveFile);
            }
        });
        
        final ImageButton deleteButton = convertView.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                mContext.deleteGameDialog(saveFile);
            }
        });
        
        return convertView;
    }
}