package com.holokenmod;

public enum GridBorderType {
    BORDER_NONE,
    BORDER_SOLID,
    BORDER_WARN,
    BORDER_CAGE_SELECTED;

    public boolean isHighlighted() {
        return this == BORDER_WARN || this == BORDER_CAGE_SELECTED;
    }
}
