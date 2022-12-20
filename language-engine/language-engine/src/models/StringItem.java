package models;

public class StringItem {
    private String str;

    public StringItem() {
        super();
        this.str = "";
    }

    public StringItem(String str) {
        super();
        this.str = str;
    }

    public String getValue() {
        return this.str;
    }

    public void setValue(String str) {
        this.str = str;
    }

    public boolean equals(StringItem cmp) {
        return this.str.equals(cmp.str);
    }
}
