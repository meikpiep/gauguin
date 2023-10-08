package com.holokenmod

import kotlin.time.Duration

object Utils {
    fun displayableGameDuration(gameDuration: Duration): String {
        return gameDuration.toComponents { hours, minutes, seconds, _ ->
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }
}
