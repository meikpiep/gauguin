package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesCache
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid

class NumberOfCagesWithPossibleForcesPossibleInCage : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: PossiblesCache,
    ): Boolean {
        grid.variant.possibleDigits.forEach { possible ->
            val numberOfPossiblesLeft = grid.variant.gridSize.smallestSide() - grid.cells.count { it.userValue == possible }

            val cagesWithPossible =
                grid.cages
                    .filter { it.cells.any { !it.isUserValueSet } }
                    .filter { it.cells.any { it.possibles.contains(possible) } }

            val cagesWithStaticNumberOfPossible =
                cagesWithPossible.filter {
                    val firstAmountOfPossible =
                        cache
                            .possibles(it)
                            .first()
                            .filterIndexed { index, _ -> !it.cells[index].isUserValueSet }
                            .count { it == possible }

                    cache.possibles(it).all { it.count { it == possible } == firstAmountOfPossible }
                }

            val staticNumberOfPossibles =
                cagesWithStaticNumberOfPossible.sumOf {
                    cache
                        .possibles(it)
                        .first()
                        .filterIndexed { index, _ -> !it.cells[index].isUserValueSet }
                        .count { it == possible }
                }

            val cagesWithDynamicNumberOfPossible = cagesWithPossible - cagesWithStaticNumberOfPossible

            if (staticNumberOfPossibles == numberOfPossiblesLeft) {
                /*
                 * All possibles are already contained in the static cages, so we delete
                 * the possible from all other cages.
                 */
                cagesWithDynamicNumberOfPossible.forEach { dynamicCage ->
                    val reduced =
                        PossiblesReducer(dynamicCage).reduceToPossibleCombinations(
                            cache
                                .possibles(dynamicCage)
                                .filter {
                                    it
                                        .filterIndexed { index, value ->
                                            !dynamicCage.cells[index].isUserValueSet && value == possible
                                        }.isEmpty()
                                },
                        )

                    if (reduced) {
                        return true
                    }
                }
            } else if (cagesWithDynamicNumberOfPossible.size == 1 && staticNumberOfPossibles == numberOfPossiblesLeft - 1) {
                val dynamicCage = cagesWithDynamicNumberOfPossible.first()

                val reduced =
                    PossiblesReducer(dynamicCage).reduceToPossibleCombinations(
                        cache
                            .possibles(dynamicCage)
                            .filter {
                                it
                                    .filterIndexed { index, _ -> !dynamicCage.cells[index].isUserValueSet }
                                    .count { it == possible } == 1
                            },
                    )

                if (reduced) {
                    return true
                }
            }
        }

        return false
    }
}
