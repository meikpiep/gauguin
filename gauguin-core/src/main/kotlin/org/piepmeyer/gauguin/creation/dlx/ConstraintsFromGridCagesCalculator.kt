package org.piepmeyer.gauguin.creation.dlx

import java.util.SortedSet

class ConstraintsFromGridCagesCalculator(
    private val dlxGrid: DLXGrid,
    private val numberOfCages: Int,
) {
    fun calculateConstraints(): Pair<List<BooleanArray>, SortedSet<Int>> {
        val contraints = mutableListOf<BooleanArray>()

        val knownSolution = mutableSetOf<Int>()

        for (creator in dlxGrid.creators) {
            for (possibleCageCombination in creator.possibleNums) {
                val constraint =
                    BooleanArray(
                        dlxGrid.possibleDigits.size * (dlxGrid.gridSize.width + dlxGrid.gridSize.height) +
                            numberOfCages,
                    )

                if (possibleCageCombination.indices.all { creator.cage.cells[it].value == possibleCageCombination[it] }) {
                    knownSolution += contraints.size
                }

                for (index in possibleCageCombination.indices) {
                    val indexOfDigit = dlxGrid.digitSetting.indexOf(possibleCageCombination[index])

                    val (columnConstraint, rowConstraint) =
                        dlxGrid.columnAndRowConstraints(
                            indexOfDigit,
                            creator,
                            index,
                        )

                    constraint[columnConstraint] = true
                    constraint[rowConstraint] = true
                }

                val cageConstraint = dlxGrid.cageConstraint(creator.id)

                constraint[cageConstraint] = true

                contraints += constraint
            }
        }

        return Pair(contraints, knownSolution.toSortedSet())
    }

    fun numberOfNodes(): Int {
        return dlxGrid.creators
            .map { it.possibleNums.size * (2 * it.numberOfCells + 1) }
            .reduce { acc, i -> acc + i }
    }
}
