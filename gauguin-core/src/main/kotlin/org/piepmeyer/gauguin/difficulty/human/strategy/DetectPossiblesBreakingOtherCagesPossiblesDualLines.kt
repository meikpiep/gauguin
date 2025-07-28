package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

class DetectPossiblesBreakingOtherCagesPossiblesDualLines : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
        val lines = cache.adjacentlines(2)

        lines.forEach { dualLines ->

            val cellsOfLines = dualLines.cells()

            val cagesContainedInBothLines =
                dualLines
                    .cages()
                    .filter { it.cells.all { it.isUserValueSet || cellsOfLines.contains(it) } }
                    .filter { it.cells.any { !it.isUserValueSet } }
                    .toSet()

            cagesContainedInBothLines.forEach { cage ->
                val cageCombinatinos = cache.possibles(cage)

                cageCombinatinos.forEach { cageCombination ->
                    val doublePossibles = calculateDualPossibles(cageCombination, cageCombinatinos, cage)

                    if (doublePossibles.isNotEmpty() &&
                        reduceIfPossible(doublePossibles, cageCombinatinos, cage, cagesContainedInBothLines, cache)
                    ) {
                        return HumanSolverStrategy.successCellsChanged(cage.cells)
                    }
                }
            }
        }

        return HumanSolverStrategy.nothingChanged()
    }

    private fun reduceIfPossible(
        doublePossibles: List<Int>,
        combinations: Set<IntArray>,
        cage: GridCage,
        cagesContainedInBothLines: Set<GridCage>,
        cache: HumanSolverCache,
    ): Boolean {
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

        return false
    }

    private fun calculateDualPossibles(
        combination: IntArray,
        combinations: Set<IntArray>,
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
