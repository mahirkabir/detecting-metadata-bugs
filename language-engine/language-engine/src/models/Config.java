package models;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public class Rules {
        public String dir;
        public List<String> run;

        public Rules() {
            this.run = new ArrayList<>();
        }
    }

    public Rules rules;
    private static Config singletonInstance;

    private Config() {
        rules = new Rules();
    }

    public static Config getInstance() {
        if (singletonInstance == null)
            singletonInstance = new Config();
        return singletonInstance;
    }
}
