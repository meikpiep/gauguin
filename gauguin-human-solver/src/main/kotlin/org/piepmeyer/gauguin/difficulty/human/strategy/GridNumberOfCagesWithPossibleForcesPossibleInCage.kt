package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid

/**
 * Scans the whole grid for each possible value analysing if the number of possibles contained in
 * the cages:
 *
 *  - Calculates the occurrences of the possible which must be fulfilled via undecided cells.
 *  - Calculates the set of cages with a static count of possibles in each combination.
 *  - Calculates the set of cages with a dynamic count of possibles.
 *  - If the static set already fulfills the needed amount of possible, delete all possible
 *    combinations of the dynamic cages which contain this possible.
 *  - If the missing count of occurrences 1 and there is exactly one dynamic cage, delete all
 *    possible combinations from this cage that do not contain the needed possible.
 */
class GridNumberOfCagesWithPossibleForcesPossibleInCage : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        grid.variant.possibleDigits.forEach { possible ->
            val numberOfPossiblesLeft =
                grid.variant.gridSize.smallestSide() - grid.cells.count { it.userValue == possible }

            val cagesWithPossible =
                grid.cages
                    .filter { it.cells.any { !it.isUserValueSet } }
                    .filter { it.cells.any { it.possibles.contains(possible) } }

            if (cagesWithPossible.isNotEmpty()) {
                val cagesWithStaticNumberOfPossible =
                    cagesWithPossible.filter { cage ->
                        val staticPossibleCount =
                            cache
                                .possibles(cage)
                                .first()
                                .filterIndexed { index, _ -> !cage.cells[index].isUserValueSet }
                                .count { it == possible }

                        cache
                            .possibles(cage)
                            .map {
                                it.filterIndexed { index, _ -> !cage.cells[index].isUserValueSet }
                            }.all { possibleCombination ->
                                possibleCombination.count { it == possible } == staticPossibleCount
                            }
                    }

                val staticNumberOfPossibles =
                    cagesWithStaticNumberOfPossible.sumOf { cage ->
                        cache
                            .possibles(cage)
                            .first()
                            .filterIndexed { index, _ -> !cage.cells[index].isUserValueSet }
                            .count { it == possible }
                    }

                val cagesWithDynamicNumberOfPossible =
                    cagesWithPossible - cagesWithStaticNumberOfPossible.toSet()

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
                            return HumanSolverStrategyResult.Success(dynamicCage.cells)
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
                        return HumanSolverStrategyResult.Success(dynamicCage.cells)
                    }
                }
            }
        }

        return HumanSolverStrategyResult.NothingChanged()
    }
}
