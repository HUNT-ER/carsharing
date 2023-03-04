package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {

        Session session;
        if ("-databaseFileName".equals(args[0])) {
            session = new Session(args[1]);
        } else {
            session = new Session("public");
        }
    }
}