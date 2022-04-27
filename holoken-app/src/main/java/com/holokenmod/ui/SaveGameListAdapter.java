package com.holokenmod.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.holokenmod.Grid;
import com.holokenmod.GridCell;
import com.holokenmod.R;
import com.holokenmod.SaveGame;
import com.holokenmod.Theme;
import com.holokenmod.options.ApplicationPreferences;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

public class SaveGameListAdapter extends RecyclerView.Adapter<SaveGameListAdapter.ViewHolder> {
    
    private final ArrayList<File> mGameFiles;
    private final LayoutInflater inflater;
    private ItemClickListener clickListener;
    private final SaveGameListActivity mContext;
    
    public SaveGameListAdapter(final SaveGameListActivity context) {
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mGameFiles = new ArrayList<>();
        this.refreshFiles();
    }
    
    public void refreshFiles() {
        this.mGameFiles.clear();
        this.mGameFiles.addAll(mContext.getSaveGameFiles());
        
        Collections.sort(this.mGameFiles, new SortSavedGames());
    }
    
    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.object_savegame, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final File saveFile = this.mGameFiles.get(position);
    
        final SaveGame saver = new SaveGame(saveFile);
        try {
            Optional<Grid> optionalGrid = saver.restore();
    
            if (optionalGrid.isPresent()) {
                Grid grid = optionalGrid.get();
    
                holder.gridUI.setGrid(grid);
                holder.gridUI.rebuidCellsFromGrid();
            }
        } catch (final Exception e) {
            // Error, delete the file.
            saveFile.delete();
            return;
        }
    
        final Theme theme = ApplicationPreferences.getInstance().getTheme();
    
        Grid grid = holder.gridUI.getGrid();
    
        grid.setActive(false);
        holder.gridUI.setTheme(theme);
    
        for (final GridCell cell : grid.getCells()) {
            cell.setSelected(false);
        }
    
        final long millis = grid.getPlayTime();
        holder.gametitle.setText(grid.getGridSize().toString());
        //+ " " + Utils.convertTimetoStr(millis)
        
        final Calendar gameDateTime = Calendar.getInstance();
        gameDateTime.setTimeInMillis(grid.getCreationDate());
        holder.date.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(grid.getCreationDate()));
        holder.time.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(grid.getCreationDate()));
    
        holder.loadButton.setOnClickListener(v -> mContext.loadSaveGame(saveFile));
        holder.deleteButton.setOnClickListener(v -> mContext.deleteGameDialog(saveFile));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mGameFiles.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        GridUI gridUI;
        TextView gametitle;
        TextView date;
        TextView time;
        MaterialButton loadButton;
        MaterialButton deleteButton;
        
        ViewHolder(View itemView) {
            super(itemView);
            
            gridUI = itemView.findViewById(R.id.saveGridView);
            gametitle = itemView.findViewById(R.id.saveGameTitle);
            date = itemView.findViewById(R.id.saveDate);
            time = itemView.findViewById(R.id.saveTime);
            
            loadButton = itemView.findViewById(R.id.button_play);
            deleteButton = itemView.findViewById(R.id.button_delete);
            
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
			if (clickListener != null) {
				clickListener.onItemClick(view, getAdapterPosition());
			}
        }
    }

    // convenience method for getting data at click position
    File getItem(int id) {
        return mGameFiles.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    
    private static class SortSavedGames implements Comparator<File> {
        long save1 = 0;
        long save2 = 0;
        
        public int compare(final File object1, final File object2) {
            try {
                save1 = new SaveGame(object1).ReadDate();
                save2 = new SaveGame(object2).ReadDate();
            } catch (final Exception e) {
                //
            }
            return (int) Math.signum(save2 - save1);
        }
        
    }
}