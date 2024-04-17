package Kitchen;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages the database connection pool using HikariCP.
 */
public class DatabaseManager {

    // Fields

    /** The Hikari data source for managing database connections. */
    private static HikariDataSource dataSource;

    // Static initialization block

    /**
     * Initializes the connection pool using HikariCP with the specified database configurations.
     */
    static {
        try {
            // Initialize HikariCP configuration
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

    // Private constructor to prevent instantiation

    /**
     * Private constructor to prevent instantiation of the DatabaseManager class.
     */
    private DatabaseManager() {
        // private constructor to prevent instantiation
    }

    // Methods

    /**
     * Retrieves a database connection from the connection pool.
     *
     * @return A database connection.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        // Get a connection from the pool
        return dataSource.getConnection();
    }

    /**
     * Closes the database connection pool.
     */
    public static void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close(); // Close the data source and hence all pooled connections
            System.out.println("Database connection pool closed.");
        }
    }
}
