package org.piepmeyer.gauguin

import kotlin.time.Duration

object Utils {
    fun displayableGameDuration(gameDuration: Duration): String {
        return gameDuration.toComponents { hours, minutes, seconds, _ ->
            if (hours == 0L) {
                String.format("%02d:%02d", minutes, seconds)
            } else {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            }
        }
    }
}
