package com.holokenmod;

import android.util.Log;

import com.holokenmod.ui.DigitSetting;
import com.srlee.DLX.DLX;
import com.srlee.DLX.MathDokuDLX;

import java.util.ArrayList;

public class GridCreator {

    // O = Origin (0,0) - must be the upper leftmost cell
    // X = Other cells used in cage
    private static final int [][][] CAGE_COORDS = new int[][][] {
            // O
            {{0,0}},
            // O
            // X
            {{0,0},{0,1}},
            // OX
            {{0,0},{1,0}},
            // O
            // X
            // X
            {{0,0},{0,1},{0,2}},
            // OXX
            {{0,0},{1,0},{2,0}},
            // O
            // XX
            {{0,0},{0,1},{1,1}},
            // O
            //XX
            {{0,0},{0,1},{-1,1}},
            // OX
            //  X
            {{0,0},{1,0},{1,1}},
            // OX
            // X
            {{0,0},{1,0},{0,1}},
            // OX
            // XX
            {{0,0},{1,0},{0,1},{1,1}},
            // OX
            // X
            // X
            {{0,0},{1,0},{0,1},{0,2}},
            // OX
            //  X
            //  X
            //{{0,0},{1,0},{1,1},{1,2}},
            // O
            // X
            // XX
            //{{0,0},{0,1},{0,2},{1,2}},
            // O
            // X
            //XX
            {{0,0},{0,1},{0,2},{-1,2}},
            // OXX
            // X
            {{0,0},{1,0},{2,0},{0,1}},
            // OXX
            //   X
            {{0,0},{1,0},{2,0},{2,1}},
            // O
            // XXX
            /*{{0,0},{0,1},{1,1},{2,1}},
            //  O
            //XXX
            {{0,0},{-2,1},{-1,1},{0,1}},
            // O
            // XX
            // X
            {{0,0},{0,1},{0,2},{1,1}},
            // O
            //XX
            // X
            {{0,0},{0,1},{0,2},{-1,1}},
            // OXX
            //  X
            {{0,0},{1,0},{2,0},{1,1}},
            // O
            //XXX
            {{0,0},{-1,1},{0,1},{1,1}},
            // OXXX
            {{0,0},{1,0},{2,0},{3,0}},
            // O
            // X
            // X
            // X
            {{0,0},{0,1},{0,2},{0,3}},
            // O
            // XX
            //  X
            {{0,0},{0,1},{1,1},{1,2}},
            // O
            //XX
            //X
            {{0,0},{0,1},{-1,1},{-1,2}},
            // OX
            //  XX
            {{0,0},{1,0},{1,1},{2,1}},
            // OX
            //XX
            {{0,0},{1,0},{0,1},{-1,1}}*/
    };

    private final int gridSize;
    private Grid grid;

    public GridCreator(int gridSize) {
        this.gridSize = gridSize;
    }

    private int CreateSingleCages(GridCageOperation operationSet) {
        int singles = grid.getGridSize() / 2;

        boolean[] RowUsed = new boolean[grid.getGridSize()];
        boolean[] ColUsed = new boolean[grid.getGridSize()];
        boolean[] ValUsed = new boolean[grid.getGridSize()];

        for (int i = 0; i < singles; i++) {
            GridCell cell;
            int cellIndex;
            do {
                cell = grid.getCell(RandomSingleton.getInstance().nextInt(grid.getGridSize() * grid.getGridSize()));

                cellIndex = cell.getValue();

                if (ApplicationPreferences.getInstance().getDigitSetting() == DigitSetting.FIRST_DIGIT_ONE) {
                    cellIndex--;
                }

            } while (RowUsed[cell.getRow()] || ColUsed[cell.getRow()] || ValUsed[cellIndex]);
            ColUsed[cell.getColumn()] = true;
            RowUsed[cell.getRow()] = true;
            ValUsed[cellIndex] = true;
            GridCage cage = new GridCage(grid, GridCage.CAGE_1);
            cage.addCell(cell);
            cage.setArithmetic(operationSet);
            cage.setCageId(i);
            grid.addCage(cage);
        }
        return singles;
    }

