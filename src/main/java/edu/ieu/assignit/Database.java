package edu.ieu.assignit;

import java.io.File;
import java.sql.*;

public class Database {
    private static Database instance;
    Connection connection;
    private String path;

    // singleton
    private Database() {
    }
    
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // connect to the path, create assignment config by executing SQL, then disconnect
    public void createAssignmentConfig(String path) throws SQLException {
        this.path = path;
        File file = new File(path);
        boolean noSavedConfig = !file.exists();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);

            if (noSavedConfig) {
                Statement stat = connection.createStatement();
                stat.executeUpdate(" CREATE TABLE config_table (ID int,COMPILER_PATH varchar(255),ASSIGNMENT_PATH varchar(255),ARGS varchar(255),EXPECTED varchar(255),RUN_COMMAND varchar(255),SELECTED_LANGUAGE varchar(255));");
            }
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
        }
    }
}
