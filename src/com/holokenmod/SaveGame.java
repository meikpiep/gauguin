package com.holokenmod;

import android.content.Context;
import android.util.Log;

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
    public File filename;

    public SaveGame(Context context) {
        this.context=context;
        this.filename = getAutosave();
    }
    public SaveGame(String filename) {
        this.filename = new File(filename);

    }
    public SaveGame(String filename) {
        this.filename = filename;

    }
    
    public boolean Save(GridView view) {
        synchronized (view.mLock) {    // Avoid saving game at the same time as creating puzzle
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(this.filename));
                long now = System.currentTimeMillis();
                writer.write(now + "\n");
                writer.write(view.mGridSize + "\n");
                writer.write(view.mPlayTime +"\n");
                writer.write(view.mActive + "\n");
                for (GridCell cell : view.mCells) {
                    writer.write("CELL:");
                    writer.write(cell.mCellNumber + ":");
                    writer.write(cell.mRow + ":");
                    writer.write(cell.mColumn + ":");
                    writer.write(cell.mCageText + ":");
                    writer.write(cell.mValue + ":");
                    writer.write(cell.getUserValue() + ":");
                    for (int possible : cell.mPossibles)
                        writer.write(possible + ",");
                    writer.write("\n");
                }
                if (view.mSelectedCell != null)
                    writer.write("SELECTED:" + view.mSelectedCell.mCellNumber + "\n");
                ArrayList<GridCell> invalidchoices = view.invalidsHighlighted();
                if (invalidchoices.size() > 0) {
                    writer.write("INVALID:");
                    for (GridCell cell : invalidchoices)
                        writer.write(cell.mCellNumber + ",");
                    writer.write("\n");
                }
                ArrayList<GridCell> cheatedcells = view.cheatedHighlighted();
                if (cheatedcells.size() > 0) {
                    writer.write("CHEATED:");
                    for (GridCell cell : cheatedcells)
                        writer.write(cell.mCellNumber + ",");
                    writer.write("\n");
                }
                for (GridCage cage : view.mCages) {
                    writer.write("CAGE:");
                    writer.write(cage.mId + ":");
                    writer.write(cage.mAction + ":");
                    writer.write(cage.mActionStr + ":");
                    writer.write(cage.mResult + ":");
                    writer.write(cage.mType + ":");
                    for (GridCell cell : cage.mCells)
                        writer.write(cell.mCellNumber + ",");
                    //writer.write(":" + cage.isOperatorHidden());
                    writer.write("\n");
                }
            }
            catch (IOException e) {
                Log.d("HoloKen", "Error saving game: "+e.getMessage());
                return false;
            }
            finally {
                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    //pass
                    return false;
                }
            }
        } // End of synchronised block
        Log.d("MathDoku", "Saved game.");
        return true;
    }
    
    
    public long ReadDate() {
        BufferedReader br = null;
        InputStream ins = null;
        try {
            ins = new FileInputStream((this.filename));
            br = new BufferedReader(new InputStreamReader(ins), 8192);
            return Long.parseLong(br.readLine());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
              try {
                ins.close();
                br.close();
              } catch (Exception e) {
                // Nothing.
                  return 0;
              }
            }
        return 0;
    }
    
    public boolean Restore(GridView view) {
        String line = null;
        BufferedReader br = null;
        InputStream ins = null;
        String[] cellParts;
        String[] cageParts;
        try {
            ins = new FileInputStream((this.filename));
            br = new BufferedReader(new InputStreamReader(ins), 8192);
            view.mDate = Long.parseLong(br.readLine());
            view.mGridSize = Integer.parseInt(br.readLine());
            view.mPlayTime = Long.parseLong(br.readLine());
            if (br.readLine().equals("true"))
                view.mActive = true;
            else
                view.mActive = false;
            view.mCells = new ArrayList<GridCell>();
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("CELL:")) break;
                cellParts = line.split(":");
                int cellNum = Integer.parseInt(cellParts[1]);
                GridCell cell = new GridCell(view, cellNum);
                cell.mRow = Integer.parseInt(cellParts[2]);
                cell.mColumn = Integer.parseInt(cellParts[3]);
                cell.mCageText = cellParts[4];
                cell.mValue = Integer.parseInt(cellParts[5]);
                cell.setUserValue(Integer.parseInt(cellParts[6]));
                if (cellParts.length == 8)
                    for (String possible : cellParts[7].split(","))
                        cell.mPossibles.add(Integer.parseInt(possible));
                view.mCells.add(cell);
            }
            view.mSelectedCell = null;
            if (line.startsWith("SELECTED:")) {
                int selected = Integer.parseInt(line.split(":")[1]);
                view.mSelectedCell = view.mCells.get(selected);
                view.mSelectedCell.mSelected = true;
                line = br.readLine();
            }
            if (line.startsWith("INVALID:")) {
                String invalidlist = line.split(":")[1];
                for (String cellId : invalidlist.split(",")) {
                    int cellNum = Integer.parseInt(cellId);
                    GridCell c = view.mCells.get(cellNum);
                    c.setInvalidHighlight(true);
                }
                line = br.readLine();
            }
            if (line.startsWith("CHEATED")) {
                String cheatedlist = line.split(":")[1];
                for (String cellId : cheatedlist.split(",")) {
                    int cellNum = Integer.parseInt(cellId);
                    GridCell c = view.mCells.get(cellNum);
                    c.setCheatedHighlight(true);
                }
                line = br.readLine();
            }
            view.mCages = new ArrayList<GridCage>();
            do {
                cageParts = line.split(":");
                GridCage cage;
                cage = new GridCage(view, Integer.parseInt(cageParts[5]));
                cage.mId = Integer.parseInt(cageParts[1]);
                cage.mAction = Integer.parseInt(cageParts[2]);
                cage.mActionStr = cageParts[3];
                cage.mResult = Integer.parseInt(cageParts[4]);
                for (String cellId : cageParts[6].split(",")) {
                    int cellNum = Integer.parseInt(cellId);
                    GridCell c = view.mCells.get(cellNum);
                    c.mCageId = cage.mId;
                    cage.mCells.add(c);
                }
                view.mCages.add(cage);
            } while ((line = br.readLine()) != null);
            
        } catch (FileNotFoundException e) {
            Log.d("Mathdoku", "FNF Error restoring game: " + e.getMessage());
            return false;
        } catch (IOException e) {
          Log.d("Mathdoku", "IO Error restoring game: " + e.getMessage());
          return false;
        }
        finally {
          try {
            ins.close();
            br.close();
            if (this.filename.getCanonicalPath().equals(getAutosave()))
                (filename).delete();
          } catch (Exception e) {
            // Nothing.
              return false;
          }
        }
        return true;
    }

    public File getAutosave() {
        return new File(context.getFilesDir(), SaveGameListActivity.SAVEGAME_AUTO_NAME);
    }


}