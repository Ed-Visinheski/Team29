package Kitchen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static Connection connection = null;

    private DatabaseManager() { }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t29";
                String user = "in2033t29_d";
                String password = "m8mHWvcTuXA";
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Database connection successfully established.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}