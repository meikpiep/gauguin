package com.holokenmod.creation;

import com.holokenmod.Grid;

import java.util.ArrayList;

public class MultiplicationZeroCreator {
    private final int n_cells;
    private final Grid grid;
    private final GridCageCreator cageCreator;

    private final int[] numbers;
    private final ArrayList<int[]> result_set = new ArrayList<>();

    public MultiplicationZeroCreator(final GridCageCreator cageCreator, final Grid grid, final int n_cells) {
        this.cageCreator = cageCreator;
        this.grid = grid;
        this.n_cells = n_cells;
        this.numbers = new int[n_cells];
    }

    public ArrayList<int[]> create() {
        getmultcombos(false, n_cells);

        return result_set;
    }

    private void getmultcombos(final boolean zeroPresent, final int n_cells) {
        //Log.d("ZeroCreator", zeroPresent + " - " + n_cells);

        if (n_cells == 1) {
            if (!zeroPresent) {
                numbers[0] = 0;
                if (cageCreator.satisfiesConstraints(numbers)) {
                    result_set.add(numbers.clone());
                }

                return;
            }
        }

        for (final int n : grid.getPossibleDigits()) {
            if (n_cells == 1) {
                numbers[0] = 0;
                if (cageCreator.satisfiesConstraints(numbers))
                    result_set.add(numbers.clone());
            } else {
                numbers[n_cells-1] = n;
                if (n == 0) {
                    getmultcombos(true, n_cells - 1);
                } else {
                    getmultcombos(zeroPresent, n_cells - 1);
                }
            }
        }
    }
}