package com.holokenmod;

import android.content.SharedPreferences;

public class ApplicationPreferences {
    private static final ApplicationPreferences INSTANCE = new ApplicationPreferences();

    private SharedPreferences preferences;

    public static ApplicationPreferences getInstance() {
        return INSTANCE;
    }

    public Theme getTheme() {
        String themePref = this.preferences.getString("theme", Theme.LIGHT.name());
        return Theme.valueOf(themePref);
    }

    public void setPreferenceManager(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean showDupedDigits() {
        return preferences.getBoolean("duplicates", true);
    }

    public boolean showBadMaths() {
        return preferences.getBoolean("badmaths", true);
    }

    public boolean showOperators() {
        return Boolean.valueOf(preferences.getString("defaultshowop", "true"));
    }

    public boolean removePencils() {
        return preferences.getBoolean("removepencils", false);
    }

    public SharedPreferences getPrefereneces() {
        return preferences;
    }
}