package KitchenToManagement;

import Kitchen.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KitchToMan implements KitchenToManagement {
    private Kitchen.Menu menu;


    @Override
    public Kitchen.Menu getMenuOnDate(LocalDate date) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Menu WHERE menuDate = ?")) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int menuID = rs.getInt("menuId");
                String menuName = rs.getString("menuName");
                String menuDescription = rs.getString("menuDescription");
                int preparationTime = rs.getInt("preparationTime");
                LocalDate menuDate = rs.getDate("menuDate").toLocalDate();
                menu = new Kitchen.Menu(menuID, menuName, menuDescription, preparationTime, menuDate);
                return menu;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Menu not found on specified Date");
    }

//    public static void main(String[] args) {
//        KitchToMan ktm = new KitchToMan();
//        LocalDate date = LocalDate.parse("2024-04-15", DateTimeFormatter.ISO_LOCAL_DATE);  // Correct date parsing
//        System.out.println(ktm.getMenuOnDate(date));
//    }
}
