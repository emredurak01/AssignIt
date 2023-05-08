package edu.ieu.assignit;

import java.io.File;
import java.sql.*;

public class Database {


    public Database() {
        String dbfile = "assignitdb.db";
        Connection conn;
        File file = new File(dbfile);
        boolean firstRun = !file.exists();

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbfile);

            if (firstRun) {
                Statement stat = conn.createStatement();
                stat.executeUpdate(" CREATE TABLE config_table (ID int,COMPILER_PATH varchar(255),ASSIGNMENT_PATH varchar(255),ARGS varchar(255),EXPECTED varchar(255));");
            }


        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
        }

    }
}