    /* Take a filled grid and randomly create cages */
    private void CreateCages() {

        GridCageOperation operationSet = ApplicationPreferences.getInstance().getOperations();
        boolean restart;

        do {
            restart = false;

            int cageId = 0;

            if (ApplicationPreferences.getInstance().getSingleCageUsage() == SingleCageUsage.FIXED_NUMBER) {
                cageId = CreateSingleCages(operationSet);
            }

            for (GridCell cell : grid.getCells()) {
                if (cell.CellInAnyCage())
                    continue; // Cell already in a cage, skip

                ArrayList<Integer> possible_cages = getValidCages(grid, cell);

                int cage_type;

                if (possible_cages.size() == 1) {
                    // Only possible cage is a single
                    if (ApplicationPreferences.getInstance().getSingleCageUsage() != SingleCageUsage.DYNAMIC) {
                        grid.ClearAllCages();
                        restart = true;
                        break;
                    } else {
                        cage_type = 0;
                    }
                } else {
                    cage_type = possible_cages.get(RandomSingleton.getInstance().nextInt(possible_cages.size() - 1) + 1);
                }

                GridCage cage = new GridCage(grid, cage_type);
                int[][] cage_coords = CAGE_COORDS[cage_type];
                for (int[] cage_coord : cage_coords) {
                    int col = cell.getColumn() + cage_coord[0];
                    int row = cell.getRow() + cage_coord[1];
                    cage.addCell(grid.getCellAt(row, col));
                }

                cage.setArithmetic(operationSet);
                cage.setCageId(cageId++);
                grid.addCage(cage);
            }
        } while (restart);

        for (GridCage cage : grid.getCages())
            cage.setBorders();
        grid.setCageTexts();
    }

    private ArrayList<Integer> getValidCages(Grid grid, GridCell origin) {
        ArrayList<Integer> valid = new ArrayList<>();

        for (int cage_num=0; cage_num < CAGE_COORDS.length; cage_num++) {
            int [][]cage_coords = CAGE_COORDS[cage_num];

            boolean validCage = true;

            for (int[] cage_coord : cage_coords) {
                int col = origin.getColumn() + cage_coord[0];
                int row = origin.getRow() + cage_coord[1];
                GridCell c = grid.getCellAt(row, col);
                if (c == null || c.CellInAnyCage()) {
                    validCage = false;
                    break;
                }
            }

            if (validCage) {
                valid.add(cage_num);
            }
        }

        return valid;
    }

    /*
     * Fills the grid with random numbers, per the rules:
     *
     * - 1 to <rowsize> on every row and column
     * - No duplicates in any row or column.
     */
    private void randomiseGrid() {
        int attempts;

        for(int digit : ApplicationPreferences.getInstance().getDigitSetting().getPossibleDigits(gridSize)) {
            for (int row = 0 ; row < grid.getGridSize() ; row++) {
                attempts = 20;
                GridCell cell;
                int column;
                while (true) {
                    column = RandomSingleton.getInstance().nextInt(grid.getGridSize());
                    cell = grid.getCellAt(row, column);

                    if (--attempts == 0)
                        break;
                    if (cell.getValue() > -1)
                        continue;
                    if (grid.valueInColumn(column, digit))
                        continue;
                    break;
                }
                if (attempts == 0) {
                    grid.clearValue(digit);
                    break;
                }
                cell.setValue(digit);
            }
        }

        for(GridCell cell : grid.getCells()) {
            Log.d("KenKen", "New cell: " + cell);
        }
    }

    public Grid create() {
        int num_solns;
        int num_attempts = 0;
        RandomSingleton.getInstance().discard();
        do {
            grid = new Grid(gridSize);

            int cellnum = 0;

            for (int row = 0; row < grid.getGridSize(); row++) {
                for (int column = 0; column < grid.getGridSize(); column++) {
                    grid.addCell(new GridCell(cellnum++, row, column));
                }
            }

            randomiseGrid();
            CreateCages();

            num_attempts++;
            MathDokuDLX mdd = new MathDokuDLX(grid.getGridSize(), grid.getCages());
            // Stop solving as soon as we find multiple solutions
            num_solns = mdd.Solve(DLX.SolveType.MULTIPLE);
            Log.d("MathDoku", "Num Solns = " + num_solns);
        } while (num_solns > 1);
        Log.d ("MathDoku", "Num Attempts = " + num_attempts);

        return grid;
    }
}