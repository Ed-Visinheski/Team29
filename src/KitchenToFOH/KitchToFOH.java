package KitchenToFOH;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * The KitchToFOH class implements the KitchenToFOH interface to provide functionality
 * for retrieving menu availability information from the kitchen to the front of house.
 */
public class KitchToFOH implements KitchenToFOH {

    /**
     * Retrieves the availability of dishes for the current menu on the specified date.
     *
     * @param date The date for which menu availability is requested.
     * @return MenuAvailability object containing dish availability information for the specified date.
     * @throws IllegalArgumentException if the menu is not found on the specified date.
     */
    @Override
    public MenuAvailability getCurrentMenuAvailability(LocalDate date) {
        // HashMap to store dish availability
        HashMap<Dish, Boolean> dishAvailable = new HashMap<>();
        try {
            Connection conn = Kitchen.DatabaseManager.getConnection(); // Get database connection
            // SQL query to fetch dish availability information
            String query = "SELECT d.dishID, d.dishTime, cd.dishAvailability " +
                    "FROM Menu m " +
                    "JOIN Course c ON m.menuID = c.menuID " +
                    "JOIN CourseDish cd ON c.courseID = cd.courseID " +
                    "JOIN Dish d ON cd.dishID = d.dishID " +
                    "WHERE m.menuDate = ?";
            PreparedStatement stmt = conn.prepareStatement(query); // Prepare SQL statement
            stmt.setDate(1, java.sql.Date.valueOf(date)); // Set date parameter
            ResultSet rs = stmt.executeQuery(); // Execute query
            // Iterate through result set and populate dish availability map
            while (rs.next()) {
                int dishID = rs.getInt("dishID"); // Get dish ID from result set
                boolean dishAvailability = rs.getBoolean("dishAvailability"); // Get dish availability from result set
                String dishTime = rs.getString("dishTime"); // Get dish time from result set
                Dish dish = new Dish(dishID, dishTime); // Create new Dish object
                dishAvailable.put(dish, dishAvailability); // Add dish availability to map
            }
            return new MenuAvailability(date, dishAvailable); // Return MenuAvailability object
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace if an exception occurs
        }
        // Throw exception if menu is not found on specified date
        throw new IllegalArgumentException("Menu not found on specified Date");
    }

    /**
     * Main method to demonstrate functionality by fetching and printing current menu availability.
     *
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        KitchToFOH kitchToFOH = new KitchToFOH(); // Create instance of KitchToFOH class
        LocalDate date = LocalDate.of(2024, 4, 15); // Define date for menu availability check
        System.out.println(kitchToFOH.getCurrentMenuAvailability(date)); // Print current menu availability
    }
}

