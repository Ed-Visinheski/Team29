package KitchenToManagement;

import Kitchen.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Implementation of the KitchenToManagement interface for retrieving menu information from the database.
 */
public class KitchToMan implements KitchenToManagement {
    private Kitchen.Menu menu;

    /**
     * Retrieves the menu for a specified date from the database.
     *
     * @param date The date for which the menu is requested.
     * @return The menu for the specified date.
     * @throws IllegalArgumentException If the menu is not found for the specified date.
     */
    @Override
    public Kitchen.Menu getMenuOnDate(LocalDate date) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Menu WHERE menuDate = ?")) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Extract menu details from the result set
                int menuID = rs.getInt("menuId");
                String menuName = rs.getString("menuName");
                String menuDescription = rs.getString("menuDescription");
                int preparationTime = rs.getInt("preparationTime");
                LocalDate menuDate = rs.getDate("menuDate").toLocalDate();
                // Create a new Menu object with the retrieved details
                menu = new Kitchen.Menu(menuID, menuName, menuDescription, preparationTime, menuDate);
                return menu;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // If menu is not found for the specified date, throw an exception
        throw new IllegalArgumentException("Menu not found on specified Date");
    }
}
