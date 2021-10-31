package com.holokenmod;

import java.util.ArrayList;

public class GridCreator {

    private final Grid grid;

    public GridCreator(Grid grid) {
        this.grid = grid;
    }

    private int CreateSingleCages(GridCageOperation operationSet) {
        int singles = grid.getGridSize() / 2;
        boolean RowUsed[] = new boolean[grid.getGridSize()];
        boolean ColUsed[] = new boolean[grid.getGridSize()];
        boolean ValUsed[] = new boolean[grid.getGridSize()];
        for (int i = 0; i < singles; i++) {
            GridCell cell;
            while (true) {
                cell = grid.getCell(RandomSingleton.getInstance().nextInt(grid.getGridSize() * grid.getGridSize()));
                if (!RowUsed[cell.getRow()] && !ColUsed[cell.getRow()] && !ValUsed[cell.getValue() - 1])
                    break;
            }
            ColUsed[cell.getColumn()] = true;
            RowUsed[cell.getRow()] = true;
            ValUsed[cell.getValue() - 1] = true;
            GridCage cage = new GridCage(grid, GridCage.CAGE_1);
            cage.addCell(cell);
            cage.setArithmetic(operationSet);
            cage.setCageId(i);
            grid.addCage(cage);
        }
        return singles;
    }

    /* Take a filled grid and randomly create cages */
    public void CreateCages() {

        boolean restart;

        do {
            restart = false;
            GridCageOperation operationSet = ApplicationPreferences.getInstance().getOperations();

            int cageId = CreateSingleCages(operationSet);
            for (int cellNum = 0; cellNum < grid.getCells().size(); cellNum++) {
                GridCell cell = grid.getCell(cellNum);
                if (cell.CellInAnyCage())
                    continue; // Cell already in a cage, skip

                ArrayList<Integer> possible_cages = GridCage.getvalidCages(grid, cell);
                if (possible_cages.size() == 1) {    // Only possible cage is a single
                    grid.ClearAllCages();
                    restart = true;
                    break;
                }

                // Choose a random cage type from one of the possible (not single cage)
                int cage_type = possible_cages.get(RandomSingleton.getInstance().nextInt(possible_cages.size() - 1) + 1);
                GridCage cage = new GridCage(grid, cage_type);
                int[][] cage_coords = GridCage.CAGE_COORDS[cage_type];
                for (int[] cage_coord : cage_coords) {
                    int col = cell.getColumn() + cage_coord[0];
                    int row = cell.getRow() + cage_coord[1];
                    cage.addCell(grid.getCellAt(row, col));
                }

                cage.setArithmetic(operationSet);  // Make the maths puzzle
                cage.setCageId(cageId++);  // Set cage's id
                grid.getCages().add(cage);  // Add to the cage list
            }
        } while (restart);

        for (GridCage cage : grid.getCages())
            cage.setBorders();
        grid.setCageTexts();
    }

    /*
     * Fills the grid with random numbers, per the rules:
     *
     * - 1 to <rowsize> on every row and column
     * - No duplicates in any row or column.
     */
    public void randomiseGrid() {
        int attempts;
        for (int value = 1 ; value < grid.getGridSize()+1 ; value++) {
            for (int row = 0 ; row < grid.getGridSize() ; row++) {
                attempts = 20;
                GridCell cell;
                int column;
                while (true) {
                    column = RandomSingleton.getInstance().nextInt(grid.getGridSize());
                    cell = grid.getCellAt(row, column);
                    if (--attempts == 0)
                        break;
                    if (cell.getValue() != 0)
                        continue;
                    if (grid.valueInColumn(column, value))
                        continue;
                    break;
                }
                if (attempts == 0) {
                    grid.clearValue(value--);
                    break;
                }
                cell.setValue(value);
                //Log.d("KenKen", "New cell: " + cell);
            }
        }
    }

}