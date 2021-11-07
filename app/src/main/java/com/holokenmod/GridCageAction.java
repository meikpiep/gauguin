package com.holokenmod;

public enum GridCageAction {

    ACTION_NONE(0),
    ACTION_ADD(1),
    ACTION_SUBTRACT(2),
    ACTION_MULTIPLY(3),
    ACTION_DIVIDE(4);

    private final int id;

    GridCageAction(final int id) {
        this.id = id;
    }

    public static GridCageAction getById(final int id) {
        for(final GridCageAction action : values()) {
            if (action.id == id) {
                return action;
            }
        }

        return null;
    }
}
