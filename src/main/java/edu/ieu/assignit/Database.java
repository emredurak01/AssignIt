package edu.ieu.assignit;

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

    // disconnect if connection is not null, connect to the path, create assignment config by executing SQL
    public void createAssignmentConfig() throws SQLException {
        try {
            Statement stat = connection.createStatement();
            stat.executeUpdate("CREATE TABLE if not exists config_table (ID INTEGER PRIMARY KEY AUTOINCREMENT,COMPILER_PATH varchar(255), ARGS varchar(255),EXPECTED varchar(255),RUN_COMMAND varchar(255),SELECTED_LANGUAGE varchar(255));");
            stat.close();

        } catch (SQLException e) {
            System.err.println(e);
            Application.createAlert(e.getMessage(), "Error");
        }
    }

    public void connect(String path) {
        this.path = path;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (Exception e) {
            System.err.println(e);
            Application.createAlert(e.getMessage(), "Error");
        }
    }


    public void addConfig(String compilerPath, String args, String expected, String runCommand, String selectedLanguage) throws SQLException {
        String sql = "INSERT INTO config_table (COMPILER_PATH, ARGS, EXPECTED, RUN_COMMAND, SELECTED_LANGUAGE) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        // id will always be 1,
        // since there will be only one config in every assignment.
        // So, is id field necessary for this simple task?
        ps.setString(1, compilerPath);
        ps.setString(2, args);
        ps.setString(3, expected);
        ps.setString(4, runCommand);
        ps.setString(5, selectedLanguage);
        ps.executeUpdate();
        ps.close();
    }
    public Config getConfig() throws SQLException {
        Config config = Config.getInstance();

        String sql = "SELECT COMPILER_PATH, ARGS, EXPECTED, RUN_COMMAND, SELECTED_LANGUAGE FROM config_table WHERE ID = 1";
        Statement stat = connection.createStatement();
        ResultSet rs = stat.executeQuery(sql);

        if (rs.next()) {
            config.COMPILER_PATH = rs.getString("COMPILER_PATH");
            config.ARGS = rs.getString("ARGS");
            config.EXPECTED = rs.getString("EXPECTED");
            config.RUN_COMMAND = rs.getString("RUN_COMMAND");
            config.SELECTED_LANGUAGE = Language.valueOf(rs.getString("SELECTED_LANGUAGE"));
        }

        rs.close();
        stat.close();

        return config;
    }

    public void disconnect() throws SQLException {
        connection.close();
    }
}
