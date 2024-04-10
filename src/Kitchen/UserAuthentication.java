package Kitchen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserAuthentication {
    private String username;

    public boolean checkUserCredentials(String inputUsername, String inputPassword) {
        this.username = inputUsername;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Using DatabaseManager to get the connection
            conn = DatabaseManager.getConnection();

            // SQL query to check if the username and password match
            String sql = "SELECT * FROM ChefAccount WHERE accountUsername = ? AND accountPassword = ?";

            // Using a PreparedStatement to prevent SQL injection
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, inputUsername);
            pstmt.setString(2, inputPassword);

            // Executing the query
            rs = pstmt.executeQuery();

            // Checking if the user exists
            if (rs.next()) {
                System.out.println("Login Successful: User found with matching username and password.");
                return true;
            } else {
                System.out.println("Login Failed: No matching user with that username and password.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Closing resources, but not the connection, as it's managed by DatabaseManager
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                // The connection is not closed here because it's managed by DatabaseManager
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
    public int getChefID() {
        int chefID = -1;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT chefID FROM ChefAccount WHERE accountUsername = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                chefID = rs.getInt("chefID");
            }
            return chefID;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chefID;
    }
}