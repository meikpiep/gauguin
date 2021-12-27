package com.holokenmod;

import android.content.Context;
import android.util.Log;

import com.holokenmod.ui.GridCellUI;
import com.holokenmod.ui.GridUI;
import com.holokenmod.ui.SaveGameListActivity;

import org.apache.commons.io.FileUtils;

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
	private final File filename;
	private Context context;
	
	public SaveGame(final Context context) {
		this.context = context;
		this.filename = getAutosave();
	}
	
	public SaveGame(final File file) {
		this.filename = file;
	}
	
	public void Save(final Grid grid) {
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename))) {
			final long now = System.currentTimeMillis();
			writer.write(now + "\n");
			writer.write(grid.getGridSize() + "\n");
			writer.write(grid.getPlayTime() + "\n");
			writer.write(grid.isActive() + "\n");
			for (final GridCell cell : grid.getCells()) {
				writer.write("CELL:");
				writer.write(cell.getCellNumber() + ":");
				writer.write(cell.getRow() + ":");
				writer.write(cell.getColumn() + ":");
				writer.write(cell.getCageText() + ":");
				writer.write(cell.getValue() + ":");
				writer.write(cell.getUserValue() + ":");
				for (final int possible : cell.getPossibles()) {
					writer.write(possible + ",");
				}
				writer.write("\n");
			}
			if (grid.getSelectedCell() != null) {
				writer.write("SELECTED:" + grid.getSelectedCell()
						.getCellNumber() + "\n");
			}
			final ArrayList<GridCell> invalidchoices = grid.invalidsHighlighted();
			if (invalidchoices.size() > 0) {
				writer.write("INVALID:");
				for (final GridCell cell : invalidchoices) {
					writer.write(cell.getCellNumber() + ",");
				}
				writer.write("\n");
			}
			final ArrayList<GridCell> cheatedcells = grid.cheatedHighlighted();
			if (cheatedcells.size() > 0) {
				writer.write("CHEATED:");
				for (final GridCell cell : cheatedcells) {
					writer.write(cell.getCellNumber() + ",");
				}
				writer.write("\n");
			}
			for (final GridCage cage : grid.getCages()) {
				writer.write("CAGE:");
				writer.write(cage.getId() + ":");
				writer.write(cage.getAction().name() + ":");
				writer.write("NOTHING" + ":");
				writer.write(cage.getResult() + ":");
				writer.write(cage.getType() + ":");
				writer.write(cage.getCellNumbers());
				//writer.write(":" + cage.isOperatorHidden());
				writer.write("\n");
			}
		} catch (final IOException e) {
			Log.d("HoloKen", "Error saving game: " + e.getMessage());
			return;
		}
		Log.d("MathDoku", "Saved game.");
	}
	
	public long ReadDate() {
		try (final InputStream ins = new FileInputStream((this.filename));
			 final BufferedReader br = new BufferedReader(new InputStreamReader(ins), 8192)) {
			return Long.parseLong(br.readLine());
		} catch (final NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean restore(final GridUI view) {
		String line;
		BufferedReader br = null;
		InputStream ins = null;
		String[] cellParts;
		String[] cageParts;
		
		if (this.filename.length() == 0) {
			return false;
		}
		
		try {
			Log.i("test", this.filename.getAbsolutePath() + " - " + this.filename.length());
			Log.i("savefile", FileUtils.readFileToString(this.filename));
			
			ins = new FileInputStream((this.filename));
			br = new BufferedReader(new InputStreamReader(ins), 8192);
			long creationDate = Long.parseLong(br.readLine());
			
			final String gridSizeString = br.readLine();
			GridSize gridSize = GridSize.create(gridSizeString);
			
			long playTime = Long.parseLong(br.readLine());
			view.resetCells();
			
			final Grid grid = new Grid(gridSize, creationDate);
			view.setGrid(grid);
			
			grid.setActive(br.readLine().equals("true"));
			grid.setPlayTime(playTime);
			
			while ((line = br.readLine()) != null) {
                if (!line.startsWith("CELL:")) {
                    break;
                }
				cellParts = line.split(":");
				
				final int cellNum = Integer.parseInt(cellParts[1]);
				final int row = Integer.parseInt(cellParts[2]);
				final int column = Integer.parseInt(cellParts[3]);
				
				final GridCell cell = new GridCell(cellNum, row, column);
				final GridCellUI cellUI = new GridCellUI(grid, cell);
				
				cell.setCagetext(cellParts[4]);
				cell.setValue(Integer.parseInt(cellParts[5]));
				cell.setUserValue(Integer.parseInt(cellParts[6]));
                if (cellParts.length == 8) {
                    for (final String possible : cellParts[7].split(",")) {
                        cell.addPossible(Integer.parseInt(possible));
                    }
                }
				view.addCell(cellUI);
				grid.addCell(cell);
			}
			if (line.startsWith("SELECTED:")) {
				final int selected = Integer.parseInt(line.split(":")[1]);
				grid.setSelectedCell(grid.getCell(selected));
				grid.getCell(selected).setSelected(true);
				line = br.readLine();
			}
			if (line.startsWith("INVALID:")) {
				final String invalidlist = line.split(":")[1];
				for (final String cellId : invalidlist.split(",")) {
					final int cellNum = Integer.parseInt(cellId);
					final GridCell c = grid.getCell(cellNum);
					c.setSelected(true);
				}
				line = br.readLine();
			}
			if (line.startsWith("CHEATED")) {
				final String cheatedlist = line.split(":")[1];
				for (final String cellId : cheatedlist.split(",")) {
					final int cellNum = Integer.parseInt(cellId);
					final GridCell c = grid.getCell(cellNum);
					c.setCheated(true);
				}
				line = br.readLine();
			}
			do {
				cageParts = line.split(":");
				final GridCage cage;
				cage = new GridCage(grid, Integer.parseInt(cageParts[5]));
				cage.setCageId(Integer.parseInt(cageParts[1]));
				cage.setAction(GridCageAction.valueOf(cageParts[2]));
				cage.setResult(Integer.parseInt(cageParts[4]));
				for (final String cellId : cageParts[6].split(",")) {
					final int cellNum = Integer.parseInt(cellId);
					final GridCell c = grid.getCell(cellNum);
					c.setCage(cage);
					cage.addCell(c);
				}
				grid.getCages().add(cage);
			} while ((line = br.readLine()) != null);
			
			return true;
		} catch (final FileNotFoundException e) {
			Log.d("Mathdoku", "FNF Error restoring game: " + e.getMessage());
			
			return false;
		} catch (final IOException e) {
			Log.d("Mathdoku", "IO Error restoring game: " + e.getMessage());
			
			return false;
		} catch (final Exception e) {
			Log.e(e.getMessage(), e.getMessage(), e);
			
			return false;
		} finally {
			try {
				if (ins != null) {
					ins.close();
				}
				
				if (br != null) {
					br.close();
				}
                if (this.filename.getCanonicalPath().equals(getAutosave())) {
                    filename.delete();
                }
			} catch (final Exception ignored) {
			}
		}
	}
	
	public File getAutosave() {
		return new File(context.getFilesDir(), SaveGameListActivity.SAVEGAME_AUTO_NAME);
	}
}