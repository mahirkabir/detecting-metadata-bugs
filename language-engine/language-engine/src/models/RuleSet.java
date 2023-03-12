package models;

import java.util.ArrayList;
import java.util.List;

public class RuleSet {
    public String category;
    public String dir;
    public List<String> run;

    public RuleSet() {
        this.run = new ArrayList<String>();
    }
}