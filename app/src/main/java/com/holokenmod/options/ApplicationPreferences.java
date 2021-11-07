package com.holokenmod.options;

import android.content.SharedPreferences;

import com.holokenmod.Theme;

public class ApplicationPreferences {
    private static final ApplicationPreferences INSTANCE = new ApplicationPreferences();

    private SharedPreferences preferences;

    public static ApplicationPreferences getInstance() {
        return INSTANCE;
    }

    public Theme getTheme() {
        final String themePref = this.preferences.getString("theme", Theme.LIGHT.name());
        return Theme.valueOf(themePref);
    }

    public void setPreferenceManager(final SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean showDupedDigits() {
        return preferences.getBoolean("duplicates", true);
    }

    public boolean showBadMaths() {
        return preferences.getBoolean("badmaths", true);
    }

    public boolean showOperators() {
        return Boolean.parseBoolean(preferences.getString("defaultshowop", "true"));
    }

    public boolean removePencils() {
        return preferences.getBoolean("removepencils", false);
    }

    public SharedPreferences getPrefereneces() {
        return preferences;
    }

    public GridCageOperation getOperations() {
        final String operations = preferences.getString("mathmode", GridCageOperation.OPERATIONS_ALL.name());
        return GridCageOperation.valueOf(operations);
    }

    public SingleCageUsage getSingleCageUsage() {
        final String usage = preferences.getString("singlecages", SingleCageUsage.FIXED_NUMBER.name());
        return SingleCageUsage.valueOf(usage);
    }


    public DigitSetting getDigitSetting() {
        final String usage = preferences.getString("digits", DigitSetting.FIRST_DIGIT_ONE.name());
        return DigitSetting.valueOf(usage);
    }


    public boolean show3x3Pencils() {
        return preferences.getBoolean("pencil3x3", true);
    }
}