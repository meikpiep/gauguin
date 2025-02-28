package org.piepmeyer.gauguin.game.save

import kotlinx.serialization.Serializable

@Serializable
data class SavedGridVersion(
    val version: Int = 1,
)
