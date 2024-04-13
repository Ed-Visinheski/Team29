package Kitchen;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static HikariDataSource dataSource;

    static {
        try {
            // Initialize the connection pool using HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t29");
            config.setUsername("in2033t29_a");
            config.setPassword("NvG2lCOEy_g");

            // Optional: Configure additional HikariCP settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(30000);
            config.setPoolName("MyAppPool");

            // Setting a connection test query
            config.setConnectionTestQuery("SELECT 1");

            // Initialize the data source with configuration
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            // Log the exception or handle it accordingly
            System.err.println("Error initializing the database pool: " + e.getMessage());
            throw new RuntimeException("Failed to initialize the database pool", e);
        }
    }

    private DatabaseManager() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        // Get a connection from the pool
        return dataSource.getConnection();
    }

    public static void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close(); // Close the data source and hence all pooled connections
            System.out.println("Database connection pool closed.");
        }
    }
}
