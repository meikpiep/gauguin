package com.holokenmod;

public class GameVariant {
    public static final GameVariant INSTANCE = new GameVariant();

    public boolean mDupedigits;
    public boolean mBadMaths;
    private boolean showOperators;

    static GameVariant getInstance() {
        return INSTANCE;
    }

    boolean showDupedDigits() {
        return ApplicationPreferences.getInstance().showDupedDigits();
    }

    boolean showBadMaths() {
        return ApplicationPreferences.getInstance().showBadMaths();
    }

    void setShowOperators(boolean showOperators) {
        this.showOperators = showOperators;
    }

    public boolean showOperators() {
        return showOperators;
    }
}