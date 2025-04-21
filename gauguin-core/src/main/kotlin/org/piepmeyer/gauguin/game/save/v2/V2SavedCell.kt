package org.piepmeyer.gauguin.game.save.v2

import kotlinx.serialization.Serializable

@Serializable
data class V2SavedCell(
    val cellNumber: Int,
    val row: Int,
    val column: Int,
    val value: Int,
    val userValue: Int,
    val possibles: Set<Int>,
)
