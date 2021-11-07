package com.holokenmod.creation;

import com.holokenmod.Grid;

import java.util.ArrayList;

public class MultiplicationNonZeroCreator {
    private final int target_sum;
    private final int n_cells;
    private final Grid grid;
    private final GridCageCreator cageCreator;

    private final int[] numbers;
    private final ArrayList<int[]> result_set = new ArrayList<>();

    public MultiplicationNonZeroCreator(final GridCageCreator cageCreator, final Grid grid, final int target_sum, final int n_cells) {
        this.cageCreator = cageCreator;
        this.grid = grid;
        this.target_sum = target_sum;
        this.n_cells = n_cells;
        this.numbers = new int[n_cells];
    }

    public ArrayList<int[]> create() {
        getmultcombos(target_sum, n_cells);

        return result_set;
    }

    private void getmultcombos(final int target_sum, final int n_cells)
    {
        for (final int n : grid.getPossibleNonZeroDigits())
        {
            if (n != 0 && target_sum != 0 && target_sum % n != 0)
                continue;

            if (n_cells == 1)
            {
                if (n == target_sum) {
                    numbers[0] = n;
                    if (cageCreator.satisfiesConstraints(numbers))
                        result_set.add(numbers.clone());
                }
            }
            else {
                numbers[n_cells-1] = n;

                getmultcombos(target_sum / n, n_cells - 1);
            }
        }
    }
}