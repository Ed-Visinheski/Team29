package Kitchen;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static HikariDataSource dataSource;

    static {
        // Initialize the connection pool using HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2033t29");
        config.setUsername("in2033t29_a");
        config.setPassword("NvG2lCOEy_g");

        // Optional: Configure additional HikariCP settings
        config.setMaximumPoolSize(10); // Maximum number of connections in the pool
        config.setMinimumIdle(5); // Minimum number of idle connections HikariCP tries to maintain
        config.setIdleTimeout(300000); // 300 seconds (5 minutes) idle time before a connection is retired
        config.setConnectionTimeout(30000); // 30 seconds to get a connection from the pool before timing out

        // Setting up a pool name for easier debugging or monitoring
        config.setPoolName("MyAppPool");

        // Initialize the data source with configuration
        dataSource = new HikariDataSource(config);
    }

    private DatabaseManager() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        // Get a connection from the pool
        return dataSource.getConnection();
    }

    public static void closeConnection() {
        if (dataSource != null) {
            dataSource.close(); // Close the data source and hence all pooled connections
            System.out.println("Database connection pool closed.");
        }
    }
}
