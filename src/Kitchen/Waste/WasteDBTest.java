package Kitchen.Waste;

import java.sql.*;

public class WasteDBTest {

    public WasteDBTest() {
        loadDBData();
    }

    public void loadDBData(){
        try {
            Connection connection = Kitchen.DatabaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM WasteManagement");

            while (resultSet.next()) {
                int wasteID = resultSet.getInt("wasteID");
                int ingredientID = resultSet.getInt("ingredientID");
                int quantityWasted = resultSet.getInt("quantityWasted");
                String reason = resultSet.getString("reason");
                Date dateWasted = resultSet.getDate("dateWasted");

                System.out.println("Waste ID: " + wasteID + ", Ingredient ID: " + ingredientID + ", Quantity Wasted: " + quantityWasted + ", Reason: " + reason + ", Date Wasted: " + dateWasted);
                // Create a Waste object with retrieved data
                Waste waste = new Waste(wasteID, ingredientID, quantityWasted, reason, dateWasted);

                // Add Waste object to TableView
            }

            // Close statement and result set
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving waste data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WasteDBTest connector = new WasteDBTest();
    }
}
