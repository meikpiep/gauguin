package org.piepmeyer.gauguin.grid

enum class GridBorderType {
    BORDER_NONE,
    BORDER_SOLID,
    BORDER_WARN,
    BORDER_CAGE_SELECTED,
    ;

    val isHighlighted: Boolean
        get() = this == BORDER_WARN || this == BORDER_CAGE_SELECTED
}
