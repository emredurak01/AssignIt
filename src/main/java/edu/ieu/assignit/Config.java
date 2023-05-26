package edu.ieu.assignit;

public class Config {
    private static Config instance;
    public Language SELECTED_LANGUAGE = Language.GENERIC;
    public String COMPILER_PATH;
    public String ASSIGNMENT_PATH;
    public String ARGS;
    public String RUN_COMMAND;
    public String EXPECTED;

    private Config() {
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

}
