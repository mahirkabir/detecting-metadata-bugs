package models;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static Config singletonInstance;
    public List<RuleSet> rulesets;

    private Config() {
        rulesets = new ArrayList<RuleSet>();
    }

    public static Config getInstance() {
        if (singletonInstance == null)
            singletonInstance = new Config();
        return singletonInstance;
    }
}
