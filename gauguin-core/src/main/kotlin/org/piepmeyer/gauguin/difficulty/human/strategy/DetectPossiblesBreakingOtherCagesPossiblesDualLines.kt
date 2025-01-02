package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class DetectPossiblesBreakingOtherCagesPossiblesDualLines : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        val lines = cache.adjacentlines(2)

        lines.forEach { dualLines ->

            val cellsOfLines = dualLines.map { it.cells() }.flatten()

            val cagesContainedInBothLines =
                dualLines
                    .asSequence()
                    .map { it.cages() }
                    .flatten()
                    .filter { it.cells.all { it.isUserValueSet || cellsOfLines.contains(it) } }
                    .filter { it.cells.any { !it.isUserValueSet } }
                    .toSet()

            cagesContainedInBothLines.forEach { cage ->
                val combinations = cache.possibles(cage)

                combinations.forEach { combination ->
                    val doublePossibles = calculateDualPossibles(combination, combinations, cage)

                    doublePossibles.forEach { doublePossible ->
                        val otherCages = cagesContainedInBothLines - cage

                        otherCages
                            .filter { it.cells.none { it.userValue == doublePossible } }
                            .forEach { otherCage ->
                                val eachPossibleEnforcesDoublePossible =
                                    cache
                                        .possibles(otherCage)
                                        .all { it.contains(doublePossible) }

                                if (eachPossibleEnforcesDoublePossible) {
                                    val reducing =
                                        PossiblesReducer(cage).reduceToPossibleCombinations(
                                            combinations.filterNot { it.count { it == doublePossible } == 2 },
                                        )

                                    if (reducing) {
                                        return true
                                    }
                                }
                            }
                    }
                }
            }
        }

        return false
    }

    private fun calculateDualPossibles(
        combination: IntArray,
        combinations: List<IntArray>,
        cage: GridCage,
    ) = combination
        .groupBy { it }
        .filter { groupedSize ->
            groupedSize.value.size == 2 && combinations.none { it.count { it == groupedSize.key } == 1 }
        }.map { it.key }
        .filter { doublePossible ->
            cage.cells.none { it.userValue == doublePossible }
        }
}
