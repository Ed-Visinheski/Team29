package Kitchen.Waste;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
//import java.util.Date;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ResourceBundle;

public class WasteController extends Application {

    @FXML private Label wasteLabel;
    @FXML private TableView<Waste> wasteTableView;
    @FXML private TableColumn<Waste, Integer> wasteIDColumn;
    @FXML private TableColumn<Waste, Integer> ingredientIDColumn;
    @FXML private TableColumn<Waste, Integer> quantityWastedColumn;
    @FXML private TableColumn<Waste, String> reasonColumn;
    @FXML private TableColumn<Waste, Date> dateWastedColumn;
    @FXML private TextField txtWasteID;
    @FXML private TextField txtIngredientID;
    @FXML private TextField txtQuantityWasted;
    @FXML private TextField txtReason;
    @FXML private DatePicker datePicker;
    private Connection connection;
    private PreparedStatement preparedStatement;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for the main page
        FXMLLoader loader = FXMLLoader.load(getClass().getResource("../MainPageUI.fxml"));
        Parent root = loader.load();

        // Set up the scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Waste Management Interface");
        primaryStage.show();

        // Get the controller associated with the FXML file
        //  MainUIController mainUIController = loader.getController();
        //  mainUIController.setWasteController(this);

        // Initialize the TableView columns
      //  initializeColumns();

        // Load waste data into TableView
        loadDBData();
    }


    public void loadData() {
        // Establish database connection
        WasteDB wasteDB = new WasteDB();
        wasteDB.Connect();
        Connection connection = wasteDB.getConnection();

        // Load waste data into TableView
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                String query = "SELECT * FROM WasteManagement";
                ResultSet resultSet = statement.executeQuery(query);

                // Clear existing items in the TableView
                wasteTableView.getItems().clear();

                while (resultSet.next()) {
                    int wasteID = resultSet.getInt("wasteID");
                    int ingredientID = resultSet.getInt("ingredientID");
                    int quantityWasted = resultSet.getInt("quantityWasted");
                    String reason = resultSet.getString("reason");
                    Date dateWasted = resultSet.getDate("dateWasted");

                    // Create a Waste object with retrieved data
                    Waste waste = new Waste(wasteID, ingredientID, quantityWasted, reason, dateWasted);

                    // Add Waste object to TableView
                    wasteTableView.getItems().clear();
                }

                // Close statement and result set
                preparedStatement.close();
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error retrieving waste data: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Disconnect from the database
                wasteDB.Disconnect();
            }
        }
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
                wasteTableView.getItems().add(waste);
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
        launch(args);
    }
}
