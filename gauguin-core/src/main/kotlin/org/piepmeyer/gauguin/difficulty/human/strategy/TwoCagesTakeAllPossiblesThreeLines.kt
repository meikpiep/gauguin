package org.piepmeyer.gauguin.difficulty.human.strategy

/**
 * Detects if there are three adjacent lines where two cages have possible combinations with
 * exactly two occurances of one possible. Then, delete this possible from all other cells of these
 * lines.
 */
class TwoCagesTakeAllPossiblesThreeLines : AbstractTwoCagesTakeAllPossibles(3)
