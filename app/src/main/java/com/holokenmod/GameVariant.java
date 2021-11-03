package com.holokenmod;

import java.util.ArrayList;
import java.util.Collection;

public class GameVariant {
    private static final GameVariant INSTANCE = new GameVariant();

    public boolean mDupedigits;
    public boolean mBadMaths;
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

    public void setShowOperators(boolean showOperators) {
        this.showOperators = showOperators;
    }

    public boolean showOperators() {
        return showOperators;
    }
}