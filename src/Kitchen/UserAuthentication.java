package Kitchen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The UserAuthentication class provides methods for user authentication and retrieval of chef ID.
 */
public class UserAuthentication {
    private String username; // Stores the username of the authenticated user

    /**
     * Checks if the provided username and password match any user in the database.
     *
     * @param inputUsername The username provided by the user for authentication.
     * @param inputPassword The password provided by the user for authentication.
     * @return True if the username and password match a user in the database, otherwise false.
     */
    public boolean checkUserCredentials(String inputUsername, String inputPassword) {
        this.username = inputUsername; // Store the provided username
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Get a connection to the database using DatabaseManager
            conn = DatabaseManager.getConnection();

            // SQL query to check if the username and password match
            String sql = "SELECT * FROM ChefAccount WHERE accountUsername = ? AND accountPassword = ?";

            // Create a PreparedStatement to prevent SQL injection
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, inputUsername); // Set the first parameter as the input username
            pstmt.setString(2, inputPassword); // Set the second parameter as the input password

            // Execute the query
            rs = pstmt.executeQuery();

            // Check if the user exists
            if (rs.next()) {
                System.out.println("Login Successful: User found with matching username and password.");
                return true; // Return true if user exists
            } else {
                System.out.println("Login Failed: No matching user with that username and password.");
                return false; // Return false if user doesn't exist
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources, excluding the connection (managed by DatabaseManager)
            try {
                if (rs != null) rs.close(); // Close ResultSet
                if (pstmt != null) pstmt.close(); // Close PreparedStatement
                // Connection is not closed here as it's managed by DatabaseManager
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false; // Return false by default if an exception occurs
    }

    /**
     * Retrieves the chef ID of the authenticated user.
     *
     * @return The chef ID of the authenticated user.
     */
    public int getChefID() {
        int chefID = -1; // Initialize chef ID with a default value
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection(); // Get a connection to the database
            String sql = "SELECT chefID FROM ChefAccount WHERE accountUsername = ?"; // SQL query to retrieve chef ID
            pstmt = conn.prepareStatement(sql); // Create a PreparedStatement
            pstmt.setString(1, username); // Set the parameter as the username
            rs = pstmt.executeQuery(); // Execute the query
            if (rs.next()) {
                chefID = rs.getInt("chefID"); // Retrieve chef ID from the result set
            }
            return chefID; // Return the retrieved chef ID
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chefID; // Return the default chef ID if an exception occurs
    }
}
