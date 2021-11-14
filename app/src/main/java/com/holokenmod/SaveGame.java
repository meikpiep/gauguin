package com.holokenmod;

import android.content.Context;
import android.util.Log;

import com.holokenmod.ui.GridCellUI;
import com.holokenmod.ui.GridUI;
import com.holokenmod.ui.SaveGameListActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SaveGame {
    private Context context;
    private final File filename;

    public SaveGame(final Context context) {
        this.context=context;
        this.filename = getAutosave();
    }
    public SaveGame(final File file) {
        this.filename = file;

    }

    public void Save(final GridUI view) {
        synchronized (view.mLock) {    // Avoid saving game at the same time as creating puzzle
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename))) {
                final long now = System.currentTimeMillis();
                writer.write(now + "\n");
                writer.write(view.getGrid().getGridSize() + "\n");
                writer.write(view.mPlayTime + "\n");
                writer.write(view.mActive + "\n");
                for (final GridCell cell : view.getGrid().getCells()) {
                    writer.write("CELL:");
                    writer.write(cell.getCellNumber() + ":");
                    writer.write(cell.getRow() + ":");
                    writer.write(cell.getColumn() + ":");
                    writer.write(cell.getCageText() + ":");
                    writer.write(cell.getValue() + ":");
                    writer.write(cell.getUserValue() + ":");
                    for (final int possible : cell.getPossibles())
                        writer.write(possible + ",");
                    writer.write("\n");
                }
                if (view.getGrid().getSelectedCell() != null)
                    writer.write("SELECTED:" + view.getGrid().getSelectedCell().getCellNumber() + "\n");
                final ArrayList<GridCell> invalidchoices = view.getGrid().invalidsHighlighted();
                if (invalidchoices.size() > 0) {
                    writer.write("INVALID:");
                    for (final GridCell cell : invalidchoices)
                        writer.write(cell.getCellNumber() + ",");
                    writer.write("\n");
                }
                final ArrayList<GridCell> cheatedcells = view.getGrid().cheatedHighlighted();
                if (cheatedcells.size() > 0) {
                    writer.write("CHEATED:");
                    for (final GridCell cell : cheatedcells)
                        writer.write(cell.getCellNumber() + ",");
                    writer.write("\n");
                }
                for (final GridCage cage : view.getGrid().getCages()) {
                    writer.write("CAGE:");
                    writer.write(cage.getId() + ":");
                    writer.write(cage.mAction.name() + ":");
                    writer.write("NOTHING" + ":");
                    writer.write(cage.mResult + ":");
                    writer.write(cage.mType + ":");
                    writer.write(cage.getCellNumbers());
                    //writer.write(":" + cage.isOperatorHidden());
                    writer.write("\n");
                }
            } catch (final IOException e) {
                Log.d("HoloKen", "Error saving game: " + e.getMessage());
                return;
            }
        } // End of synchronised block
        Log.d("MathDoku", "Saved game.");
    }

    public long ReadDate() {
        try (InputStream ins = new FileInputStream((this.filename)); BufferedReader br = new BufferedReader(new InputStreamReader(ins), 8192)) {
            return Long.parseLong(br.readLine());
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    
    public void Restore(final GridUI view) {
        String line = null;
        BufferedReader br = null;
        InputStream ins = null;
        String[] cellParts;
        String[] cageParts;
        try {
            ins = new FileInputStream((this.filename));
            br = new BufferedReader(new InputStreamReader(ins), 8192);
            view.mDate = Long.parseLong(br.readLine());

            final int gridSize = Integer.parseInt(br.readLine());

            view.mPlayTime = Long.parseLong(br.readLine());
            view.mActive = br.readLine().equals("true");
            view.resetCells();

            final Grid grid = new Grid(gridSize);
            view.setGrid(grid);

            while ((line = br.readLine()) != null) {
                if (!line.startsWith("CELL:")) break;
                cellParts = line.split(":");

                final int cellNum = Integer.parseInt(cellParts[1]);
                final int row = Integer.parseInt(cellParts[2]);
                final int column = Integer.parseInt(cellParts[3]);

                final GridCell cell = new GridCell(cellNum, row, column);
                final GridCellUI cellUI = new GridCellUI(grid, cell);

                cell.setCagetext(cellParts[4]);
                cell.setValue(Integer.parseInt(cellParts[5]));
                cell.setUserValue(Integer.parseInt(cellParts[6]));
                if (cellParts.length == 8)
                    for (final String possible : cellParts[7].split(","))
                        cell.addPossible(Integer.parseInt(possible));
                view.addCell(cellUI);
                grid.addCell(cell);
            }
            if (line.startsWith("SELECTED:")) {
                final int selected = Integer.parseInt(line.split(":")[1]);
                view.getGrid().setSelectedCell(view.getGrid().getCell(selected));
                view.getGrid().getCell(selected).setSelected(true);
                line = br.readLine();
            }
            if (line.startsWith("INVALID:")) {
                final String invalidlist = line.split(":")[1];
                for (final String cellId : invalidlist.split(",")) {
                    final int cellNum = Integer.parseInt(cellId);
                    final GridCell c = view.getGrid().getCell(cellNum);
                    c.setSelected(true);
                }
                line = br.readLine();
            }
            if (line.startsWith("CHEATED")) {
                final String cheatedlist = line.split(":")[1];
                for (final String cellId : cheatedlist.split(",")) {
                    final int cellNum = Integer.parseInt(cellId);
                    final GridCell c = view.getGrid().getCell(cellNum);
                    c.setCheated(true);
                }
                line = br.readLine();
            }
            do {
                cageParts = line.split(":");
                final GridCage cage;
                cage = new GridCage(view.getGrid(), Integer.parseInt(cageParts[5]));
                cage.setCageId(Integer.parseInt(cageParts[1]));
                cage.mAction = GridCageAction.valueOf(cageParts[2]);
                cage.mResult = Integer.parseInt(cageParts[4]);
                for (final String cellId : cageParts[6].split(",")) {
                    final int cellNum = Integer.parseInt(cellId);
                    final GridCell c = view.getGrid().getCell(cellNum);
                    c.setCage(cage);
                    cage.addCell(c);
                }
                view.getGrid().getCages().add(cage);
            } while ((line = br.readLine()) != null);
            
        } catch (final FileNotFoundException e) {
            Log.d("Mathdoku", "FNF Error restoring game: " + e.getMessage());
        } catch (final IOException e) {
          Log.d("Mathdoku", "IO Error restoring game: " + e.getMessage());
        }
        finally {
          try {
              if (ins != null) {
                  ins.close();
              }

              if (br != null) {
                  br.close();
              }
            if (this.filename.getCanonicalPath().equals(getAutosave()))
                filename.delete();
          } catch (final Exception ignored) {
          }
        }
    }

    public File getAutosave() {
        return new File(context.getFilesDir(), SaveGameListActivity.SAVEGAME_AUTO_NAME);
    }
}