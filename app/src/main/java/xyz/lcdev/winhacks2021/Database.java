package xyz.lcdev.winhacks2021;


import org.sqlite.JDBC;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static Connection conn;
    public static Connection open() throws SQLException {
        // Kyle to implement properly.
        // Currently just doing sqlite
        if(conn!=null)
            return conn;
        return conn = JDBC.createConnection("jdbc:"+App.app.getProperty("lc.winhacks2021.cockroachdb.connect"),new Properties());
    }
}
