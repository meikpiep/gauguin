package com.holokenmod.options;

public class GameVariant {
    private static final GameVariant INSTANCE = new GameVariant();

    private boolean showOperators;

    public static GameVariant getInstance() {
        return INSTANCE;
    }

    public boolean showDupedDigits() {
        return ApplicationPreferences.getInstance().showDupedDigits();
    }

    public boolean showBadMaths() {
        return ApplicationPreferences.getInstance().showBadMaths();
    }

    public void setShowOperators(final boolean showOperators) {
        this.showOperators = showOperators;
    }

    public boolean showOperators() {
        return showOperators;
    }
}