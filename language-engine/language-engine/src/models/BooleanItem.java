package models;

import utils.Constants;

public class BooleanItem {
    private boolean b;

    public BooleanItem(boolean b) {
        super();
        this.b = b;
    }

    public boolean getValue() {
        return this.b;
    }

    public void setValue(boolean b) {
        this.b = b;
    }

    public String toString() {
        if (this.b)
            return Constants.BOOLEAN_TRUE;
        return Constants.BOOLEAN_FALSE;
    }

    public boolean equals(BooleanItem cmp) {
        return this.b == cmp.b;
    }
}
