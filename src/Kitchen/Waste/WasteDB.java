package Kitchen.Waste;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class WasteDB {
    private Connection connection;

    public WasteDB() {
        Connect();
    }
    public void Connect() {
        String url = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t29";
        String user = "in2033t29_a";
        String password = "NvG2lCOEy_g";

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void Disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from the database");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void main(String[] args) {
        WasteDB connector = new WasteDB();
        connector.Connect();
        // launch(args);
    }
}
