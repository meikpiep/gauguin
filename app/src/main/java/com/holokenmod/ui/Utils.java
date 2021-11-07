package com.holokenmod.ui;

class Utils {
    static String convertTimetoStr(final long time) {
        int seconds = (int) (time / 1000);
        final int minutes = seconds / 60 % 60;
        final int hours   = seconds / 3600;
        seconds     = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}