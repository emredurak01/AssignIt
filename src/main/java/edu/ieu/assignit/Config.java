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
    // for test purposes
    public static void display() {
        System.out.println("COMPILER_PATH: " + Config.getInstance().COMPILER_PATH);
        System.out.println("ASSIGNMENT_PATH: " + Config.getInstance().ASSIGNMENT_PATH);
        System.out.println("ARGS: " + Config.getInstance().ARGS);
        System.out.println("EXPECTED: " + Config.getInstance().EXPECTED);
        System.out.println("RUN_COMMAND: " + Config.getInstance().RUN_COMMAND);
    }
}
