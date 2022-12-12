package models;

public class IntegerItem {
    private Integer num;

    public IntegerItem(Integer num) {
        super();
        this.num = num;
    }

    public Integer getValue() {
        return this.num;
    }

    public void setValue(Integer num) {
        this.num = num;
    }

    public boolean equals(IntegerItem cmp) {
        return this.num == cmp.num;
    }
}
