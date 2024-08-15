package org.piepmeyer.gauguin.difficulty.human.strategy

/**
 * Two adjacent lines to find two cages whose every combination of possibles contain one static
 * possible value. If found, this possible gets deleted from every cell of both lines not covered
 * by the two cages.
 */
/*class DualAdjacentLinesXWing : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val linePairs = GridLines(grid).adjacentlinesWithEachPossibleValue(2)

        linePairs.forEach { linePair ->
            val (singleCageNotCoveredByLines, staticGridSum) = calculateSingleCageCoveredByLines(grid, linePair)

            singleCageNotCoveredByLines?.let { cage ->
                val neededSumOfLines = grid.variant.possibleDigits.sum() * numberOfLines - staticGridSum

                val indexesInLines =
                    cage.cells.mapIndexedNotNull { index, cell ->
                        if (linePair.any { line -> line.contains(cell) }) {
                            index
                        } else {
                            null
                        }
                    }

                val validPossibles = ValidPossiblesCalculator(grid, cage).calculatePossibles()
                val validPossiblesWithNeededSum =
                    validPossibles.filter {
                        it
                            .filterIndexed { index, _ ->
                                indexesInLines.contains(index)
                            }.sum() == neededSumOfLines
                    }

                if (validPossiblesWithNeededSum.isNotEmpty() && validPossiblesWithNeededSum.size < validPossibles.size) {
                    val reducedPossibles = PossiblesReducer(grid, cage).reduceToPossileCombinations(validPossiblesWithNeededSum)

                    if (reducedPossibles) {
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun calculateSingleCageCoveredByLines(
        grid: Grid,
        lines: Set<GridLine>,
    ): Pair<GridCage?, Int> {
        val cages = lines.map { it.cages() }.flatten().toSet()
        val lineCells = lines.map { it.cells() }.flatten().toSet()

        var singleCageNotCoveredByLines: GridCage? = null
        var staticGridSum = 0

        val cagesWithSinglePossibleSet =
            cages
                .filter { it.cells.all { it in lineCells } }
                .associateWith {
                    ValidPossiblesCalculator(grid, it)
                        .calculatePossibles()
                        .map { it.toSet() }
                        .distinct()
                        .filter { it.size == 1 }
                        .first()
                }

        forEach { cage ->
            val hasAtLeastOnePossibleInLines =
                cage.cells
                    .filter {
                        lines.any { line -> line.contains(it) }
                    }.any { !it.isUserValueSet }

            if (!StaticSumUtils.hasStaticSumInCells(grid, cage, lineCells)) {
                if (singleCageNotCoveredByLines != null && hasAtLeastOnePossibleInLines) {
                    return Pair(null, 0)
                }

                singleCageNotCoveredByLines = cage
            } else {
                staticGridSum += StaticSumUtils.staticSumInCells(grid, cage, lineCells)
            }
        }

        return Pair(singleCageNotCoveredByLines, staticGridSum)
    }
}
*/
