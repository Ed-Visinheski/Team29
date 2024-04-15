package KitchenToFOH;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;

public class KitchToFOH implements KitchenToFOH {

    @Override
    public MenuAvailability getCurrentMenuAvailability(LocalDate date) {
        HashMap<Dish, Boolean> dishAvailable = new HashMap<>();
        try {
            Connection conn = Kitchen.DatabaseManager.getConnection();
            // Properly joins the tables to fetch the required details
            String query = "SELECT d.dishID, d.dishTime, cd.dishAvailability " +
                    "FROM Menu m " +
                    "JOIN Course c ON m.menuID = c.menuID " +
                    "JOIN CourseDish cd ON c.courseID = cd.courseID " +
                    "JOIN Dish d ON cd.dishID = d.dishID " +
                    "WHERE m.menuDate = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int dishID = rs.getInt("dishID");
                boolean dishAvailability = rs.getBoolean("dishAvailability");
                String dishTime = rs.getString("dishTime");
                System.out.println(dishID + " " + dishAvailability + " " + dishTime);
                Dish dish = new Dish(dishID, dishTime);  // Assuming constructor Dish(int, String)
                dishAvailable.put(dish, dishAvailability);
            }
            return new MenuAvailability(date, dishAvailable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Menu not found on specified Date");
    }

    public static void main(String[] args) {
        KitchToFOH kitchToFOH = new KitchToFOH();
        LocalDate date = LocalDate.of(2024, 4, 15);
        System.out.println(kitchToFOH.getCurrentMenuAvailability(date));
    }
}
