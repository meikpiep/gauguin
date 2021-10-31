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

    public GridCageOperation getOperations() {
        String operations = preferences.getString("mathmode", GridCageOperation.OPERATIONS_ALL.name());
        return GridCageOperation.valueOf(operations);
    }

    public SingleCageUsage getSingleCageUsage() {
        String usage = preferences.getString("singlecages", SingleCageUsage.FIXED_NUMBER.name());
        return SingleCageUsage.valueOf(usage);
    }

    public boolean show3x3Pencils() {
        return preferences.getBoolean("pencil3x3", true);
    }
}