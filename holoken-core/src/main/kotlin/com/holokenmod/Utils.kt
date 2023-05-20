package com.holokenmod

object Utils {
    @JvmStatic
    fun convertTimetoStr(time: Long): String {
        var seconds = (time / 1000).toInt()
        val minutes = seconds / 60 % 60
        val hours = seconds / 3600
        seconds %= 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
