package org.piepmeyer.gauguin.difficulty.human.strategy

/**
 * Scans three adjacent lines to find that each part of cage contained in this lines has a static sum
 * excluding one part of cage. The sum of this part of cages is calculated all enforced by deleting
 * deviant possibles.
 */
class LinesSingleCagePossiblesSumTriple : AbstractLinesSingleCagePossiblesSum(3)
