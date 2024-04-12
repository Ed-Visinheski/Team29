package Kitchen.Stock;

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

public class StockController extends Application {

    @FXML private Label stockLabel;
    @FXML private TableView<Stock> stockTableView;
    @FXML private TableColumn<Stock, Integer> ingredientIDColumn;
    @FXML private TableColumn<Stock, Integer> stockLevel;
    @FXML private TableColumn<Stock, Integer> stockThreshold;
    @FXML private TableColumn<Stock, Date> deliveryArrivalDate;
    @FXML private TextField txtIngredientID;
    @FXML private TextField txtStockLevel;
    @FXML private TextField textStockThreshold;
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
        primaryStage.setTitle("Stock Management Interface");
        primaryStage.show();

        // Get the controller associated with the FXML file
        //  MainUIController mainUIController = loader.getController();
        //  mainUIController.setWasteController(this);

        // Initialize the TableView columns
        //  initializeColumns();

        // Load stock data into TableView
        loadData();
    }


    public void loadData() {
        // Establish database connection
        StockDB stockDB = new StockDB();
        stockDB.Connect();
        Connection connection = stockDB.getConnection();

        // Load stock data into TableView
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                String query = "SELECT * FROM StockTracking";
                ResultSet resultSet = statement.executeQuery(query);

                // Clear existing items in the TableView
                stockTableView.getItems().clear();

                while (resultSet.next()) {
                    int ingredientID = resultSet.getInt("ingredientID");
                    int stockLevel = resultSet.getInt("stockLevel");
                    int stockThreshold = resultSet.getInt("stockThreshold");
                    Date deliveryArrivalDate = resultSet.getDate("deliveryArrivalDate");

                    // Create a Stock object with retrieved data
                    Stock stock = new Stock(ingredientID, stockLevel, stockThreshold, deliveryArrivalDate);

                    // Add Waste object to TableView
                    stockTableView.getItems().clear();
                }

                // Close statement and result set
                preparedStatement.close();
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error retrieving waste data: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Disconnect from the database
                stockDB.Disconnect();
            }
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}