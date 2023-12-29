package org.piepmeyer.gauguin.creation.dlx

class ConstraintsFromGridCagesCalculator(
    private val dlxGrid: DLXGrid,
    private val numberOfCages: Int,
) {
    fun calculateConstraints(): List<BooleanArray> {
        val contraints = mutableListOf<BooleanArray>()

        for (creator in dlxGrid.creators) {
            for (possibleCageCombination in creator.possibleNums) {
                val constraint =
                    BooleanArray(
                        dlxGrid.possibleDigits.size * (dlxGrid.gridSize.width + dlxGrid.gridSize.height) +
                            numberOfCages,
                    )

                for (i in possibleCageCombination.indices) {
                    val indexOfDigit = dlxGrid.digitSetting.indexOf(possibleCageCombination[i])

                    val (columnConstraint, rowConstraint) =
                        dlxGrid.columnAndRowConstraints(
                            indexOfDigit,
                            creator,
                            i,
                        )

                    constraint[columnConstraint] = true
                    constraint[rowConstraint] = true
                }

                val cageConstraint = dlxGrid.cageConstraint(creator.id)

                constraint[cageConstraint] = true

                contraints += constraint
            }
        }

        return contraints
    }

    fun numberOfNodes(): Int {
        return dlxGrid.creators
            .map { it.possibleNums.size * (2 * it.numberOfCells + 1) }
            .reduce { acc, i -> acc + i }
    }
}
