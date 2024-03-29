package org.piepmeyer.gauguin.creation.dlx

class ConstraintsFromRectangularGridsCalculator(
    private val dlxGrid: DLXGrid,
    private val numberOfCages: Int,
) {
    private val cartesianProductOfRectangularPossibles =
        if (dlxGrid.gridSize.isSquare) {
            emptyList()
        } else {
            uniqueIndexSetsOfGivenLength(
                dlxGrid.possibleDigits.indices.toList(),
                dlxGrid.gridSize.largestSide() - dlxGrid.gridSize.smallestSide(),
            )
        }

    fun calculateConstraints(): List<BooleanArray> {
        if (dlxGrid.gridSize.isSquare) {
            return emptyList()
        }

        var cageId = dlxGrid.creators.size

        val contraints = mutableListOf<BooleanArray>()

        for (rowOrColumn in 0 until dlxGrid.gridSize.largestSide()) {
            for (indexesOfDigits in cartesianProductOfRectangularPossibles) {
                val constraint =
                    BooleanArray(
                        dlxGrid.possibleDigits.size * (dlxGrid.gridSize.width + dlxGrid.gridSize.height) +
                            numberOfCages,
                    )

                for (indexOfDigit in indexesOfDigits) {
                    val (columnConstraint, rowConstraint) =
                        dlxGrid.columnAndRowConstraints(
                            indexOfDigit,
                            rowOrColumn,
                            rowOrColumn,
                        )

                    if (dlxGrid.gridSize.width < dlxGrid.gridSize.height) {
                        constraint[rowConstraint] = true
                    } else {
                        constraint[columnConstraint] = true
                    }

                    val cageConstraint = dlxGrid.cageConstraint(cageId)
                    constraint[cageConstraint] = true
                }

                contraints += constraint
            }
            cageId++
        }

        return contraints
    }

    private fun uniqueIndexSetsOfGivenLength(
        values: List<Int>,
        numberOfCopies: Int,
    ): Set<Set<Int>> {
        return UniqueIndexSetsOfGivenLength(values, numberOfCopies).calculateProduct()
    }

    fun numberOfNodes(): Int {
        return if (dlxGrid.gridSize.isSquare) {
            0
        } else {
            dlxGrid.gridSize.largestSide() * cartesianProductOfRectangularPossibles.size * 200
        }
    }
}
