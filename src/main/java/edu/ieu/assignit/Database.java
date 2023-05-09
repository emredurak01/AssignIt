package edu.ieu.assignit;

import java.io.File;
import java.sql.*;

public class Database {


    private Connection conn;

    public Database() {


        File file = new File("assignitdb.db");
        boolean firstRun = !file.exists();
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:assignitdb.db");


            if (firstRun) {
                Statement stat = conn.createStatement();
                stat.executeUpdate(" CREATE TABLE config_table (ID integer primary key autoincrement NOT NULL,COMPILER_PATH varchar(255),ASSIGNMENT_PATH varchar(255),ARGS varchar(255),EXPECTED varchar(255));");
            }


        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
        }



    }
    public void insertConfig(final Config config) throws SQLException{
        final PreparedStatement statement = conn.prepareStatement("insert into config_table " +
                "(COMPILER_PATH, " +
                "ASSIGNMENT_PATH, " +
                "ARGS, " +
                "EXPECTED) " +
                "values (?, ?, ?, ?) ");
                statement.setString(1,config.COMPILER_PATH);
                statement.setString(2,config.ASSIGNMENT_PATH);
                statement.setString(3,config.ARGS);
                statement.setString(4,config.EXPECTED);

            statement.executeUpdate();

    }
}