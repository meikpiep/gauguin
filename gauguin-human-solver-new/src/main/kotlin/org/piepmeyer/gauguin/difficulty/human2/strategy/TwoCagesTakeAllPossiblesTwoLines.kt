package org.piepmeyer.gauguin.difficulty.human2.strategy

/**
 * Detects if there are two adjacent lines where two cages have possible combinations with
 * exactly two occurrences of one possible. Then, delete this possible from all other cells of these
 * lines.
 */
class TwoCagesTakeAllPossiblesTwoLines : AbstractTwoCagesTakeAllPossibles(2)
